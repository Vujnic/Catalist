package com.example.catalist.features.profile

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile") },
                actions = {
                    // Dodato refresh dugme
                    IconButton(
                        onClick = { viewModel.refresh() },
                        enabled = !uiState.value.isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                uiState.value.error != null -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = uiState.value.error!!,
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(onClick = { viewModel.refresh() }) {
                            Text("Retry")
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // User Info Section
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        "Account Information",
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                    Text("Full Name: ${uiState.value.fullName}")
                                    Text("Nickname: ${uiState.value.nickname}")
                                    Text("Email: ${uiState.value.email}")
                                }
                            }
                        }

                        // Stats Section
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        "Statistics",
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                    Text("Best Score: ${String.format("%.1f", uiState.value.bestScore)}")
                                    Text("Best Ranking: #${uiState.value.bestRanking}")
                                    Text("Total Games: ${uiState.value.quizResults.size}")
                                }
                            }
                        }

                        // Quiz History Section
                        item {
                            Text(
                                "Quiz History",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }

                        items(
                            items = uiState.value.quizResults,
                            key = { it.timestamp },
                        ) { result ->
                            QuizResultItem(result)
                        }
                    }
                }
            }

            // Loading indicator overlay
            if (uiState.value.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
private fun QuizResultItem(result: ProfileViewModel.QuizResultUi) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = String.format("Score: %.1f", result.score),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                        .format(Date(result.timestamp)),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (result.isPublished) {
                Icon(
                    imageVector = Icons.Default.Public,
                    contentDescription = "Published",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}