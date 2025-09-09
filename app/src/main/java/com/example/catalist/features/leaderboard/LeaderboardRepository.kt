package com.example.catalist.features.leaderboard

import com.example.catalist.core.data.UserAccountStore
import com.example.catalist.features.leaderboard.data.QuizResultDao
import com.example.catalist.features.leaderboard.data.QuizResultEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LeaderboardRepository @Inject constructor(
    private val leaderboardApi: LeaderboardApi,
    private val quizResultDao: QuizResultDao,
    private val userAccountStore: UserAccountStore
) {
    fun getLocalResults(): Flow<List<QuizResultEntity>> =
        quizResultDao.getAllResults()

    suspend fun getLeaderboard(): List<LeaderboardEntry> =
        leaderboardApi.getLeaderboard()

    suspend fun saveLocalResult(result: QuizResultEntity) {
        quizResultDao.insertResult(result)
    }

    suspend fun submitResult(score: Float): LeaderboardResponse {
        val nickname = userAccountStore.nickname.first() ?:
        throw IllegalStateException("User not logged in")

        return leaderboardApi.submitResult(
            LeaderboardSubmission(
                nickname = nickname,
                result = score,
                category = 1
            )
        )
    }
}