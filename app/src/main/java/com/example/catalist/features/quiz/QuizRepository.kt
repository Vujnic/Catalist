package com.example.catalist.features.quiz

import com.example.catalist.features.cats.room.CatBreedDao
import com.example.catalist.features.cats.room.CatImageDao
import jakarta.inject.Inject

class QuizRepository @Inject constructor(
    private val catBreedDao: CatBreedDao,
    private val catImageDao: CatImageDao
) {
    private val usedImages = mutableSetOf<String>()

    suspend fun generateBreedQuestion(): QuizQuestion.BreedGuess {
        val allBreeds = catBreedDao.getAllBreedsSync()
        val correctBreed = allBreeds.random()
        val images = catImageDao.getBreedImagesSync(correctBreed.id)
        val unusedImages = images.filterNot { it.id in usedImages }

        val selectedImage = unusedImages.random()
        usedImages.add(selectedImage.id)

        val incorrectBreeds = allBreeds
            .filter { it.id != correctBreed.id }
            .shuffled()
            .take(3)
            .map { it.name }

        return QuizQuestion.BreedGuess(
            id = selectedImage.id,
            imageUrl = selectedImage.url,
            correctAnswer = correctBreed.name,
            options = (incorrectBreeds + correctBreed.name).shuffled()
        )
    }

    suspend fun generateTemperamentQuestion(): QuizQuestion.TemperamentOutlier {
        val allBreeds = catBreedDao.getAllBreedsSync()
        val correctBreed = allBreeds.random()
        val images = catImageDao.getBreedImagesSync(correctBreed.id)
        val unusedImages = images.filterNot { it.id in usedImages }

        val selectedImage = unusedImages.random()
        usedImages.add(selectedImage.id)

        val correctTemperaments = correctBreed.temperament?.split(", ")
        val allTemperaments = allBreeds
            .flatMap { it.temperament?.split(", ") ?: emptyList() }
            .distinct()
            .filter { correctTemperaments?.contains(it) != true  }

        val outlierTemperament = allTemperaments.random()
        val options = (correctTemperaments?.take(3)?.plus(outlierTemperament))?.shuffled()

        return QuizQuestion.TemperamentOutlier(
            id = selectedImage.id,
            imageUrl = selectedImage.url,
            correctAnswer = outlierTemperament,
            options = options
        )
    }

    fun clearUsedImages() {
        usedImages.clear()
    }
}