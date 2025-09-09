package com.example.catalist.features.quiz

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun QuizScreen(
    viewModel: QuizViewModel,
    onFinish: () -> Unit,
    onBack: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsState().value

    BackHandler {
        viewModel.onEvent(QuizContract.UiEvent.ExitQuiz)
    }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is QuizContract.SideEffect.NavigateBack -> onBack()
                is QuizContract.SideEffect.NavigateToLeaderboard -> onBack()
                is QuizContract.SideEffect.ShowError -> {
                    // TODO: Prikazati error toast
                }
            }
        }
    }

    Scaffold(
        topBar = {
            QuizTopBar(
                questionNumber = uiState.currentQuestionIndex + 1,
                totalQuestions = uiState.totalQuestions,
                remainingTime = uiState.remainingTimeSeconds,
                onExit = { viewModel.onEvent(QuizContract.UiEvent.ExitQuiz) }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.isFinished -> {
                    QuizResultScreen(
                        result = uiState.result!!,
                        onPublish = { viewModel.onEvent(QuizContract.UiEvent.PublishResult) }
                    )
                }
                uiState.currentQuestion != null -> {
                    QuizQuestionScreen(
                        question = uiState.currentQuestion!!,
                        onAnswerSelected = { answer ->
                            viewModel.onEvent(QuizContract.UiEvent.AnswerSelected(answer))
                        }
                    )
                }
            }

            if (uiState.showExitDialog) {
                ExitQuizDialog(
                    onConfirm = { viewModel.onEvent(QuizContract.UiEvent.ConfirmExit) },
                    onDismiss = { viewModel.onEvent(QuizContract.UiEvent.CancelExit) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuizTopBar(
    questionNumber: Int,
    totalQuestions: Int,
    remainingTime: Int,
    onExit: () -> Unit
) {
    TopAppBar(
        title = { Text("Question $questionNumber/$totalQuestions") },
        navigationIcon = {
            IconButton(onClick = onExit) {
                Icon(Icons.Default.Close, "Exit Quiz")
            }
        },
        actions = {
            Text(
                text = "${remainingTime / 60}:${(remainingTime % 60).toString().padStart(2, '0')}",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    )
}

@Composable
private fun QuizQuestionScreen(
    question: QuizQuestion,
    onAnswerSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = when (question) {
                is QuizQuestion.BreedGuess -> "What breed is this cat?"
                is QuizQuestion.TemperamentOutlier -> "Which trait does NOT belong to this cat?"
            },
            style = MaterialTheme.typography.headlineSmall
        )

        AsyncImage(
            model = question.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        question.options?.forEach { option ->
            Button(
                onClick = { onAnswerSelected(option) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(option)
            }
        }
    }
}

@Composable
private fun QuizResultScreen(
    result: QuizResult,
    onPublish: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Quiz Complete!",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            "Correct answers: ${result.correctAnswers}/20",
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            "Score: ${result.calculateScore().toInt()}",
            style = MaterialTheme.typography.displayMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onPublish,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Publish Result")
        }
    }
}

@Composable
private fun ExitQuizDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Exit Quiz?") },
        text = { Text("Are you sure you want to exit? Your progress will be lost.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Exit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}