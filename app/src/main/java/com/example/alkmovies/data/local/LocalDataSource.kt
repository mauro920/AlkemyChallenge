package com.example.alkmovies.data.local

import com.example.alkmovies.data.model.MovieEntity
import com.example.alkmovies.data.model.MovieList
import com.example.alkmovies.data.model.toMovieList

class LocalDataSource (private val movieDao: MovieDao){

    suspend fun getMovies(): MovieList = movieDao.getAllMovies().toMovieList()

    suspend fun saveMovie(movie: MovieEntity){
        movieDao.saveMovie(movie)
    }
}