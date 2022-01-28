package com.example.alkmovies.repository

import com.example.alkmovies.data.model.MovieList
import com.example.alkmovies.utils.Constants
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface APIService {
    @GET("movie/popular")
    suspend fun getMovies(@Query("api_key")apiKey: String): MovieList
}

object RetrofitClient{
    val apiservice by lazy{
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build().create(APIService::class.java)
    }
}