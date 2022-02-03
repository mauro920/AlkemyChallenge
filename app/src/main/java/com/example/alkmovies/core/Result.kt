package com.example.alkmovies.core

//Sealed class who Return a Result, or a failure, and stay in a Loading, while search for the data required.
sealed class Result<out T> {
    class Loading<out T>: Result<T>()
    data class Success<out T>(val data: T): Result<T>()
    data class Failure(val exception: Exception): Result<Nothing>()
}