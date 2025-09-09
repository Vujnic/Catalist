package com.example.catalist.features.ui.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.example.catalist.features.ui.list.model.CatsUiModel
import kotlinx.coroutines.launch

@Composable
fun CatsListScreen(
    viewModel: CatsListViewModel,
    onUserClick: (String) -> Unit
) {
    val uiState = viewModel.state.collectAsState()
    CatsListScreen(
        state = uiState.value,
        onEvent = viewModel::setEvent,
        onUserClick = onUserClick,
    )
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CatsListScreen(
    state: CatsListScreenContract.UiState,
    onEvent: (CatsListScreenContract.UiEvent) -> Unit,
    onUserClick: (String) -> Unit
) {
    val uiScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val showScrollToTop by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Breeds") })
        },
        floatingActionButton = {
            if (showScrollToTop) {
                FloatingActionButton(onClick = {
                    uiScope.launch { listState.scrollToItem(index = 0) }
                }) {}
            }
        },
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    OutlinedTextField(
                        value = state.query,
                        onValueChange = { onEvent(CatsListScreenContract.UiEvent.Search(it)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        label = { Text("Search breeds") },
                        singleLine = true,
                        trailingIcon = {
                            if (state.query.isNotEmpty()) {
                                IconButton(onClick = { onEvent(CatsListScreenContract.UiEvent.Search("")) }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear"
                                    )
                                }
                            }
                        }
                    )
                }


                itemsIndexed(state.filteredCats) { index, cat ->
                    CatsListItem(model = cat, onUserClick = onUserClick)
                }

            }
        }
    )
}

@Composable
private fun CatsListItem(
    model: CatsUiModel,
    onUserClick: (String) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable { onUserClick(model.id) },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .height(IntrinsicSize.Min)
        ) {
            SubcomposeAsyncImage(
                model = "https://cdn2.thecatapi.com/images/${model.reference_image_id}.jpg",
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp)),
                loading = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                },
                error = {
                    Icon(
                        imageVector = Icons.Default.BrokenImage,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(48.dp)
                    )
                }
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = 8.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = model.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                model.alt_names?.let {
                    if (it.isNotEmpty()) {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Text(
                    text = model.description.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    overflow = TextOverflow.Ellipsis
                )

                if (model.temperament?.isNotEmpty() == true) {
                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 8.dp)
                    ) {
                        model.temperament.take(5).forEach { temperament ->
                            AssistChip(
                                onClick = {},
                                label = { Text(temperament) },
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}