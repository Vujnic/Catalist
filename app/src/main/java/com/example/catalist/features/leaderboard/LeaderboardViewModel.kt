package com.example.catalist.features.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val repository: LeaderboardRepository
) : ViewModel() {

    data class LeaderboardUiState(
        val entries: List<LeaderboardEntryUi> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    )

    data class LeaderboardEntryUi(
        val position: Int,
        val nickname: String,
        val score: Float,
        val gamesPlayed: Int
    )

    private val _uiState = MutableStateFlow(LeaderboardUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init {
        loadLeaderboard()
    }

    private fun loadLeaderboard() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }

                val leaderboard = repository.getLeaderboard()
                val entriesGrouped = leaderboard.groupBy { it.nickname }

                val entries = entriesGrouped.map { (nickname, entries) ->
                    val bestEntry = entries.maxBy { it.result }
                    LeaderboardEntryUi(
                        position = leaderboard.indexOf(bestEntry) + 1,
                        nickname = nickname,
                        score = bestEntry.result,
                        gamesPlayed = entries.size
                    )
                }.sortedBy { it.position }

                _uiState.update { it.copy(
                    entries = entries,
                    isLoading = false
                )}
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = "Failed to load leaderboard: ${e.message}"
                )}
            }
        }
    }

    fun refresh() {
        loadLeaderboard()
    }
}