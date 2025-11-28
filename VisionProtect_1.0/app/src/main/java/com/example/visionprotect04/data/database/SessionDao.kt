package com.example.visionprotect04.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Insert
    suspend fun insertSession(session: SessionEntity)

    @Query("SELECT * FROM sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<SessionEntity>>

    @Query("SELECT * FROM sessions WHERE startTime >= :timestamp ORDER BY startTime DESC")
    fun getSessionsAfter(timestamp: Long): Flow<List<SessionEntity>>
    
    @Query("SELECT AVG(finalTotalScore) FROM sessions WHERE startTime >= :timestamp")
    suspend fun getAverageScoreAfter(timestamp: Long): Float?
}
