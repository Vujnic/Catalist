package com.example.catalist.features.cats.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CatImageDao {
    // Dohvata sve slike za određenu rasu
    @Query("SELECT * FROM cat_images WHERE breedId = :breedId")
    fun getBreedImages(breedId: String): Flow<List<CatImageEntity>>

    // Ubacuje ili ažurira slike u bazi
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImages(images: List<CatImageEntity>)

    // Briše sve slike za određenu rasu
    @Query("DELETE FROM cat_images WHERE breedId = :breedId")
    suspend fun deleteBreedImages(breedId: String)

    @Query("SELECT COUNT(*) FROM cat_images WHERE breedId = :breedId")
    suspend fun getImageCount(breedId: String): Int

    @Query("SELECT * FROM cat_images WHERE breedId = :breedId")
    suspend fun getBreedImagesSync(breedId: String): List<CatImageEntity>
}