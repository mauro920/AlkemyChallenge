package com.example.alkmovies.repository

import com.example.alkmovies.data.model.MovieList

interface MovieRepo {
    suspend fun getMovies(): MovieList
}