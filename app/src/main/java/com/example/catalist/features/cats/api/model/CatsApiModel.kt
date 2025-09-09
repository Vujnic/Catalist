package com.example.catalist.features.cats.api.model

import kotlinx.serialization.Serializable

@Serializable
data class CatsApiModel(
    val id: String,
    val name: String,
    val alt_names: String? = null,
    val description: String? = null,
    val temperament: String? = null,
    val origin: String? = null,
    val country_codes: String? = null,
    val life_span: String? = null,
    val weight: Weight?,
    val adaptability: Int = 0,
    val affection_level: Int = 0,
    val child_friendly: Int = 0,
    val dog_friendly: Int = 0,
    val energy_level: Int = 0,
    val grooming: Int = 0,
    val health_issues: Int = 0,
    val intelligence: Int = 0,
    val shedding_level: Int = 0,
    val social_needs: Int = 0,
    val stranger_friendly: Int = 0,
    val vocalisation: Int = 0,
    val rare: Int = 0,
    val wikipedia_url: String? = null,
    val reference_image_id: String? = null,
)

@Serializable
data class Weight(
    val imperial: String?  = "",
    val metric: String? = ""
)
