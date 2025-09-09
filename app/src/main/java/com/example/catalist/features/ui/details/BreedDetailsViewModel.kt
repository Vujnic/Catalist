package com.example.catalist.features.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catalist.features.cats.repository.CatsRepository
import com.example.catalist.features.cats.room.CatBreedEntity
import com.example.catalist.features.ui.details.model.BreedDetailsUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.*

@HiltViewModel
class BreedDetailsViewModel @Inject constructor(
    private val repository: CatsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val breedId: String = checkNotNull(savedStateHandle[BREED_ID_ARG]) {
        "Breed ID is required"
    }

    private val _state = MutableStateFlow(BreedDetailsState(loading = true))
    val state: StateFlow<BreedDetailsState> = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<BreedDetailsEvent>()
    private val _effectFlow = MutableSharedFlow<BreedDetailsEffect>()
    val effects: SharedFlow<BreedDetailsEffect> = _effectFlow.asSharedFlow()

    init {
        observeEvents()
        sendEvent(BreedDetailsEvent.Load)
    }

    fun sendEvent(event: BreedDetailsEvent) {
        viewModelScope.launch { _eventFlow.emit(event) }
    }

    private fun observeEvents() {
        viewModelScope.launch {
            _eventFlow.collect { event ->
                when (event) {
                    BreedDetailsEvent.Load,
                    BreedDetailsEvent.Retry -> loadBreed()
                }
            }
        }
    }

    private fun loadBreed() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }

            try {
                try {
                    repository.refreshBreeds()
                } catch (_: Exception) {
                    // fallback na lokalne podatke
                }

                repository.getBreedById(breedId).collect { breedEntity ->
                    _state.update {
                        it.copy(
                            breed = breedEntity?.toUiModel(),
                            loading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(loading = false, error = e.message) }
                _effectFlow.emit(BreedDetailsEffect.ShowError("Greška: ${e.message}"))
            }
        }
    }

    private fun CatBreedEntity.toUiModel() = BreedDetailsUiModel(
        id = id,
        name = name,
        description = description,
        originCountries = origin?.split(",")?.map { it.trim() },
        temperament = temperament?.split(",")?.map { it.trim() },
        lifeSpan = lifeSpan,
        weight = weight,
        height = null,
        reference_image_id = reference_image_id,
        isRare = rare,
        wikipediaUrl = wikipediaUrl,
        adaptability = adaptability,
        affectionLevel = affectionLevel,
        childFriendly = childFriendly,
        dogFriendly = dogFriendly,
        energyLevel = energyLevel,
        grooming = grooming,
        healthIssues = healthIssues,
        intelligence = intelligence,
        sheddingLevel = sheddingLevel,
        socialNeeds = socialNeeds,
        strangerFriendly = strangerFriendly,
        vocalisation = vocalisation
    )

    companion object {
        private const val BREED_ID_ARG = "breedId"
    }
}


