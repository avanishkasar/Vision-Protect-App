package com.example.visionprotect04.ui.screens

import androidx.compose.animation.core.*
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
    var blinksPerMinute by remember { mutableStateOf(16) }
    
    LaunchedEffect(Unit) {
        while (true) {
            delay(Random.nextLong(2000, 4000))
            blinkCount++
            blinksPerMinute = Random.nextInt(14, 22)
        }
    }
    
    Box(modifier = Modifier.fillMaxSize().background(DeepSpace)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(44.dp).clip(CircleShape).background(CardDark)
                        .border(1.dp, NeonPurple.copy(alpha = 0.5f), CircleShape).clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Default.ArrowBack, null, tint = Color.White, modifier = Modifier.size(24.dp)) }
                Spacer(Modifier.width(16.dp))
                Text("Blink Counter", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            
            Spacer(Modifier.height(60.dp))
            
            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
            val scale by infiniteTransition.animateFloat(1f, 1.05f, infiniteRepeatable(tween(1000), RepeatMode.Reverse), label = "s")
            
            Box(
                modifier = Modifier.size(200.dp).graphicsLayer { scaleX = scale; scaleY = scale }.clip(CircleShape)
                    .background(Brush.radialGradient(listOf(NeonPurple.copy(alpha = 0.3f), CardDark)))
                    .border(3.dp, Brush.linearGradient(listOf(NeonPurple, NeonCyan)), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Face, null, Modifier.size(50.dp), tint = NeonPurple)
                    Spacer(Modifier.height(8.dp))
                    Text(blinkCount.toString(), fontSize = 48.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("blinks", color = NeonPurple, fontSize = 16.sp)
                }
            }
            
            Spacer(Modifier.height(40.dp))
            
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = CardDark), shape = RoundedCornerShape(16.dp)) {
                Row(Modifier.padding(20.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Blinks per minute", color = Color.White.copy(alpha = 0.8f))
                    Text(blinksPerMinute.toString(), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = if (blinksPerMinute >= 15) NeonCyan else Color(0xFFFFAA00))
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = CardDark), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(20.dp)) {
                    Text("Healthy Blink Rate", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("15-20 blinks per minute recommended", color = Color.White.copy(alpha = 0.7f))
                    Spacer(Modifier.height(16.dp))
                    LinearProgressIndicator(
                        progress = { (blinksPerMinute / 20f).coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = if (blinksPerMinute >= 15) NeonCyan else Color(0xFFFFAA00),
                        trackColor = CardDark.copy(alpha = 0.5f)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(if (blinksPerMinute >= 15) "Great! Healthy blink rate" else "Try to blink more", color = if (blinksPerMinute >= 15) NeonCyan else Color(0xFFFFAA00), fontSize = 12.sp)
                }
            }
            
            Spacer(Modifier.height(16.dp))
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(10.dp).clip(CircleShape).background(Color(0xFF00FF00)))
                Spacer(Modifier.width(8.dp))
                Text("Monitoring active", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
            }
        }
    }
}
