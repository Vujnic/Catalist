package com.example.catalist.features.ui.list

import com.example.catalist.features.ui.list.model.CatsUiModel


interface CatsListScreenContract {

    data class UiState(
        val cats: List<CatsUiModel> = emptyList(),
        val filteredCats: List<CatsUiModel> = emptyList(),
        val loading: Boolean = false,
        val query: String = ""
    )

    sealed class UiEvent {
        data class Search(val query: String) : UiEvent()
        object Refresh : UiEvent()
    }

    sealed class SideEffect {
        data class Error(val message: String) : SideEffect()
    }
}