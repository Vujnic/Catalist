package com.example.catalist.features.ui.details

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import coil3.compose.SubcomposeAsyncImage
import com.example.catalist.features.ui.details.model.BreedDetailsUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreedDetailScreen(
    viewModel: BreedDetailsViewModel,
    onBack: () -> Unit,
    onGalleryClick: (String) -> Unit
) {

    val effects = viewModel.effects
    LaunchedEffect(Unit) {
        effects.collect { effect ->
            when (effect) {
                is BreedDetailsEffect.ShowError -> {
                    println("Error: ${effect.message}")
                }
            }
        }
    }

    val state = viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = state.value.breed?.name ?: "Breed Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when {
                state.value.loading -> {
                    CircularProgressIndicator()
                }
                state.value.error != null -> {
                    Text(
                        text = state.value.error ?: "",
                        color = MaterialTheme.colorScheme.error
                    )
                }
                state.value.breed != null -> {
                    BreedDetailsContent(
                        breed = state.value.breed!!,
                        onGalleryClick = { onGalleryClick(state.value.breed!!.id) }
                    )
                }
                else -> {
                    Text(text = "No data available.")
                }
            }
        }
    }
}

@Composable
fun BreedDetailsContent(
    breed: BreedDetailsUiModel,
    onGalleryClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(breed.name, style = MaterialTheme.typography.headlineMedium)

            SubcomposeAsyncImage(
                model = breed.reference_image_id?.let {
                    "https://cdn2.thecatapi.com/images/$it.jpg"
                },
                contentDescription = breed.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                loading = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                },
                error = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.BrokenImage,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onGalleryClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("View Gallery")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Info Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        breed.description ?: "No description.",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    breed.originCountries?.let {
                        Text(
                            "Origin countries: ${it.joinToString()}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    breed.temperament?.let {
                        Text(
                            "Temperament: ${it.joinToString()}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Text(
                        "Life span: ${breed.lifeSpan}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "Weight: ${breed.weight ?: "Unknown"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "Rare breed: ${if (breed.isRare) "Yes" else "No"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!breed.wikipediaUrl.isNullOrBlank()) {
                val context = LocalContext.current
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, breed.wikipediaUrl.toUri())
                        ContextCompat.startActivity(context, intent, null)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Open Wikipedia page")
                }
            }
        }

        item {
            Text(
                "Traits",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BreedTrait("Adaptability", breed.adaptability)
                    BreedTrait("Affection level", breed.affectionLevel)
                    BreedTrait("Child friendliness", breed.childFriendly)
                    BreedTrait("Dog friendliness", breed.dogFriendly)
                    BreedTrait("Energy level", breed.energyLevel)
                    BreedTrait("Grooming", breed.grooming)
                    BreedTrait("Health issues", breed.healthIssues)
                    BreedTrait("Intelligence", breed.intelligence)
                    BreedTrait("Shedding", breed.sheddingLevel)
                    BreedTrait("Social needs", breed.socialNeeds)
                    BreedTrait("Stranger friendliness", breed.strangerFriendly)
                    BreedTrait("Vocalisation", breed.vocalisation ?: 0)
                }
            }
        }
    }
}

@Composable
fun BreedTrait(name: String, level: Int) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "$level/5",
                style = MaterialTheme.typography.bodySmall
            )
        }
        LinearProgressIndicator(
            progress = level / 5f,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
        )
    }
}