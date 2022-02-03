package com.example.alkmovies.repository

import com.example.alkmovies.data.model.MovieList
import com.example.alkmovies.data.remote.MovieDataSource
//Implementation of the repository
class MovieRepoImpl(private val dataSource: MovieDataSource) : MovieRepo {
    override suspend fun getMovies(page: Int): MovieList = dataSource.getMovies(page)
}