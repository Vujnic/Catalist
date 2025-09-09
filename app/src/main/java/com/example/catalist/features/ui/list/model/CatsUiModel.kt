package com.example.catalist.features.ui.list.model

data class CatsUiModel (
    val id: String,
    val name: String,
    val reference_image_id: String? = null,
    val alt_names: String? = null,
    val description: String? = null,
    val temperament: List<String>? = null,
)