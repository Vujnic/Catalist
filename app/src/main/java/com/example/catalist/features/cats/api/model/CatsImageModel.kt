package com.example.catalist.features.cats.api.model

import kotlinx.serialization.Serializable

@Serializable
data class CatImageModel(
    val id: String,
    val url: String,
    val breeds: List<CatsApiModel>? = null
)