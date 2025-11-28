package com.example.visionprotect04.data

import android.content.Context
import com.example.visionprotect04.data.database.AppDatabase
import com.example.visionprotect04.data.database.SessionEntity
import com.example.visionprotect04.service.EyeHealthManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Calendar

class AnalyticsManager(private val context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val sessionDao = database.sessionDao()
    private val scope = CoroutineScope(Dispatchers.IO)

    fun saveSession(
        startTime: Long,
        endTime: Long,
        score: EyeHealthManager.EyeHealthScore
    ) {
        scope.launch {
            val session = SessionEntity(
                startTime = startTime,
                endTime = endTime,
                avgDistanceScore = score.distanceScore,
                avgBlinkScore = score.blinkScore,
                avgLightingScore = score.lightingScore,
                avgPostureScore = score.postureScore,
                finalTotalScore = score.totalScore
            )
            sessionDao.insertSession(session)
        }
    }

    fun getWeeklyAverageScore(): Flow<Int> {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val oneWeekAgo = calendar.timeInMillis

        return sessionDao.getSessionsAfter(oneWeekAgo).map { sessions ->
            if (sessions.isEmpty()) 0
            else sessions.map { it.finalTotalScore }.average().toInt()
        }
    }

    fun getWeeklySessions(): Flow<List<SessionEntity>> {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val oneWeekAgo = calendar.timeInMillis
        return sessionDao.getSessionsAfter(oneWeekAgo)
    }
}
