package com.example.alkmovies.ui.movie.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.alkmovies.core.BaseViewHolder
import com.example.alkmovies.data.model.Movie
import com.example.alkmovies.databinding.MovieItemBinding
import com.example.alkmovies.databinding.ViewholderLoadMoreItemsBinding

class MovieAdapter(
    private val moviesList: List<Movie?>,
    private val itemClickListener: OnMovieClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnMovieClickListener {
        fun onMovieClick(movie: Movie?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemBinding =
            MovieItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val itemLoadingBinding =
            ViewholderLoadMoreItemsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        val holder = MoviesViewHolder(itemBinding, parent.context)
        val loadingHolder = LoadingViewHolder(itemLoadingBinding)
        itemBinding.root.setOnClickListener {
            val position =
                holder.bindingAdapterPosition.takeIf { it != DiffUtil.DiffResult.NO_POSITION }
                    ?: return@setOnClickListener
            itemClickListener.onMovieClick(moviesList[position])
        }
        return when (viewType) {
            0 -> holder
            else -> loadingHolder
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MoviesViewHolder -> holder.bind(moviesList[position])
        }
    }

    override fun getItemCount(): Int = moviesList.size + 1

    override fun getItemViewType(position: Int): Int {
        return if (position < moviesList.size) 0
        else 1
    }

    private class MoviesViewHolder(
        val binding: MovieItemBinding,
        val context: Context
    ) : BaseViewHolder<Movie>(binding.root) {
        override fun bind(item: Movie?) {
            binding.tvMovieName.text = item?.title
            Glide.with(context).load("https://image.tmdb.org/t/p/w500/${item?.poster_path}")
                .centerCrop().into(binding.imgMovie)
        }
    }

    private class LoadingViewHolder(
        binding: ViewholderLoadMoreItemsBinding
    ) : RecyclerView.ViewHolder(binding.root)
}