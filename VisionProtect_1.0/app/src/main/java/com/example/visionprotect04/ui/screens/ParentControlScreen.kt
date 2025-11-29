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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val NeonCyan = Color(0xFF00F5FF)
private val NeonPink = Color(0xFFFF00A0)
private val NeonPurple = Color(0xFFBF00FF)
private val DeepSpace = Color(0xFF0A0A1A)
private val CardDark = Color(0xFF15152A)

@Composable
fun ParentControlScreen(onBack: () -> Unit) {
    var screenTimeLimit by remember { mutableStateOf(2f) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var lockEnabled by remember { mutableStateOf(false) }
    
    Box(modifier = Modifier.fillMaxSize().background(DeepSpace)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp).verticalScroll(rememberScrollState())
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(44.dp).clip(CircleShape).background(CardDark)
                        .border(1.dp, NeonPink.copy(alpha = 0.5f), CircleShape).clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Default.ArrowBack, null, tint = Color.White, modifier = Modifier.size(24.dp)) }
                Spacer(Modifier.width(16.dp))
                Text("Parent Control", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            
            Spacer(Modifier.height(30.dp))
            
            // Child Profile Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardDark),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(60.dp).clip(CircleShape)
                            .background(Brush.linearGradient(listOf(NeonPink, NeonPurple))),
                        contentAlignment = Alignment.Center
                    ) { Icon(Icons.Default.Face, null, Modifier.size(36.dp), tint = Color.White) }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text("Child's Device", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Last active: Just now", color = NeonCyan, fontSize = 14.sp)
                    }
                }
            }
            
            Spacer(Modifier.height(20.dp))
            
            // Screen Time Limit
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardDark),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Daily Screen Time Limit", fontWeight = FontWeight.Bold, color = Color.White)
                        Text(" hrs", color = NeonPink, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(16.dp))
                    Slider(
                        value = screenTimeLimit,
                        onValueChange = { screenTimeLimit = it },
                        valueRange = 0.5f..8f,
                        steps = 15,
                        colors = SliderDefaults.colors(
                            thumbColor = NeonPink,
                            activeTrackColor = NeonPink,
                            inactiveTrackColor = CardDark.copy(alpha = 0.5f)
                        )
                    )
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("30 min", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                        Text("8 hrs", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                    }
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            // Today's Usage
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardDark),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("Today's Usage", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
                    Spacer(Modifier.height(16.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        UsageStat("Screen Time", "1h 45m", NeonCyan)
                        UsageStat("Warnings", "3", NeonPink)
                        UsageStat("Breaks", "5", NeonPurple)
                    }
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            // Settings
            Text("Settings", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
            Spacer(Modifier.height(12.dp))
            
            SettingToggle("Push Notifications", "Get alerts when limits are reached", Icons.Default.Notifications, notificationsEnabled) { notificationsEnabled = it }
            Spacer(Modifier.height(12.dp))
            SettingToggle("App Lock", "Lock device when limit exceeded", Icons.Default.Lock, lockEnabled) { lockEnabled = it }
            
            Spacer(Modifier.height(20.dp))
            
            // Quick Actions
            Text("Quick Actions", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
            Spacer(Modifier.height(12.dp))
            
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ActionButton(Modifier.weight(1f), "Add Time", Icons.Default.Add, NeonCyan)
                ActionButton(Modifier.weight(1f), "Lock Now", Icons.Default.Lock, NeonPink)
            }
            
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun UsageStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
        Text(label, fontSize = 12.sp, color = Color.White.copy(alpha = 0.6f))
    }
}

@Composable
private fun SettingToggle(title: String, subtitle: String, icon: ImageVector, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardDark),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = NeonPink, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(title, color = Color.White, fontWeight = FontWeight.Medium)
                Text(subtitle, color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = NeonPink,
                    checkedTrackColor = NeonPink.copy(alpha = 0.3f)
                )
            )
        }
    }
}

@Composable
private fun ActionButton(modifier: Modifier, label: String, icon: ImageVector, color: Color) {
    Card(
        modifier = modifier.clickable { },
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = color, modifier = Modifier.size(28.dp))
            Spacer(Modifier.height(8.dp))
            Text(label, color = color, fontWeight = FontWeight.Medium, fontSize = 14.sp)
        }
    }
}
