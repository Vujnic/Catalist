package com.example.catalist.features.ui.viewer


data class PhotoViewerState(
    val photos: List<String> = emptyList(),
    val currentIndex: Int = 0,
    val loading: Boolean = true,
    val error: String? = null
)

sealed class PhotoViewerEvent {
    object LoadPhotos : PhotoViewerEvent()
    data class ChangePage(val index: Int) : PhotoViewerEvent()
    object Retry : PhotoViewerEvent()
}

sealed class PhotoViewerEffect {
    data class ShowError(val message: String) : PhotoViewerEffect()
}
