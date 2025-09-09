package com.example.catalist.features.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catalist.features.leaderboard.LeaderboardRepository
import com.example.catalist.features.leaderboard.data.QuizResultEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    private val leaderboardRepository: LeaderboardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizContract.UiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = Channel<QuizContract.SideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

    private var quizTimer: Job? = null
    private var questions = mutableListOf<QuizQuestion>()

    init {
        startQuiz()
    }

    private fun startQuiz() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                // Generišemo pitanja
                questions = buildList {
                    repeat(10) { add(quizRepository.generateBreedQuestion()) }
                    repeat(10) { add(quizRepository.generateTemperamentQuestion()) }
                }.shuffled().toMutableList()

                _uiState.update { it.copy(
                    isLoading = false,
                    currentQuestion = questions.firstOrNull(),
                    currentQuestionIndex = 0,
                    totalQuestions = questions.size
                )}

                startTimer()
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = "Failed to start quiz: ${e.message}"
                )}
            }
        }
    }

    private fun startTimer() {
        quizTimer?.cancel()
        quizTimer = viewModelScope.launch {
            while (_uiState.value.remainingTimeSeconds > 0 && !_uiState.value.isFinished) {
                delay(1000)
                _uiState.update { it.copy(remainingTimeSeconds = it.remainingTimeSeconds - 1) }

                if (_uiState.value.remainingTimeSeconds == 0) {
                    finishQuiz()
                }
            }
        }
    }

    fun onEvent(event: QuizContract.UiEvent) {
        when (event) {
            is QuizContract.UiEvent.AnswerSelected -> handleAnswer(event.answer)
            is QuizContract.UiEvent.NextQuestion -> showNextQuestion()
            is QuizContract.UiEvent.ExitQuiz -> showExitDialog()
            is QuizContract.UiEvent.ConfirmExit -> exitQuiz()
            is QuizContract.UiEvent.CancelExit -> hideExitDialog()
            is QuizContract.UiEvent.PublishResult -> publishResult()
        }
    }

    private fun handleAnswer(selectedAnswer: String) {
        val currentQuestion = _uiState.value.currentQuestion ?: return
        val isCorrect = selectedAnswer == currentQuestion.correctAnswer

        _uiState.update { state ->
            state.copy(
                answers = state.answers + (state.currentQuestionIndex to isCorrect)
            )
        }

        if (_uiState.value.currentQuestionIndex < questions.size - 1) {
            showNextQuestion()
        } else {
            finishQuiz()
        }
    }

    private fun showNextQuestion() {
        val nextIndex = _uiState.value.currentQuestionIndex + 1
        _uiState.update { state ->
            state.copy(
                currentQuestionIndex = nextIndex,
                currentQuestion = questions.getOrNull(nextIndex)
            )
        }
    }

    private fun finishQuiz() {
        quizTimer?.cancel()
        val correctAnswers = _uiState.value.answers.count { it.value }
        val remainingTime = _uiState.value.remainingTimeSeconds

        val result = QuizResult(
            correctAnswers = correctAnswers,
            remainingTimeSeconds = remainingTime
        )

        viewModelScope.launch {
            try {
                leaderboardRepository.saveLocalResult(
                    QuizResultEntity(
                        score = result.calculateScore(),
                        isPublished = false
                    )
                )
            } catch (e: Exception) {
                TODO()
            }

        }
        _uiState.update { it.copy(
            isFinished = true,
            result = result
        )}
    }

    private fun publishResult() {
        viewModelScope.launch {
            try {
                val result = _uiState.value.result ?: return@launch
                val score = result.calculateScore()

                leaderboardRepository.saveLocalResult(
                    QuizResultEntity(
                        score = score,
                        isPublished = true
                    )
                )

                leaderboardRepository.submitResult(score)

                _sideEffect.send(QuizContract.SideEffect.NavigateBack)
            } catch (e: Exception) {
                _sideEffect.send(QuizContract.SideEffect.ShowError("Failed to publish result: ${e.message}"))
            }
        }
    }

    private fun showExitDialog() {
        _uiState.update { it.copy(showExitDialog = true) }
    }

    private fun hideExitDialog() {
        _uiState.update { it.copy(showExitDialog = false) }
    }

    private fun exitQuiz() {
        quizTimer?.cancel()
        quizRepository.clearUsedImages()
        questions.clear()
        _uiState.update {
            QuizContract.UiState()
        }
        viewModelScope.launch {
            _sideEffect.send(QuizContract.SideEffect.NavigateBack)
        }
    }

    override fun onCleared() {
        super.onCleared()
        quizTimer?.cancel()
        quizRepository.clearUsedImages()
    }
}