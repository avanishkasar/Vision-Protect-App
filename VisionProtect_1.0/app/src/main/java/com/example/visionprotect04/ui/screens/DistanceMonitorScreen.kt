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
private val NeonBlue = Color(0xFF4D4DFF)
private val DeepSpace = Color(0xFF0A0A1A)
private val CardDark = Color(0xFF15152A)

@Composable
fun DistanceMonitorScreen(onBack: () -> Unit) {
    var currentDistance by remember { mutableStateOf(45) }
    
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
                        .border(1.dp, NeonCyan.copy(alpha = 0.5f), CircleShape)
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ArrowBack, null, tint = Color.White, modifier = Modifier.size(24.dp))
                }
                Spacer(Modifier.width(16.dp))
                Text("Distance Monitor", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            
            Spacer(Modifier.height(60.dp))
            
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(listOf(NeonCyan.copy(alpha = 0.3f), CardDark)))
                    .border(3.dp, Brush.linearGradient(listOf(NeonCyan, NeonBlue)), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Search, null, Modifier.size(50.dp), tint = NeonCyan)
                    Spacer(Modifier.height(8.dp))
                    Text(currentDistance.toString(), fontSize = 48.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("cm", color = NeonCyan, fontSize = 16.sp)
                }
            }
            
            Spacer(Modifier.height(40.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardDark),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("Distance Zones", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
                    Spacer(Modifier.height(16.dp))
                    DistanceZone("Safe Zone", "> 100 cm", Color(0xFF00FF00))
                    DistanceZone("Warning Zone", "50-100 cm", Color(0xFFFFAA00))
                    DistanceZone("Danger Zone", "< 50 cm", Color(0xFFFF0000))
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = if (currentDistance < 50) Color(0xFF330000) else CardDark),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (currentDistance < 50) Icons.Default.Warning else Icons.Default.Check,
                        null,
                        tint = if (currentDistance < 50) Color.Red else NeonCyan
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        if (currentDistance < 50) "Too close! Move back" else "Distance OK",
                        color = Color.White, fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun DistanceZone(label: String, range: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(12.dp).clip(CircleShape).background(color))
            Spacer(Modifier.width(12.dp))
            Text(label, color = Color.White.copy(alpha = 0.8f))
        }
        Text(range, color = color, fontWeight = FontWeight.Bold)
    }
}
