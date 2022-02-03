package com.example.alkmovies.utils

import MovieAdapter
import androidx.recyclerview.widget.GridLayoutManager
import com.example.alkmovies.data.model.Movie

//Set of variables for movie fragment use
object Utils {
    lateinit var layoutManager: GridLayoutManager
    lateinit var adapter: MovieAdapter
    var isLoading = false
    var page = 1
    var moviesList: MutableList<Movie> = mutableListOf()
}