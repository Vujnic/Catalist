package com.example.catalist.features.leaderboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    viewModel: LeaderboardViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Leaderboard") },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
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
                uiState.value.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.value.error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
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
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.value.entries) { entry ->
                            LeaderboardEntryItem(entry)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardEntryItem(
    entry: LeaderboardViewModel.LeaderboardEntryUi
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#${entry.position}",
                    style = MaterialTheme.typography.titleLarge,
                    color = when(entry.position) {
                        1 -> MaterialTheme.colorScheme.primary
                        2 -> MaterialTheme.colorScheme.secondary
                        3 -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )

                Column {
                    Text(
                        text = entry.nickname,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${entry.gamesPlayed} games played",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Text(
                text = "%.1f".format(entry.score),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}