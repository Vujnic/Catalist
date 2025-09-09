package com.example.catalist.features.cats.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cat_images")
data class CatImageEntity(
    @PrimaryKey val id: String,
    val breedId: String,
    val url: String
)