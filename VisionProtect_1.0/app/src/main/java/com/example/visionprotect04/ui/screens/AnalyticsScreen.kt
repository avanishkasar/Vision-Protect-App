package com.example.visionprotect04.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.visionprotect04.data.AnalyticsManager
import com.example.visionprotect04.data.database.SessionEntity
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AnalyticsScreen() {
    val context = LocalContext.current
    val analyticsManager = remember { AnalyticsManager(context) }
    val weeklyAverage by analyticsManager.getWeeklyAverageScore().collectAsState(initial = 0)
    val weeklySessions by analyticsManager.getWeeklySessions().collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Behavioral Analytics",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Weekly Average Score",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "$weeklyAverage",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = if (weeklyAverage >= 80) "Excellent! Keep it up." 
                           else if (weeklyAverage >= 50) "Good, but room for improvement." 
                           else "Needs attention.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Session History (Last 7 Days)",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (weeklySessions.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No sessions recorded yet.", color = Color.Gray)
            }
        } else {
            // Simple Chart
            SessionChart(sessions = weeklySessions)
            
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(weeklySessions) { session ->
                    SessionItem(session)
                }
            }
        }
    }
}

@Composable
fun SessionChart(sessions: List<SessionEntity>) {
    // A simple bar chart visualization
    val maxScore = 100
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Canvas(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {
            
            val barWidth = size.width / (sessions.size * 2f)
            val maxBarHeight = size.height
            
            sessions.reversed().forEachIndexed { index, session ->
                val barHeight = (session.finalTotalScore / 100f) * maxBarHeight
                val xOffset = index * (barWidth * 2) + barWidth / 2
                
                drawLine(
                    color = if (session.finalTotalScore >= 80) Color.Green 
                            else if (session.finalTotalScore >= 50) Color.Yellow 
                            else Color.Red,
                    start = Offset(xOffset, size.height),
                    end = Offset(xOffset, size.height - barHeight),
                    strokeWidth = barWidth,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

@Composable
fun SessionItem(session: SessionEntity) {
    val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    val durationMin = (session.endTime - session.startTime) / 60000
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = dateFormat.format(Date(session.startTime)),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$durationMin mins",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${session.finalTotalScore}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (session.finalTotalScore >= 80) Color(0xFF2E7D32) 
                            else if (session.finalTotalScore >= 50) Color(0xFFF9A825) 
                            else Color(0xFFC62828)
                )
                Text(
                    text = "Score",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
