package com.example.catalist.features.ui.gallery

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreedGalleryScreen(
    viewModel: BreedGalleryViewModel,
    onPhotoClick: (List<String>, Int) -> Unit,
    onBack: () -> Unit
) {
    val state = viewModel.state.collectAsState().value

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is GalleryEffect.ShowError -> {
                    println("Error: ${effect.message}")
                    // TODO: snackbar/Toast ako koristiš
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gallery") },
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
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                state.error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(state.error)
                        Button(onClick = { viewModel.sendEvent(GalleryEvent.Retry) }) {
                            Text("Retry")
                        }
                    }
                }
                state.photos.isEmpty() -> {
                    Text("No photos available", modifier = Modifier.align(Alignment.Center))
                }
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        contentPadding = PaddingValues(4.dp)
                    ) {
                        items(state.photos, key = { it.id }) { photo ->
                            AsyncImage(
                                model = photo.url,
                                contentDescription = null,
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .padding(4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        onPhotoClick(
                                            state.photos.map { it.url },
                                            state.photos.indexOf(photo)
                                        )
                                    },
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
        }
    }
}

/*@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreedGalleryScreen(
    viewModel: BreedGalleryViewModel,
    onPhotoClick: (List<String>, Int) -> Unit,
    onBack: () -> Unit
) {
    val state = viewModel.state.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gallery") },
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
                state.isLoading -> {
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
                        Button(onClick = { viewModel.retryLoading() }) {
                            Text("Retry")
                        }
                    }
                }
                state.photos.isEmpty() -> {
                    Text(
                        "No photos available",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        contentPadding = PaddingValues(4.dp)
                    ) {
                        items(
                            items = state.photos,
                            key = { it.id }
                        ) { photo ->
                            AsyncImage(
                                model = photo.url,
                                contentDescription = null,
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .padding(4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        onPhotoClick(
                                            state.photos.map { it.url },
                                            state.photos.indexOf(photo)
                                        )
                                    },
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
        }
    }
}*/