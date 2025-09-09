package com.example.catalist.core.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.catalist.features.cats.room.CatBreedDao
import com.example.catalist.features.cats.room.CatBreedEntity
import com.example.catalist.features.cats.room.CatImageDao
import com.example.catalist.features.cats.room.CatImageEntity
import com.example.catalist.features.leaderboard.data.QuizResultDao
import com.example.catalist.features.leaderboard.data.QuizResultEntity


@Database(
    entities = [
        CatBreedEntity::class,
        CatImageEntity::class,
        QuizResultEntity::class
    ],
    version = 3,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun catBreedDao(): CatBreedDao
    abstract fun catImageDao(): CatImageDao
    abstract fun quizResultDao(): QuizResultDao
}