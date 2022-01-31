package com.example.alkmovies.data.remote

import com.example.alkmovies.data.model.MovieList
import com.example.alkmovies.repository.APIService
import com.example.alkmovies.utils.Constants

class MovieDataSource(private val apiService: APIService) {

    suspend fun getMovies(page: Int): MovieList = apiService.getMovies(Constants.API_KEY, page)
}