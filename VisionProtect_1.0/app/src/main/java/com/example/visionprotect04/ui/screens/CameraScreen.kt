package com.example.visionprotect04.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.visionprotect04.R
import com.example.visionprotect04.data.EyeHealthRepository

@Composable
fun CameraScreen(
    onStartProtection: () -> Unit,
    onStopProtection: () -> Unit,
    isServiceRunning: Boolean
) {
    val eyeHealthScore by EyeHealthRepository.eyeHealthScore.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Protection Control Button
        Button(
            onClick = {
                if (isServiceRunning) {
                    onStopProtection()
                } else {
                    onStartProtection()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isServiceRunning) MaterialTheme.colorScheme.error 
                else MaterialTheme.colorScheme.primary
            ),
            shape = MaterialTheme.shapes.large
        ) {
            Text(
                text = stringResource(
                    if (isServiceRunning) R.string.stop_protection 
                    else R.string.start_protection
                ),
                fontSize = 20.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Eye Health Score Display
        if (isServiceRunning && eyeHealthScore != null) {
            val score = eyeHealthScore!!
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Eye Health Score",
                        style = MaterialTheme.typography.titleLarge
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Score Circle (Simple Text for now)
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(120.dp)
                    ) {
                        CircularProgressIndicator(
                            progress = { score.totalScore / 100f },
                            modifier = Modifier.fillMaxSize(),
                            color = when {
                                score.totalScore >= 80 -> Color.Green
                                score.totalScore >= 50 -> Color.Yellow
                                else -> Color.Red
                            },
                            strokeWidth = 12.dp,
                        )
                        Text(
                            text = "${score.totalScore}",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Breakdown
                    ScoreRow("Distance", score.distanceScore, 30)
                    ScoreRow("Duration", score.durationScore, 20)
                    ScoreRow("Blink Rate", score.blinkScore, 20)
                    ScoreRow("Lighting", score.lightingScore, 15)
                    ScoreRow("Posture", score.postureScore, 15)
                    
                    if (score.recommendations.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Recommendations:",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        score.recommendations.forEach { recommendation ->
                            Text(
                                text = "• $recommendation",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        } else if (isServiceRunning) {
            Text("Calculating score...", style = MaterialTheme.typography.bodyLarge)
        } else {
            Text(
                "Start protection to see your Eye Health Score",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun ScoreRow(label: String, score: Int, maxScore: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = "$score / $maxScore", 
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}