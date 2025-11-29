package com.example.visionprotect04.ui.screens

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

private val NeonCyan = Color(0xFF00F5FF)
private val NeonPink = Color(0xFFFF00A0)
private val DeepSpace = Color(0xFF0A0A1A)
private val CardDark = Color(0xFF15152A)

@Composable
fun BodyPostureScreen(onBack: () -> Unit) {
    var postureScore by remember { mutableStateOf(85) }
    
    Box(modifier = Modifier.fillMaxSize().background(DeepSpace)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(CardDark)
                        .border(1.dp, NeonPink.copy(alpha = 0.5f), CircleShape)
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ArrowBack, null, tint = Color.White, modifier = Modifier.size(24.dp))
                }
                Spacer(Modifier.width(16.dp))
                Text("Body Posture", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            
            Spacer(Modifier.height(60.dp))
            
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(listOf(NeonPink.copy(alpha = 0.3f), CardDark)))
                    .border(3.dp, Brush.linearGradient(listOf(NeonPink, NeonCyan)), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Person, null, Modifier.size(50.dp), tint = NeonPink)
                    Spacer(Modifier.height(8.dp))
                    Text(postureScore.toString(), fontSize = 48.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("score", color = NeonPink, fontSize = 16.sp)
                }
            }
            
            Spacer(Modifier.height(40.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardDark),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("Posture Analysis", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
                    Spacer(Modifier.height(16.dp))
                    PostureItem("Head Position", "Good", NeonCyan)
                    PostureItem("Shoulder Alignment", "Excellent", NeonPink)
                    PostureItem("Back Posture", "Good", NeonCyan)
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            Text(
                "AI monitors your posture\nusing front camera",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun PostureItem(label: String, status: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.White.copy(alpha = 0.8f))
        Text(status, color = color, fontWeight = FontWeight.Bold)
    }
}
