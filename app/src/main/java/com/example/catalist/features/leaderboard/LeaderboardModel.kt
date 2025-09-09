package com.example.catalist.features.leaderboard

import kotlinx.serialization.Serializable

@Serializable
data class LeaderboardEntry(
    val category: Int,
    val nickname: String,
    val result: Float,
    val createdAt: Long
)

@Serializable
data class LeaderboardSubmission(
    val nickname: String,
    val result: Float,
    val category: Int = 1
)

@Serializable
data class LeaderboardResponse(
    val result: LeaderboardEntry,
    val ranking: Int
)