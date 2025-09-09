package com.example.catalist.features.cats.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CatBreedDao {
    // Dohvata sve rase kao Flow (reaktivno)
    @Query("SELECT * FROM cat_breeds")
    fun getAllBreeds(): Flow<List<CatBreedEntity>>

    // Dohvata jednu rasu po ID-u
    @Query("SELECT * FROM cat_breeds WHERE id = :breedId")
    fun getBreedById(breedId: String): Flow<CatBreedEntity?>

    // Ubacuje ili ažurira rase u bazi
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBreeds(breeds: List<CatBreedEntity>)

    // Pretraga rasa po imenu
    @Query("SELECT * FROM cat_breeds WHERE name LIKE '%' || :query || '%'")
    fun searchBreeds(query: String): Flow<List<CatBreedEntity>>

    @Query("SELECT COUNT(*) FROM cat_breeds")
    suspend fun getBreedCount(): Int

    @Query("SELECT * FROM cat_breeds")
    suspend fun getAllBreedsSync(): List<CatBreedEntity>
}
