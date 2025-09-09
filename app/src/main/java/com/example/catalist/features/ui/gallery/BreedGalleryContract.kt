package com.example.catalist.features.ui.gallery

import com.example.catalist.features.cats.room.CatImageEntity

data class GalleryState(
    val photos: List<CatImageEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class GalleryEvent {
    object LoadImages : GalleryEvent()
    object Retry : GalleryEvent()
}

sealed class GalleryEffect {
    data class ShowError(val message: String) : GalleryEffect()
}
