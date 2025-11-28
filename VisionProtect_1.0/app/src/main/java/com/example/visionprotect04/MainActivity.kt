package com.example.visionprotect04

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.visionprotect04.service.VisionProtectService
import com.example.visionprotect04.ui.screens.CameraScreen
import com.example.visionprotect04.ui.screens.HomeScreen
import com.example.visionprotect04.ui.theme.VisionProtect04Theme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

class MainActivity : ComponentActivity() {
    private var serviceRunning by mutableStateOf(false)

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            VisionProtect04Theme {
                MainScreen(
                    onStartService = { startVisionProtectService(it) },
                    onStopService = { stopVisionProtectService(it) },
                    serviceRunning = serviceRunning
                )
            }
        }
    }

    private fun startVisionProtectService(context: Context) {
        if (!serviceRunning) {
            val serviceIntent = Intent(context, VisionProtectService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
            serviceRunning = true
        }
    }

    private fun stopVisionProtectService(context: Context) {
        if (serviceRunning) {
            context.stopService(Intent(context, VisionProtectService::class.java))
            serviceRunning = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopVisionProtectService(this)
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun MainScreen(
    onStartService: (Context) -> Unit,
    onStopService: (Context) -> Unit,
    serviceRunning: Boolean
) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val permissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.POST_NOTIFICATIONS
        )
    )

    LaunchedEffect(permissionsState.allPermissionsGranted) {
        if (permissionsState.allPermissionsGranted && 
            !Settings.canDrawOverlays(context)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context.packageName}")
            )
            context.startActivity(intent)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (!permissionsState.allPermissionsGranted) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "Camera and notification permissions are required",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { permissionsState.launchMultiplePermissionRequest() }) {
                    Text("Grant Permissions")
                }
            }
        } else {
            NavHost(
                navController = navController,
                startDestination = "home"
            ) {
                composable("home") {
                    HomeScreen(
                        onStartProtection = {
                            navController.navigate("camera")
                        }
                    )
                }
                composable("camera") {
                    CameraScreen(
                        onStartProtection = { onStartService(context) },
                        onStopProtection = { onStopService(context) },
                        isServiceRunning = serviceRunning
                    )
                }
            }
        }
    }
}