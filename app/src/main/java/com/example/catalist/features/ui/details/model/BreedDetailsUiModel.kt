package com.example.catalist.features.ui.details.model

data class BreedDetailsUiModel(
    val id: String,
    val name: String,
    val description: String?,
    val originCountries: List<String>?,
    val temperament: List<String>?,
    val lifeSpan: String?,
    val weight: String?,
    val height: String?,
    val reference_image_id: String?,
    val isRare: Boolean,
    val wikipediaUrl: String?,
    val adaptability: Int,
    val affectionLevel: Int,
    val childFriendly: Int,
    val dogFriendly: Int,
    val energyLevel: Int,
    val grooming: Int,
    val healthIssues: Int,
    val intelligence: Int,
    val sheddingLevel: Int,
    val socialNeeds: Int,
    val strangerFriendly: Int,
    val vocalisation: Int?
)