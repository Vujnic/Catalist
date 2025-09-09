package com.example.catalist.features.quiz

interface QuizContract {
    data class UiState(
        val currentQuestion: QuizQuestion? = null,
        val currentQuestionIndex: Int = 0,
        val totalQuestions: Int = 20,
        val remainingTimeSeconds: Int = 300,
        val answers: Map<Int, Boolean> = emptyMap(),
        val isLoading: Boolean = true,
        val error: String? = null,
        val isFinished: Boolean = false,
        val showExitDialog: Boolean = false,
        val result: QuizResult? = null
    )

    sealed interface UiEvent {
        data class AnswerSelected(val answer: String) : UiEvent
        object NextQuestion : UiEvent
        object ExitQuiz : UiEvent
        object ConfirmExit : UiEvent
        object CancelExit : UiEvent
        object PublishResult : UiEvent
    }

    sealed interface SideEffect {
        data class ShowError(val message: String) : SideEffect
        object NavigateToLeaderboard : SideEffect
        object NavigateBack : SideEffect
    }
}