package com.example.visionprotect04.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.sin
import kotlin.random.Random

private val NeonCyan = Color(0xFF00F5FF)
private val NeonPurple = Color(0xFFBF00FF)
private val NeonPink = Color(0xFFFF00A0)
private val NeonBlue = Color(0xFF4D4DFF)
private val DeepSpace = Color(0xFF0A0A1A)
private val CardDark = Color(0xFF15152A)

@Composable
fun HomeScreen(
    onStartProtection: () -> Unit,
    onBlinkCounter: () -> Unit = {},
    onBodyPosture: () -> Unit = {},
    onDistanceMonitor: () -> Unit = {},
    onAnalytics: () -> Unit = {},
    onParentControl: () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize()) {
        GalaxyBackground()
        FloatingStars()
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            GlowingLogo()
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "VisionProtect",
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineLarge.copy(
                    brush = Brush.linearGradient(listOf(NeonCyan, NeonPurple, NeonPink))
                )
            )
            Text("AI-Powered Eye Safety", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            MainFeatureCard(
                title = "Screen Protect",
                subtitle = "Smart Distance Shield",
                description = "Blocks screen < 100cm • Warning < 200cm",
                onClick = onStartProtection
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FeatureCard(Modifier.weight(1f), "Blink\nCounter", Icons.Default.Face, NeonPurple, onBlinkCounter)
                FeatureCard(Modifier.weight(1f), "Body\nPosture", Icons.Default.Person, NeonPink, onBodyPosture)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FeatureCard(Modifier.weight(1f), "Distance\nMonitor", Icons.Default.Search, NeonCyan, onDistanceMonitor)
                FeatureCard(Modifier.weight(1f), "Daily\nAnalytics", Icons.Default.DateRange, NeonBlue, onAnalytics)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            ParentControlCard(onParentControl)
            Spacer(modifier = Modifier.height(32.dp))
            GlowingButton("START PROTECTION", onStartProtection)
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun GalaxyBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "galaxy")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(15000, easing = LinearEasing), RepeatMode.Reverse),
        label = "offset"
    )
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(color = DeepSpace)
        drawCircle(
            brush = Brush.radialGradient(
                listOf(NeonPurple.copy(alpha = 0.4f), NeonPurple.copy(alpha = 0.1f), Color.Transparent),
                center = Offset(size.width * (0.2f + offset * 0.3f), size.height * 0.3f),
                radius = size.width * 0.8f
            ), radius = size.width
        )
        drawCircle(
            brush = Brush.radialGradient(
                listOf(NeonCyan.copy(alpha = 0.3f), NeonBlue.copy(alpha = 0.1f), Color.Transparent),
                center = Offset(size.width * (0.8f - offset * 0.2f), size.height * 0.7f),
                radius = size.width * 0.6f
            ), radius = size.width
        )
        drawCircle(
            brush = Brush.radialGradient(
                listOf(NeonPink.copy(alpha = 0.25f), Color.Transparent),
                center = Offset(size.width * 0.5f, size.height * (0.4f + offset * 0.2f)),
                radius = size.width * 0.5f
            ), radius = size.width
        )
    }
}

@Composable
fun FloatingStars() {
    val stars = remember { List(50) { floatArrayOf(Random.nextFloat(), Random.nextFloat(), Random.nextFloat() * 3f + 1f, Random.nextFloat() * 0.8f + 0.2f) } }
    val infiniteTransition = rememberInfiniteTransition(label = "stars")
    val twinkle by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "twinkle"
    )
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        stars.forEach { s ->
            val alpha = (s[3] * (0.5f + twinkle * 0.5f * sin(s[0] * 10f))).coerceIn(0.1f, 1f)
            drawCircle(Color.White.copy(alpha = alpha), radius = s[2], center = Offset(s[0] * size.width, s[1] * size.height))
        }
    }
}

@Composable
fun GlowingLogo() {
    val infiniteTransition = rememberInfiniteTransition(label = "logo")
    val rotation by infiniteTransition.animateFloat(0f, 360f, infiniteRepeatable(tween(10000, easing = LinearEasing), RepeatMode.Restart), label = "r")
    val pulse by infiniteTransition.animateFloat(1f, 1.1f, infiniteRepeatable(tween(1500, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "p")
    
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(150.dp)) {
        Canvas(modifier = Modifier.size(150.dp).rotate(rotation)) {
            drawArc(Brush.sweepGradient(listOf(NeonCyan, NeonPurple, NeonPink, NeonCyan)), 0f, 300f, false, style = Stroke(4.dp.toPx(), cap = StrokeCap.Round))
        }
        Box(
            modifier = Modifier.size(110.dp).scale(pulse).clip(CircleShape)
                .background(Brush.radialGradient(listOf(NeonCyan.copy(alpha = 0.3f), NeonPurple.copy(alpha = 0.15f), Color.Transparent)))
                .border(2.dp, Brush.linearGradient(listOf(NeonCyan, NeonPurple)), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Face, null, Modifier.size(50.dp), tint = NeonCyan)
        }
    }
}

@Composable
fun MainFeatureCard(title: String, subtitle: String, description: String, onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "card")
    val glow by infiniteTransition.animateFloat(0.5f, 1f, infiniteRepeatable(tween(1200), RepeatMode.Reverse), label = "g")
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .border(
                2.dp,
                Brush.linearGradient(listOf(NeonCyan.copy(alpha = glow), NeonBlue.copy(alpha = glow))),
                RoundedCornerShape(24.dp)
            )
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = CardDark.copy(alpha = 0.9f)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(65.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(listOf(NeonCyan, NeonBlue))),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Lock, null, Modifier.size(32.dp), tint = Color.White)
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1
                )
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = NeonCyan,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    maxLines = 1
                )
            }
            Icon(Icons.Default.ArrowForward, null, Modifier.size(22.dp), tint = NeonCyan)
        }
    }
}

@Composable
fun FeatureCard(modifier: Modifier, title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, onClick: () -> Unit = {}) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = CardDark.copy(alpha = 0.85f)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(Modifier.fillMaxSize()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            listOf(color.copy(alpha = 0.2f), Color.Transparent),
                            center = Offset(0f, 0f),
                            radius = 400f
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(icon, null, Modifier.size(32.dp), tint = color)
                Spacer(Modifier.height(6.dp))
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 14.sp,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
fun ParentControlCard(onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, NeonPink.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = CardDark.copy(alpha = 0.85f)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(NeonPink, NeonPurple))),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, null, Modifier.size(28.dp), tint = Color.White)
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    "Parent Control",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1
                )
                Text(
                    "Monitor screen time",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    maxLines = 1
                )
            }
            Icon(Icons.Default.ArrowForward, null, Modifier.size(20.dp), tint = NeonPink)
        }
    }
}

@Composable
fun GlowingButton(text: String, onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "btn")
    val glow by infiniteTransition.animateFloat(0.6f, 1f, infiniteRepeatable(tween(1000), RepeatMode.Reverse), label = "g")
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(64.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        NeonCyan.copy(alpha = glow),
                        NeonPurple.copy(alpha = glow),
                        NeonPink.copy(alpha = glow)
                    )
                )
            )
            .border(
                2.dp,
                Brush.linearGradient(listOf(NeonCyan, NeonPink)),
                RoundedCornerShape(32.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = Color.White
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1
            )
        }
    }
}
