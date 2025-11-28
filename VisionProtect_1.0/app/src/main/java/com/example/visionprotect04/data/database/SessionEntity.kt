package com.example.visionprotect04.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startTime: Long,
    val endTime: Long,
    val avgDistanceScore: Int,
    val avgBlinkScore: Int,
    val avgLightingScore: Int,
    val avgPostureScore: Int,
    val finalTotalScore: Int
)
