package com.example.catalist.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catalist.core.data.UserAccountStore
import com.example.catalist.features.leaderboard.LeaderboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userAccountStore: UserAccountStore,
    private val leaderboardRepository: LeaderboardRepository
) : ViewModel() {

    data class ProfileUiState(
        val fullName: String = "",
        val nickname: String = "",
        val email: String = "",
        val quizResults: List<QuizResultUi> = emptyList(),
        val bestScore: Float = 0f,
        val bestRanking: Int = 0,
        val isLoading: Boolean = true,
        val error: String? = null
    )

    data class QuizResultUi(
        val score: Float,
        val timestamp: Long,
        val isPublished: Boolean
    )

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadProfileData()
    }

    private fun loadProfileData() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                val fullName = userAccountStore.fullName.first()
                val nickname = userAccountStore.nickname.first()
                val email = userAccountStore.email.first()
                val results = leaderboardRepository.getLocalResults().first()
                val leaderboard = leaderboardRepository.getLeaderboard()

                val resultUiList = results.map { result ->
                    QuizResultUi(
                        score = result.score,
                        timestamp = result.timestamp,
                        isPublished = result.isPublished
                    )
                }.sortedByDescending { it.timestamp }

                val bestScore = results.maxOfOrNull { it.score } ?: 0f

                val bestRanking = if (nickname != null) {
                    leaderboard
                        .filter { it.nickname == nickname }
                        .minOfOrNull { leaderboard.indexOf(it) + 1 } ?: 0
                } else 0

                _uiState.update {
                    it.copy(
                        fullName = fullName ?: "",
                        nickname = nickname ?: "",
                        email = email ?: "",
                        quizResults = resultUiList,
                        bestScore = bestScore,
                        bestRanking = bestRanking,
                        isLoading = false
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load profile: ${e.message}"
                    )
                }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                loadProfileData()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to refresh: ${e.message}"
                    )
                }
            }
        }
    }
}