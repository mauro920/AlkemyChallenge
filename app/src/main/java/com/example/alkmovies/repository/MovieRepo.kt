package com.example.alkmovies.repository

import com.example.alkmovies.data.model.MovieList
//Repository, interface.
interface MovieRepo {
    suspend fun getMovies(page:Int): MovieList
}