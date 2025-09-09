package com.example.catalist.features.leaderboard.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizResultDao {
    @Query("SELECT * FROM quiz_results ORDER BY timestamp DESC")
    fun getAllResults(): Flow<List<QuizResultEntity>>

    @Query("SELECT * FROM quiz_results WHERE isPublished = 1")
    fun getPublishedResults(): Flow<List<QuizResultEntity>>

    @Insert
    suspend fun insertResult(result: QuizResultEntity)

    @Update
    suspend fun updateResult(result: QuizResultEntity)
}