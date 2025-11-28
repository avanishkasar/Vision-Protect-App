package com.example.visionprotect04.data

import com.example.visionprotect04.service.EyeHealthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object EyeHealthRepository {
    private val _eyeHealthScore = MutableStateFlow<EyeHealthManager.EyeHealthScore?>(null)
    val eyeHealthScore: StateFlow<EyeHealthManager.EyeHealthScore?> = _eyeHealthScore.asStateFlow()

    fun updateScore(score: EyeHealthManager.EyeHealthScore) {
        _eyeHealthScore.value = score
    }
}
