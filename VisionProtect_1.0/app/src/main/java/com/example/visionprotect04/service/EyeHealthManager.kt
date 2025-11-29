package com.example.visionprotect04.service

import kotlin.math.abs

class EyeHealthManager {

    enum class RiskLevel {
        LOW, MEDIUM, HIGH
    }

    data class EyeHealthScore(
        val totalScore: Int,
        val distanceScore: Int,
        val durationScore: Int,
        val blinkScore: Int,
        val lightingScore: Int,
        val postureScore: Int,
        val recommendations: List<String>,
        val predictedRisk: RiskLevel = RiskLevel.LOW
    )

    private var sessionStartTime: Long = System.currentTimeMillis()
    private var lastBlinkTime: Long = System.currentTimeMillis()
    private var blinkCount: Int = 0
    private var blinkRate: Float = 15f // Blinks per minute (default average)
    
    // Prediction Buffer
    private val scoreHistory = mutableListOf<Pair<Long, Int>>() // Timestamp, Score
    // Thresholds
    companion object {
        const val HISTORY_WINDOW_MS = 10 * 60 * 1000L // 10 minutes buffer
        const val OPTIMAL_DISTANCE_MIN = 0.3f // ~30cm (approx coverage)
        const val OPTIMAL_DISTANCE_MAX = 0.6f // ~60cm (approx coverage)
        const val MAX_SESSION_DURATION_MS = 20 * 60 * 1000L // 20 minutes
        const val MIN_BLINK_RATE = 10f // Blinks per minute
        const val OPTIMAL_LUX_MIN = 300f
        const val OPTIMAL_LUX_MAX = 1000f
        const val MAX_TILT_DEGREES = 15f
    }

    fun resetSession() {
        sessionStartTime = System.currentTimeMillis()
        blinkCount = 0
        blinkRate = 15f
        scoreHistory.clear()
    }

    fun registerBlink() {
        val now = System.currentTimeMillis()
        blinkCount++
        // Simple moving average or windowed calculation could go here
        // For now, let's calculate rate based on session duration or a rolling window
        val sessionDurationMin = (now - sessionStartTime) / 60000f
        if (sessionDurationMin > 0.5f) { // Wait 30 seconds before calculating rate
            blinkRate = blinkCount / sessionDurationMin
        }
    }

    fun calculateScore(
        faceCoverage: Float,
        ambientLux: Float?,
        headEulerAngleZ: Float? // Tilt
    ): EyeHealthScore {
        val now = System.currentTimeMillis()
        val sessionDuration = now - sessionStartTime

        // 1. Distance Score (30 points)
        // Lower coverage = further away. 
        // Too close (> 0.7 coverage) = 0 points
        // Optimal (0.3 - 0.6) = 30 points
        // Too far (< 0.2) = 20 points (maybe too hard to see)
        val distanceScore = when {
            faceCoverage > 0.7f -> 0
            faceCoverage in 0.3f..0.6f -> 30
            else -> 15 // Acceptable but not perfect
        }

        // 2. Duration Score (20 points)
        // Full points if under 20 mins. Lose 1 point per minute over.
        val durationOverrunMinutes = if (sessionDuration > MAX_SESSION_DURATION_MS) {
            (sessionDuration - MAX_SESSION_DURATION_MS) / 60000
        } else 0
        val durationScore = (20 - durationOverrunMinutes).toInt().coerceIn(0, 20)

        // 3. Blink Score (20 points)
        // > 10 blinks/min = 20 points
        // < 10 = proportional drop
        val blinkScore = if (blinkRate >= MIN_BLINK_RATE) 20 else ((blinkRate / MIN_BLINK_RATE) * 20).toInt()

        // 4. Lighting Score (15 points)
        val lightingScore = if (ambientLux != null) {
            when {
                ambientLux < 50f -> 0 // Dangerously low light
                ambientLux in OPTIMAL_LUX_MIN..OPTIMAL_LUX_MAX -> 15
                else -> 5
            }
        } else {
            15 // Assume good if no sensor
        }

        // 5. Posture Score (15 points)
        val postureScore = if (headEulerAngleZ != null) {
            if (abs(headEulerAngleZ) < MAX_TILT_DEGREES) 15 else 0
        } else {
            15 // Assume good if no detection
        }

        val totalScore = distanceScore + durationScore + blinkScore + lightingScore + postureScore
        
        // Update History
        updateScoreHistory(now, totalScore)
        
        // Predict Risk
        val predictedRisk = predictStrainRisk(totalScore)
        
        val recommendations = mutableListOf<String>()
        if (distanceScore < 15) recommendations.add("Move further back")
        if (durationScore < 10) recommendations.add("Take a break (20-20-20 rule)")
        if (blinkScore < 10) recommendations.add("Blink more often")
        if (lightingScore < 10) recommendations.add("Adjust room lighting")
        if (postureScore < 10) recommendations.add("Straighten your head")
        if (predictedRisk == RiskLevel.HIGH) recommendations.add("HIGH STRAIN RISK: Stop immediately!")

        return EyeHealthScore(
            totalScore = totalScore,
            distanceScore = distanceScore,
            durationScore = durationScore,
            blinkScore = blinkScore,
            lightingScore = lightingScore,
            postureScore = postureScore,
            recommendations = recommendations,
            predictedRisk = predictedRisk
        )
    }
    
    private fun updateScoreHistory(timestamp: Long, score: Int) {
        scoreHistory.add(timestamp to score)
        // Keep only last 10 minutes
        val cutoff = timestamp - HISTORY_WINDOW_MS
        scoreHistory.removeAll { it.first < cutoff }
    }
    
    private fun predictStrainRisk(currentScore: Int): RiskLevel {
        if (scoreHistory.size < 10) return RiskLevel.LOW // Not enough data
        
        // Calculate slope (linear regression)
        // Simple approach: (Last Score - First Score) / Time Delta
        val first = scoreHistory.first()
        val last = scoreHistory.last()
        val timeDeltaMinutes = (last.first - first.first) / 60000f
        
        if (timeDeltaMinutes < 1f) return RiskLevel.LOW // Need at least 1 minute of data
        
        val scoreDelta = last.second - first.second
        val slope = scoreDelta / timeDeltaMinutes // Points per minute change
        
        // Project 30 minutes into future
        val projectedScore = currentScore + (slope * 30)
        
        return when {
            projectedScore < 40 -> RiskLevel.HIGH
            projectedScore < 70 -> RiskLevel.MEDIUM
            else -> RiskLevel.LOW
        }
    }
}

