package com.example.alkmovies.repository

import com.example.alkmovies.data.model.MovieList
import com.example.alkmovies.data.remote.MovieDataSource

class MovieRepoImpl(private val dataSource: MovieDataSource): MovieRepo{
    override suspend fun getMovies(): MovieList = dataSource.getMovies()
}