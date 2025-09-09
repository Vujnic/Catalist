package com.example.catalist.features.ui.details

import com.example.catalist.features.ui.details.model.BreedDetailsUiModel

data class BreedDetailsState(
    val loading: Boolean = false,
    val error: String? = null,
    val breed: BreedDetailsUiModel? = null
)

sealed class BreedDetailsEvent {
    object Load : BreedDetailsEvent()
    object Retry : BreedDetailsEvent()
}

sealed class BreedDetailsEffect {
    data class ShowError(val message: String) : BreedDetailsEffect()
}
