package com.example.catalist.core.di

import android.content.Context
import androidx.room.Room
import com.example.catalist.core.data.AppDatabase
import com.example.catalist.features.cats.room.CatBreedDao
import com.example.catalist.features.cats.room.CatImageDao
import com.example.catalist.features.leaderboard.data.QuizResultDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "catalist.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideCatBreedDao(database: AppDatabase): CatBreedDao =
        database.catBreedDao()

    @Provides
    @Singleton
    fun provideCatImageDao(database: AppDatabase): CatImageDao =
        database.catImageDao()

    @Provides
    @Singleton
    fun provideQuizResultDao(database: AppDatabase): QuizResultDao =
        database.quizResultDao()
}