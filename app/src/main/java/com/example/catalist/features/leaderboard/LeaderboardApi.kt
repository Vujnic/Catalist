package com.example.catalist.features.leaderboard

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface LeaderboardApi {
    @GET("leaderboard")
    suspend fun getLeaderboard(@Query("category") category: Int = 1): List<LeaderboardEntry>

    @POST("leaderboard")
    suspend fun submitResult(@Body submission: LeaderboardSubmission): LeaderboardResponse
}