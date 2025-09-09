package com.example.catalist.features.ui.viewer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.example.catalist.features.ui.viewer.PhotoViewerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoViewerScreen(
    viewModel: PhotoViewerViewModel,
    onBack: () -> Unit
) {
    val state = viewModel.state.collectAsState().value

    val effects = viewModel.effects
    LaunchedEffect(Unit) {
        effects.collect { effect ->
            when (effect) {
                is PhotoViewerEffect.ShowError -> {
                    println("Error: ${effect.message}")
                }
            }
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Photo ${state.currentIndex + 1}/${state.photos.size}") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
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
                state.loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                state.error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(state.error)
                        Button(onClick = {
                            viewModel.sendEvent(PhotoViewerEvent.Retry)
                        }) {
                            Text("Retry")
                        }
                    }
                }
                state.photos.isNotEmpty() -> {
                    val pagerState = rememberPagerState(
                        initialPage = state.currentIndex,
                        pageCount = { state.photos.size }
                    )

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        AsyncImage(
                            model = state.photos[page],
                            modifier = Modifier.fillMaxSize(),
                            contentDescription = null,
                            contentScale = ContentScale.Fit
                        )
                    }

                    LaunchedEffect(pagerState.currentPage) {
                        viewModel.sendEvent(PhotoViewerEvent.ChangePage(pagerState.currentPage))
                    }

                }
                else -> {
                    Text(
                        "No photos available",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}