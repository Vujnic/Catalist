package com.example.catalist.core.di

import com.example.catalist.features.cats.api.CatsApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object CatsModule {

    @Provides
    @Singleton
    fun provideCatsApi(retrofit: Retrofit): CatsApi = retrofit.create()

    private val json = Json {
        ignoreUnknownKeys = true
    }

    class ApiKeyInterceptor : Interceptor {
        private val apiKey = "live_Tob4FgeNA9uxT4DuDtHhvn0cp4nplktxRWHCRjNhmOQs4hu7QmZ3ndf6roTpdNdf"

        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request().newBuilder()
                .addHeader("x-api-key", apiKey)
                .build()
            return chain.proceed(request)
        }
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl("https://api.thecatapi.com/v1/")
            .addConverterFactory(json.asConverterFactory(contentType))
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(ApiKeyInterceptor())
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    setLevel(HttpLoggingInterceptor.Level.BODY)
                }
            )
            .build()
    }
}


