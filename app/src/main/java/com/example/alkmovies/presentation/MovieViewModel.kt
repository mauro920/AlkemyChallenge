package com.example.alkmovies.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.alkmovies.core.Result
import com.example.alkmovies.repository.MovieRepo
import kotlinx.coroutines.Dispatchers

class MovieViewModel(private val repo: MovieRepo): ViewModel() {

    fun fetchMovies(page:Int) = liveData(viewModelScope.coroutineContext + Dispatchers.Main){
        emit(Result.Loading())
        try {
            emit(Result.Success(repo.getMovies(page)))

        } catch (e: Exception){
            emit(Result.Failure(e))
        }
    }
}

class MovieViewModelFactory(private val repo: MovieRepo):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(MovieRepo::class.java).newInstance(repo)
    }
}