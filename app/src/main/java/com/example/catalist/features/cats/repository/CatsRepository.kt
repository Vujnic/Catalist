package com.example.catalist.features.cats.repository

import com.example.catalist.features.cats.api.CatsApi
import com.example.catalist.features.cats.api.model.CatImageModel
import com.example.catalist.features.cats.api.model.CatsApiModel
import com.example.catalist.features.cats.room.CatBreedDao
import com.example.catalist.features.cats.room.CatBreedEntity
import com.example.catalist.features.cats.room.CatImageDao
import com.example.catalist.features.cats.room.CatImageEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CatsRepository @Inject constructor(
    private val catsApi: CatsApi,
    private val catBreedDao: CatBreedDao,
    private val catImageDao: CatImageDao
) {

    val allBreeds: Flow<List<CatBreedEntity>> = catBreedDao.getAllBreeds()

    fun getBreedById(id: String): Flow<CatBreedEntity?> =
        catBreedDao.getBreedById(id)

    suspend fun refreshBreeds() {
        withContext(Dispatchers.IO) {
            try {
                val breeds = catsApi.getAllCats().map { it.toEntity() }
                catBreedDao.insertBreeds(breeds)

                coroutineScope {
                    breeds.map { breed ->
                        async {
                            try {
                                if (!hasLocalImages(breed.id)) {
                                    refreshBreedImages(breed.id)
                                }
                            } catch (e: Exception) {
                                println("Error loading images for breed ${breed.id}: ${e.message}")
                            }
                        }
                    }.awaitAll()
                }
            } catch (e: Exception) {
                println("Error refreshing breeds: ${e.message}")
            }
        }
    }

    fun getBreedImages(breedId: String): Flow<List<CatImageEntity>> =
        catImageDao.getBreedImages(breedId)

    private val imageLoadingSemaphore = Semaphore(5) // Maksimalno 5 paralelnih zahteva

    suspend fun refreshBreedImages(breedId: String) {
        imageLoadingSemaphore.withPermit {
            withContext(Dispatchers.IO) {
                try {
                    val images = catsApi.getBreedImages(breedId = breedId, limit = 10)
                        .map { it.toEntity(breedId) }
                    catImageDao.insertImages(images)
                } catch (e: Exception) {
                    println("Error refreshing images: ${e.message}")
                }
            }
        }
    }

    suspend fun hasLocalBreeds(): Boolean =
        withContext(Dispatchers.IO) {
            catBreedDao.getBreedCount() > 0
        }

    suspend fun hasLocalImages(breedId: String): Boolean =
        withContext(Dispatchers.IO) {
            catImageDao.getImageCount(breedId) > 0
        }
}

private fun CatsApiModel.toEntity() = CatBreedEntity(
    id = id,
    name = name,
    description = description ?: "",
    temperament = temperament ?: "",
    reference_image_id = reference_image_id,
    origin = origin ?: "",
    lifeSpan = life_span ?: "",
    weight = weight?.metric ?: "",
    adaptability = adaptability,
    affectionLevel = affection_level,
    childFriendly = child_friendly,
    dogFriendly = dog_friendly,
    energyLevel = energy_level,
    grooming = grooming,
    healthIssues = health_issues,
    intelligence = intelligence,
    sheddingLevel = shedding_level,
    socialNeeds = social_needs,
    strangerFriendly = stranger_friendly,
    vocalisation = vocalisation,
    rare = rare == 1,
    wikipediaUrl = wikipedia_url
)

private fun CatImageModel.toEntity(breedId: String) = CatImageEntity(
    id = id,
    breedId = breedId,
    url = url
)