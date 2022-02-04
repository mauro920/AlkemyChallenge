package com.example.alkmovies.repository

import com.example.alkmovies.data.local.LocalDataSource
import com.example.alkmovies.data.model.MovieList
import com.example.alkmovies.data.model.toMovieEntity
import com.example.alkmovies.data.remote.RemoteMovieDataSource

//Implementation of the repository
class MovieRepoImpl(
    private val dataSourceRemote: RemoteMovieDataSource,
    private val dataSourceLocal: LocalDataSource
) : MovieRepo {
    override suspend fun getMovies(page: Int): MovieList {
        dataSourceRemote.getMovies(page).results.forEach {
            dataSourceLocal.saveMovie(it.toMovieEntity())
        }
        return MovieList(dataSourceLocal.getMovies().results)
    }
}