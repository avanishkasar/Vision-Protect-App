package com.example.visionprotect04.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.graphics.RenderEffect
import android.graphics.Shader
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.AudioManager
import android.media.ImageReader
import android.media.ToneGenerator
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import com.example.visionprotect04.MainActivity
import com.example.visionprotect04.R
import com.example.visionprotect04.data.AnalyticsManager
import com.example.visionprotect04.data.EyeHealthRepository
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceLandmark
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

@OptIn(ExperimentalGetImage::class)
class VisionProtectService : LifecycleService(), SensorEventListener {
    companion object {
        private const val FACE_TOO_CLOSE_THRESHOLD = 0.7f // 70% of frame coverage
        private const val FACE_OPTIMAL_THRESHOLD = 0.4f // 40% of frame coverage
        private const val CHECK_INTERVAL = 1000L // 1 second
        private const val MAX_NO_FACE_COUNT = 3
        private const val CHANNEL_ID = "vision_protect_channel"
        private const val NOTIFICATION_ID = 1
        private const val ACTION_PAUSE = "com.example.visionprotect04.PAUSE"
        private const val ACTION_RESUME = "com.example.visionprotect04.RESUME"
        private const val SCREENSHOT_INTERVAL_MS = 2000L // Take screenshot every 2 seconds
        
        // Blink detection thresholds
        private const val EYE_OPEN_PROBABILITY_THRESHOLD = 0.4f
        private const val BLINK_ALERT_THRESHOLD_MS = 10000L // 10 seconds
    }

    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: View
    private lateinit var blurView: ImageView
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var faceDetector: FaceDetector
    private lateinit var wakeLock: PowerManager.WakeLock
    private lateinit var notificationManager: NotificationManager
    private lateinit var handler: Handler
    private lateinit var sensorManager: SensorManager
    private lateinit var analyticsManager: AnalyticsManager
    private var lightSensor: Sensor? = null
    private var toneGenerator: ToneGenerator? = null
    
    private var isScreenFrozen = false
    private var isBlinkAlertActive = false
    private var baselineFaceSize: Float? = null
    private var isBaselineSet = false
    private var lastProcessingTimeMs = 0L
    private var consecutiveNoFaceCount = 0
    private var isPaused = false
    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null
    private val executor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private var lastFaceCoverage: Float = 0f
    private var serviceIntent: Intent? = null
    private var sessionStartTime: Long = 0L

    // Eye Health Tracking
    private val eyeHealthManager = EyeHealthManager()
    private var currentAmbientLux: Float? = null
    private var wasEyeOpen = true
    private var lastBlinkTimestamp: Long = System.currentTimeMillis()

    private val faceCheckRunnable = object : Runnable {
        override fun run() {
            if (!isPaused) {
                captureAndProcessImage()
                checkBlinkStatus()
            }
            handler.postDelayed(this, CHECK_INTERVAL)
        }
    }

    override fun onCreate() {
        super.onCreate()
        
        // Initialize components
        handler = Handler(Looper.getMainLooper())
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        analyticsManager = AnalyticsManager(this)
        sessionStartTime = System.currentTimeMillis()
        
        // Create notification channel
        createNotificationChannel()
        
        // Start as foreground service
        startForeground(NOTIFICATION_ID, createNotification("Starting protection...", false))
        
        // Setup components
        setupWakeLock()
        setupOverlay()
        setupFaceDetection()
        setupLightSensor()
        setupToneGenerator()
    }

    private fun setupToneGenerator() {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupLightSensor() {
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        lightSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    private fun stopLightSensor() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
            currentAmbientLux = event.values[0]
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed
    }

    private fun setupWakeLock() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "VisionProtect::MonitoringWakeLock"
        ).apply {
            acquire(10*60*1000L /*10 minutes*/)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Vision Protect",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Vision Protect Service"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(status: String, isPaused: Boolean): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val toggleIntent = Intent(this, VisionProtectService::class.java).apply {
            action = if (isPaused) ACTION_RESUME else ACTION_PAUSE
        }
        val togglePendingIntent = PendingIntent.getService(
            this, 2, toggleIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, MainActivity::class.java).apply {
            action = "STOP_SERVICE"
        }
        val stopPendingIntent = PendingIntent.getActivity(
            this, 1, stopIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Vision Protect ${if (isPaused) "(Paused)" else "Active"}")
            .setContentText(status)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .addAction(
                if (isPaused) android.R.drawable.ic_media_play else android.R.drawable.ic_media_pause,
                if (isPaused) "Resume" else "Pause",
                togglePendingIntent
            )
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Stop", stopPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        serviceIntent = intent
        when (intent?.action) {
            ACTION_PAUSE -> {
                isPaused = true
                clearScreenBlur()
                resetBlinkAlert()
                updateNotification("Protection paused", true)
            }
            ACTION_RESUME -> {
                isPaused = false
                isBaselineSet = false
                eyeHealthManager.resetSession()
                lastBlinkTimestamp = System.currentTimeMillis()
                updateNotification("Protection resumed", false)
            }
            "STOP_SERVICE" -> {
                stopSelf()
                return START_NOT_STICKY
            }
            else -> {
                // Initial start
                startScreenCapture()
                startCamera()
            }
        }
        return START_STICKY
    }

    private fun updateNotification(status: String, isPaused: Boolean) {
        notificationManager.notify(NOTIFICATION_ID, createNotification(status, isPaused))
    }

    private fun setupOverlay() {
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        overlayView = inflater.inflate(R.layout.overlay_layout, null)
        blurView = overlayView.findViewById(R.id.blurView)

        // Set up the blur effect
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            blurView.setRenderEffect(RenderEffect.createBlurEffect(25f, 25f, Shader.TileMode.DECAL))
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_DIM_BEHIND,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            dimAmount = 1.0f // Make it completely dark
        }

        windowManager.addView(overlayView, params)
        overlayView.visibility = View.GONE
    }

    private fun setupFaceDetection() {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .setMinFaceSize(0.15f)
            .enableTracking()
            .build()

        faceDetector = FaceDetection.getClient(options)
    }

    private fun startCamera() {
        cameraExecutor = Executors.newSingleThreadExecutor()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build()
            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                processImageProxy(imageProxy)
            }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_FRONT_CAMERA,
                    preview,
                    imageAnalysis
                )
                // Start periodic checks
                handler.post(faceCheckRunnable)
            } catch (e: Exception) {
                e.printStackTrace()
                updateNotification("Camera initialization failed", isPaused)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    @OptIn(ExperimentalGetImage::class)
    private fun processImageProxy(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees
            )
            faceDetector.process(image)
                .addOnSuccessListener { faces ->
                    if (faces.isNotEmpty()) {
                        processFaceDetection(faces[0], mediaImage.width, mediaImage.height)
                    } else {
                        handleNoFaceDetected()
                    }
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    updateNotification("Face detection failed", isPaused)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }

    private fun processFaceDetection(face: Face, imageWidth: Int, imageHeight: Int) {
        if (isPaused) return
        
        consecutiveNoFaceCount = 0
        
        // Get face landmarks for more accurate tracking
        val leftEye = face.getLandmark(FaceLandmark.LEFT_EYE)
        val rightEye = face.getLandmark(FaceLandmark.RIGHT_EYE)
        val nose = face.getLandmark(FaceLandmark.NOSE_BASE)
        
        // Blink Detection
        val leftOpen = face.leftEyeOpenProbability
        val rightOpen = face.rightEyeOpenProbability
        if (leftOpen != null && rightOpen != null) {
            val isEyeOpen = (leftOpen > EYE_OPEN_PROBABILITY_THRESHOLD) || (rightOpen > EYE_OPEN_PROBABILITY_THRESHOLD)
            if (wasEyeOpen && !isEyeOpen) {
                // Blink detected
                eyeHealthManager.registerBlink()
                lastBlinkTimestamp = System.currentTimeMillis()
                resetBlinkAlert()
            }
            wasEyeOpen = isEyeOpen
        }

        // Calculate face coverage with improved accuracy
        val frameCoverage = calculateFaceCoverage(face, imageWidth, imageHeight)
        
        // Update Eye Health Score
        val score = eyeHealthManager.calculateScore(
            faceCoverage = frameCoverage,
            ambientLux = currentAmbientLux,
            headEulerAngleZ = face.headEulerAngleZ
        )
        EyeHealthRepository.updateScore(score)

        // Additional checks for face validity
        if (!isFaceValid(face, leftEye, rightEye, nose)) {
            handleNoFaceDetected()
            return
        }

        // If face is too close (covers too much of the frame)
        if (frameCoverage > FACE_TOO_CLOSE_THRESHOLD) {
            if (!isScreenFrozen) {
                freezeScreen()
                updateNotification("Too close to screen", isPaused)
            }
        }
        // If face is at optimal distance
        else if (frameCoverage <= FACE_OPTIMAL_THRESHOLD) {
            if (isScreenFrozen) {
                unfreezeScreen()
                updateNotification("Optimal distance", isPaused)
            }
            isBaselineSet = true
        }
        // Otherwise we just need to update our baseline measurement
        else {
            if (!isBaselineSet) {
                setBaselineFaceSize(frameCoverage)
            }
            if (isScreenFrozen) {
                unfreezeScreen()
            }
        }

        lastFaceCoverage = frameCoverage
        lastProcessingTimeMs = System.currentTimeMillis()
    }

    private fun checkBlinkStatus() {
        if (isPaused || isScreenFrozen) return

        val timeSinceLastBlink = System.currentTimeMillis() - lastBlinkTimestamp
        if (timeSinceLastBlink > BLINK_ALERT_THRESHOLD_MS) {
            triggerBlinkAlert()
        }
    }

    private fun triggerBlinkAlert() {
        if (!isBlinkAlertActive) {
            isBlinkAlertActive = true
            handler.post {
                overlayView.visibility = View.VISIBLE
                blurView.setImageDrawable(null) // Clear any blur image
                overlayView.setBackgroundColor(0x4D000000.toInt()) // Semi-transparent black (30% alpha)
                
                // Pulse animation
                overlayView.alpha = 0f
                overlayView.animate()
                    .alpha(1f)
                    .setDuration(500)
                    .withEndAction {
                        overlayView.setBackgroundColor(0) // Reset background
                    }
                    .start()
                
                // Play sound
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP)
            }
        }
    }

    private fun resetBlinkAlert() {
        if (isBlinkAlertActive) {
            isBlinkAlertActive = false
            handler.post {
                overlayView.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .withEndAction {
                        if (!isScreenFrozen) {
                            overlayView.visibility = View.GONE
                        }
                        overlayView.setBackgroundColor(0) // Reset background
                    }
                    .start()
            }
        }
    }

    private fun isFaceValid(face: Face, leftEye: FaceLandmark?, rightEye: FaceLandmark?, nose: FaceLandmark?): Boolean {
        // Check if essential landmarks are detected
        if (leftEye == null || rightEye == null || nose == null) {
            return false
        }

        }

        // Check if nose is between eyes (roughly)
        if (nosePos.x < min(leftEyePos.x, rightEyePos.x) || 
            nosePos.x > max(leftEyePos.x, rightEyePos.x)) {
            return false
        }

        return true
    }

    private fun calculateFaceCoverage(face: Face, imageWidth: Int, imageHeight: Int): Float {
        val boundingBox = face.boundingBox
        
        // Get face landmarks for more accurate coverage calculation
        val leftEye = face.getLandmark(FaceLandmark.LEFT_EYE)
        val rightEye = face.getLandmark(FaceLandmark.RIGHT_EYE)
        
        if (leftEye != null && rightEye != null) {
            // Calculate inter-eye distance
            val eyeDistance = sqrt(
                (rightEye.position.x - leftEye.position.x).pow(2) +
                (rightEye.position.y - leftEye.position.y).pow(2)
            )
            
            // Estimate face size based on inter-eye distance (typical ratio is 1:3)
            val estimatedFaceWidth = eyeDistance * 3
            val estimatedFaceHeight = eyeDistance * 4
            
            // Calculate coverage using estimated face size
            val faceArea = estimatedFaceWidth * estimatedFaceHeight
            val imageArea = imageWidth * imageHeight
            return faceArea / imageArea
        }
        
        // Fallback to bounding box calculation if landmarks are not available
        val faceArea = boundingBox.width() * boundingBox.height()
        val imageArea = imageWidth * imageHeight
        return faceArea.toFloat() / imageArea.toFloat()
    }

    private fun handleNoFaceDetected() {
        consecutiveNoFaceCount++
        if (consecutiveNoFaceCount >= MAX_NO_FACE_COUNT && isScreenFrozen) {
            unfreezeScreen()
            updateNotification("No face detected", isPaused)
        }
    }

    private fun freezeScreen() {
        if (!isScreenFrozen) {
            isScreenFrozen = true
            // Ensure blink alert is cleared if we are freezing screen
            resetBlinkAlert()
            
            handler.post {
                overlayView.visibility = View.VISIBLE
                overlayView.setBackgroundColor(0) // Reset background
                blurView.setImageResource(R.drawable.blur_overlay)
                overlayView.alpha = 0f
                
                // Animate Fade In
                overlayView.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .start()
                    
                // Animate Shield Pulse
                val shieldContainer = overlayView.findViewById<View>(R.id.shieldContainer)
                if (shieldContainer != null) {
                    shieldContainer.alpha = 0f
                    shieldContainer.scaleX = 0.8f
                    shieldContainer.scaleY = 0.8f
                    
                    shieldContainer.animate()
                        .alpha(1f)
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(400)
                        .setInterpolator(android.view.animation.OvershootInterpolator())
                        .start()
                }

                toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP)
            }
        }
    }

    private fun unfreezeScreen() {
        if (isScreenFrozen) {
            isScreenFrozen = false
            handler.post {
                // Animate Fade Out
                overlayView.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .withEndAction {
                        overlayView.visibility = View.GONE
                    }
                    .start()
            }
        }
    }

    private fun captureAndProcessImage() {
        // This method is called every CHECK_INTERVAL
        // The actual capture and processing is handled by the CameraX analyzer
    }

    private fun startScreenCapture() {
        val mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val resultCode = serviceIntent?.getIntExtra("resultCode", -1) ?: return
        val resultData = serviceIntent?.getParcelableExtra<Intent>("data") ?: return

        mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, resultData)
        setupVirtualDisplay()

        // Start periodic screenshot capture
        executor.scheduleAtFixedRate({
            captureScreenshot()
        }, 0, SCREENSHOT_INTERVAL_MS, TimeUnit.MILLISECONDS)
    }

    private fun setupVirtualDisplay() {
        val metrics = resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        val density = metrics.densityDpi

        imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2)
        virtualDisplay = mediaProjection?.createVirtualDisplay(
            "ScreenCapture",
            width,
            height,
            density,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader?.surface,
            null,
            null
        )
    }

    private fun captureScreenshot() {
        val bitmap = imageReader?.acquireLatestImage()?.let { image ->
            val planes = image.planes
            val buffer = planes[0].buffer
            val pixelStride = planes[0].pixelStride
            val rowStride = planes[0].rowStride
            val rowPadding = rowStride - pixelStride * image.width

            val bitmap = Bitmap.createBitmap(
                image.width + rowPadding / pixelStride,
                image.height,
                Bitmap.Config.ARGB_8888
            )
            bitmap.copyPixelsFromBuffer(buffer)
            image.close()
            bitmap
        }

        // Analyze the screenshot and update screen state
        bitmap?.let { analyzeScreenshot(it) }
    }

    private fun analyzeScreenshot(bitmap: Bitmap) {
        // TODO: Send bitmap to AI for analysis
        // For now, we'll use a mock analysis
        val faceCoverage = 0.6f // Mock value from AI analysis
        
        handler.post {
            updateScreenState(faceCoverage)
        }
    }

    private fun updateScreenState(faceCoverage: Float) {
        lastFaceCoverage = faceCoverage
        if (faceCoverage > 0.5f && !isScreenFrozen) {
            freezeScreen()
        } else if (faceCoverage <= 0.5f && isScreenFrozen) {
            unfreezeScreen()
        }
    }

    private fun clearScreenBlur() {
        if (isScreenFrozen) {
            isScreenFrozen = false
            handler.post {
                overlayView.visibility = View.GONE
            }
        }
    }

    private fun setBaselineFaceSize(frameCoverage: Float) {
        baselineFaceSize = frameCoverage
        isBaselineSet = true
    }

    override fun onDestroy() {
        super.onDestroy()
        
        // Save Session Data to Analytics
        val endTime = System.currentTimeMillis()
        val lastScore = EyeHealthRepository.eyeHealthScore.value
        if (lastScore != null) {
            analyticsManager.saveSession(sessionStartTime, endTime, lastScore)
        }
        
        // Cleanup
        stopLightSensor()
        toneGenerator?.release()
        handler.removeCallbacks(faceCheckRunnable)
        if (::wakeLock.isInitialized && wakeLock.isHeld) {
            wakeLock.release()
        }
        cameraExecutor.shutdown()
        try {
            cameraExecutor.awaitTermination(1000, TimeUnit.MILLISECONDS)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        windowManager.removeView(overlayView)
        executor.shutdown()
        virtualDisplay?.release()
        mediaProjection?.stop()
        imageReader?.close()
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }
}