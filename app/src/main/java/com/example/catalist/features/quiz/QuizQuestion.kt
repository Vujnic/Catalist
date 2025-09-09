package com.example.catalist.features.quiz

sealed class QuizQuestion {
    abstract val id: String
    abstract val imageUrl: String
    abstract val correctAnswer: String
    abstract val options: List<String>?

    data class BreedGuess(
        override val id: String,
        override val imageUrl: String,
        override val correctAnswer: String,
        override val options: List<String>
    ) : QuizQuestion()

    data class TemperamentOutlier(
        override val id: String,
        override val imageUrl: String,
        override val correctAnswer: String,
        override val options: List<String>?
    ) : QuizQuestion()
}