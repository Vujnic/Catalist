package com.example.catalist.features.cats.api

import com.example.catalist.features.cats.api.model.CatImageModel
import com.example.catalist.features.cats.api.model.CatsApiModel
import retrofit2.http.GET
import retrofit2.http.Query

interface CatsApi {

    @GET("breeds")
    suspend fun getAllCats(): List<CatsApiModel>

    @GET("images/search")
    suspend fun getBreedImages(
        @Query("breed_ids") breedId: String,
        @Query("limit") limit: Int = 10,
        @Query("page") page: Int = 0,
        @Query("order") order: String = "ASC"
    ): List<CatImageModel>
}