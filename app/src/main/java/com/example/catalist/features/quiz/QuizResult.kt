package com.example.catalist.features.quiz

data class QuizResult(
    val correctAnswers: Int,
    val remainingTimeSeconds: Int,
    val totalTimeSeconds: Int = 300,
) {
    fun calculateScore(): Float {
        val timeBonus = (remainingTimeSeconds + 120) / totalTimeSeconds.toFloat()
        val baseScore = correctAnswers * 2.5f
        val finalScore = baseScore * (1 + timeBonus)
        return finalScore.coerceAtMost(100f)
    }

}