package com.example.catalist.features.ui.gallery

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catalist.features.cats.repository.CatsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.*

@HiltViewModel
class BreedGalleryViewModel @Inject constructor(
    private val catsRepository: CatsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val breedId: String = checkNotNull(savedStateHandle["breedId"])

    private val _state = MutableStateFlow(GalleryState(isLoading = true))
    val state: StateFlow<GalleryState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<GalleryEvent>()
    private val _effects = MutableSharedFlow<GalleryEffect>()
    val effects: SharedFlow<GalleryEffect> = _effects.asSharedFlow()

    init {
        observeEvents()
        sendEvent(GalleryEvent.LoadImages)
    }

    fun sendEvent(event: GalleryEvent) {
        viewModelScope.launch { _events.emit(event) }
    }

    private fun observeEvents() {
        viewModelScope.launch {
            _events.collect { event ->
                when (event) {
                    GalleryEvent.LoadImages,
                    GalleryEvent.Retry -> loadBreedImages()
                }
            }
        }
    }

    private fun loadBreedImages() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                catsRepository.refreshBreedImages(breedId)
            } catch (_: Exception) {
            }

            try {
                catsRepository.getBreedImages(breedId)
                    .collect { images ->
                        _state.update {
                            it.copy(
                                photos = images,
                                isLoading = false,
                                error = if (images.isEmpty()) "No photos available" else null
                            )
                        }
                    }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
                _effects.emit(GalleryEffect.ShowError("Failed to load images: ${e.message}"))
            }
        }
    }
}
