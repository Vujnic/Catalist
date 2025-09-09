package com.example.catalist.features.ui.viewer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catalist.features.cats.repository.CatsRepository
import com.example.catalist.features.cats.room.CatImageEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.map


@HiltViewModel
class PhotoViewerViewModel @Inject constructor(
    private val repository: CatsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val breedId: String = checkNotNull(savedStateHandle["breedId"]) {
        "breedId parameter is missing"
    }

    private val initialIndex: Int = savedStateHandle["photoIndex"] ?: 0

    private val _state = MutableStateFlow(PhotoViewerState(currentIndex = initialIndex))
    val state: StateFlow<PhotoViewerState> = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<PhotoViewerEvent>()
    private val _effectFlow = MutableSharedFlow<PhotoViewerEffect>()
    val effects: SharedFlow<PhotoViewerEffect> = _effectFlow.asSharedFlow()

    init {
        observeEvents()
        sendEvent(PhotoViewerEvent.LoadPhotos)
    }

    fun sendEvent(event: PhotoViewerEvent) {
        viewModelScope.launch { _eventFlow.emit(event) }
    }

    private fun observeEvents() {
        viewModelScope.launch {
            _eventFlow.collect { event ->
                when (event) {
                    is PhotoViewerEvent.LoadPhotos,
                    is PhotoViewerEvent.Retry -> loadPhotos()

                    is PhotoViewerEvent.ChangePage -> {
                        _state.update { it.copy(currentIndex = event.index) }
                    }
                }
            }
        }
    }

    private fun loadPhotos() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }

            try {
                try {
                    repository.refreshBreedImages(breedId)
                } catch (_: Exception) {
                }

                repository.getBreedImages(breedId)
                    .collect { images ->
                        val urls = images.map(CatImageEntity::url)
                        _state.update {
                            it.copy(
                                photos = urls,
                                loading = false,
                                error = if (urls.isEmpty()) "No photos available" else null
                            )
                        }
                    }
            } catch (e: Exception) {
                val errorMsg = "Error loading photos: ${e.message}"
                _state.update { it.copy(loading = false, error = errorMsg) }
                _effectFlow.emit(PhotoViewerEffect.ShowError(errorMsg))
            }
        }
    }
}

