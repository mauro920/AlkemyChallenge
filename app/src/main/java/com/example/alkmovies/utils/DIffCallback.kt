package com.example.alkmovies.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.alkmovies.data.model.Movie


open class DiffCallback(
    private val oldMovieList: List<Movie>,
    private val newMovieList: List<Movie>
): DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldMovieList.size
    }

    override fun getNewListSize(): Int {
        return newMovieList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        // In the real world you need to compare something unique like id
        return oldMovieList[oldItemPosition] == newMovieList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        // This is called if areItemsTheSame() == true;
        return oldMovieList[oldItemPosition] == newMovieList[newItemPosition]
    }

}