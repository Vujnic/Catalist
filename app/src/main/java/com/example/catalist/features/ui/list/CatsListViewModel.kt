package com.example.catalist.features.ui.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catalist.features.cats.repository.CatsRepository
import com.example.catalist.features.cats.room.CatBreedEntity
import com.example.catalist.features.ui.list.CatsListScreenContract.UiEvent
import com.example.catalist.features.ui.list.CatsListScreenContract.UiState
import com.example.catalist.features.ui.list.model.CatsUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CatsListViewModel @Inject constructor(
    private val repository: CatsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()
    private fun setState(reduce: UiState.() -> UiState) =
        _state.update { it.reduce() }

    private val _events = MutableSharedFlow<UiEvent>()
    fun setEvent(event: UiEvent) = viewModelScope.launch { _events.emit(event) }

    init {
        observeEvents()
        loadInitialData()
    }

    private fun observeEvents() = viewModelScope.launch {
        _events.collect { event ->
            when (event) {
                is UiEvent.Refresh -> {
                    refreshCats()
                }
                is UiEvent.Search -> {
                    setState {
                        val filtered = if (event.query.isBlank()) cats
                        else cats.filter { it.name.startsWith(event.query, ignoreCase = true) }
                        copy(query = event.query, filteredCats = filtered)
                    }
                }
            }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            setState { copy(loading = true) }
            try {
                repository.refreshBreeds()

                // Pratimo podatke iz baze
                repository.allBreeds.collect { breeds ->
                    val uiModels = breeds.map { it.toUiModel() }
                    setState {
                        copy(
                            cats = uiModels,
                            filteredCats = uiModels,
                            loading = false
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("CatsListViewModel", "Error loading cats", e)
                setState { copy(loading = false) }
            }
        }
    }

    private fun refreshCats() {
        viewModelScope.launch {
            setState { copy(loading = true) }
            try {
                repository.refreshBreeds()
                setState { copy(loading = false) }
            } catch (e: Exception) {
                Log.e("CatsListViewModel", "Error refreshing cats", e)
                setState { copy(loading = false) }
            }
        }
    }

    private fun CatBreedEntity.toUiModel() = CatsUiModel(
        id = id,
        name = name,
        reference_image_id = reference_image_id,
        alt_names = null,
        description = description?.take(250),
        temperament = temperament?.split(", ")?.take(5)
    )
}