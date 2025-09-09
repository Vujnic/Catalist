package com.example.catalist.features.leaderboard.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_results")
data class QuizResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val score: Float,
    val isPublished: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)