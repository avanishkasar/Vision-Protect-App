package com.example.visionprotect04.ui.components

import android.graphics.Rect
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.util.concurrent.Executors

@OptIn(ExperimentalGetImage::class)
@Composable
fun CameraPreview(
    onFaceDetected: (Face, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var detectedFaces by remember { mutableStateOf<List<Face>>(emptyList()) }
    var previewSize by remember { mutableStateOf(Size(0, 0)) }
    val previewView = remember { PreviewView(context) }
    val executor = remember { Executors.newSingleThreadExecutor() }
    val cameraProviderFuture = remember { androidx.camera.lifecycle.ProcessCameraProvider.getInstance(context) }

    DisposableEffect(Unit) {
        previewView.implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        
        val listener = cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build()
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            val faceDetector = FaceDetection.getClient(
                FaceDetectorOptions.Builder()
                    .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                    .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
                    .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
                    .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
                    .setMinFaceSize(0.2f)
                    .build()
            )

            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(720, 1280))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .apply {
                    setAnalyzer(executor) { imageProxy ->
                        val mediaImage = imageProxy.image
                        if (mediaImage != null) {
                            val image = com.google.mlkit.vision.common.InputImage.fromMediaImage(
                                mediaImage,
                                imageProxy.imageInfo.rotationDegrees
                            )
                            
                            previewSize = Size(mediaImage.width, mediaImage.height)
                            
                            faceDetector.process(image)
                                .addOnSuccessListener { faces ->
                                    detectedFaces = faces
                                    if (faces.isNotEmpty()) {
                                        val face = faces[0]
                                        val frameCoverage = calculateFaceCoverage(
                                            face.boundingBox,
                                            mediaImage.width,
                                            mediaImage.height
                                        )
                                        onFaceDetected(face, frameCoverage)
                                    }
                                }
                                .addOnCompleteListener {
                                    imageProxy.close()
                                }
                        } else {
                            imageProxy.close()
                        }
                    }
                }

            preview.setSurfaceProvider(previewView.surfaceProvider)

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(context))

        onDispose {
            executor.shutdown()
            cameraProviderFuture.cancel(true)
        }
    }

    Box(modifier = modifier) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            detectedFaces.forEach { face ->
                val rect = face.boundingBox
                val scaleX = size.width / previewSize.width.toFloat()
                val scaleY = size.height / previewSize.height.toFloat()

                drawRect(
                    color = Color.Red,
                    topLeft = androidx.compose.ui.geometry.Offset(
                        rect.left * scaleX,
                        rect.top * scaleY
                    ),
                    size = androidx.compose.ui.geometry.Size(
                        rect.width() * scaleX,
                        rect.height() * scaleY
                    ),
                    style = Stroke(width = 2f)
                )
            }
        }
    }
}

private fun calculateFaceCoverage(boundingBox: Rect, imageWidth: Int, imageHeight: Int): Float {
    val faceArea = boundingBox.width() * boundingBox.height()
    val imageArea = imageWidth * imageHeight
    return faceArea.toFloat() / imageArea.toFloat()
} 