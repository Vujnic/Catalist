package com.example.catalist.features.cats.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cat_breeds")
data class CatBreedEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String?,
    val temperament: String?,
    val reference_image_id: String?,
    val origin: String?,
    val lifeSpan: String?,
    val weight: String?,
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
    val vocalisation: Int,
    val rare: Boolean,
    val wikipediaUrl: String?
)
