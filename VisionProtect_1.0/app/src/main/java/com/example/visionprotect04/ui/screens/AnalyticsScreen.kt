package com.example.visionprotect04.ui.screens

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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val NeonCyan = Color(0xFF00F5FF)
private val NeonBlue = Color(0xFF4D4DFF)
private val NeonPurple = Color(0xFFBF00FF)
private val NeonPink = Color(0xFFFF00A0)
private val DeepSpace = Color(0xFF0A0A1A)
private val CardDark = Color(0xFF15152A)

@Composable
fun AnalyticsScreen(onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(DeepSpace)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp).verticalScroll(rememberScrollState()),
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
                        .border(1.dp, NeonBlue.copy(alpha = 0.5f), CircleShape)
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ArrowBack, null, tint = Color.White, modifier = Modifier.size(24.dp))
                }
                Spacer(Modifier.width(16.dp))
                Text("Daily Analytics", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            
            Spacer(Modifier.height(30.dp))
            
            // Overall Score
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardDark),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Today's Eye Health Score", color = Color.White.copy(alpha = 0.7f))
                    Spacer(Modifier.height(12.dp))
                    Text("87", fontSize = 64.sp, fontWeight = FontWeight.Bold,
                        style = LocalTextStyle.current.copy(brush = Brush.linearGradient(listOf(NeonCyan, NeonBlue))))
                    Text("Excellent", color = NeonCyan, fontWeight = FontWeight.Medium)
                }
            }
            
            Spacer(Modifier.height(20.dp))
            
            // Stats Grid
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(Modifier.weight(1f), "Screen Time", "4h 23m", NeonPurple)
                StatCard(Modifier.weight(1f), "Avg Distance", "52 cm", NeonCyan)
            }
            
            Spacer(Modifier.height(12.dp))
            
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(Modifier.weight(1f), "Blinks/min", "18", NeonPink)
                StatCard(Modifier.weight(1f), "Breaks", "6", NeonBlue)
            }
            
            Spacer(Modifier.height(20.dp))
            
            // Session History
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardDark),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("Session History", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
                    Spacer(Modifier.height(16.dp))
                    SessionItem("Morning Session", "9:00 - 11:30", "Good", NeonCyan)
                    SessionItem("Afternoon Session", "14:00 - 16:45", "Excellent", NeonPurple)
                    SessionItem("Evening Session", "19:00 - 21:00", "Warning", NeonPink)
                }
            }
            
            Spacer(Modifier.height(20.dp))
            
            // Tips
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardDark),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, null, tint = NeonCyan)
                    Spacer(Modifier.width(12.dp))
                    Text("Follow 20-20-20 rule for better eye health", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                }
            }
            
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun StatCard(modifier: Modifier, label: String, value: String, color: Color) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CardDark),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
            Spacer(Modifier.height(4.dp))
            Text(label, fontSize = 12.sp, color = Color.White.copy(alpha = 0.6f))
        }
    }
}

@Composable
private fun SessionItem(name: String, time: String, status: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(name, color = Color.White, fontWeight = FontWeight.Medium)
            Text(time, color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
        }
        Text(status, color = color, fontWeight = FontWeight.Bold)
    }
}
