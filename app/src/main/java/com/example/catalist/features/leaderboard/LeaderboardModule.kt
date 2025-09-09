package com.example.catalist.features.leaderboard

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LeaderboardModule {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        prettyPrint = true
        isLenient = true
    }
    @Provides
    @Singleton
    fun provideLeaderboardApi(okHttpClient: OkHttpClient): LeaderboardApi {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl("https://rma.finlab.rs/")
            .addConverterFactory(json.asConverterFactory(contentType))
            .client(okHttpClient)
            .build()
            .create(LeaderboardApi::class.java)
    }

}

