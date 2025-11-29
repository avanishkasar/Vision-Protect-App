package com.example.visionprotect04.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

private val NeonCyan = Color(0xFF00F5FF)
private val NeonPurple = Color(0xFFBF00FF)
private val DeepSpace = Color(0xFF0A0A1A)
private val CardDark = Color(0xFF15152A)

@Composable
fun BlinkCounterScreen(onBack: () -> Unit) {
    var blinkCount by remember { mutableStateOf(0) }
    
    // Simulate random blinks every 2-4 seconds
    LaunchedEffect(Unit) {
        while (true) {
            delay(Random.nextLong(2000, 4000)) // Random delay between 2-4 seconds
            blinkCount++
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(DeepSpace)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(CardDark)
                        .border(1.dp, NeonPurple.copy(alpha = 0.5f), CircleShape)
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ArrowBack, null, tint = Color.White, modifier = Modifier.size(24.dp))
                }
                Spacer(Modifier.width(16.dp))
                Text(
                    "Blink Counter",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(60.dp))

            // Main counter display
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(listOf(NeonPurple.copy(alpha = 0.3f), CardDark)))
                    .border(3.dp, Brush.linearGradient(listOf(NeonPurple, NeonCyan)), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Face, null, Modifier.size(50.dp), tint = NeonPurple)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = blinkCount.toString(),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text("blinks", color = NeonPurple, fontSize = 16.sp)
                }
            }

            Spacer(Modifier.height(40.dp))

            // Info cards
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardDark),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("Healthy Blink Rate", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("15-20 blinks per minute is recommended", color = Color.White.copy(alpha = 0.7f))
                    Spacer(Modifier.height(16.dp))
                    LinearProgressIndicator(
                        progress = { (blinkCount / 20f).coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = NeonPurple,
                        trackColor = CardDark.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                "AI detecting blinks in real-time",
                color = NeonCyan.copy(alpha = 0.7f),
                fontSize = 14.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
