package com.example.alkmovies.ui.movie

import MovieAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alkmovies.R
import com.example.alkmovies.core.Result
import com.example.alkmovies.data.model.Movie
import com.example.alkmovies.data.remote.MovieDataSource
import com.example.alkmovies.databinding.FragmentMovieBinding
import com.example.alkmovies.presentation.MovieViewModel
import com.example.alkmovies.presentation.MovieViewModelFactory
import com.example.alkmovies.repository.MovieRepoImpl
import com.example.alkmovies.repository.RetrofitClient


class MovieFragment : Fragment(R.layout.fragment_movie), MovieAdapter.OnMovieClickListener {

    lateinit var layoutManager: GridLayoutManager
    lateinit var adapter: MovieAdapter
    private lateinit var binding: FragmentMovieBinding
    private var isLoading = false
    private var page = 1
    private var moviesList: MutableList<Movie> = mutableListOf()
    private val viewModel by viewModels<MovieViewModel> {
        MovieViewModelFactory(
            MovieRepoImpl(
                MovieDataSource(RetrofitClient.apiservice)
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMovieBinding.bind(view)
        layoutManager = GridLayoutManager(activity, 3)
        binding.rvMovieList.layoutManager = layoutManager
        adaptMovies()

        binding.rvMovieList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val visibleItemCount = layoutManager.childCount
                val pastVisibleItem = layoutManager.findFirstCompletelyVisibleItemPosition()
                val total = adapter.itemCount

                if (!isLoading) {
                    if ((visibleItemCount + pastVisibleItem) >= total) {
                        adaptMovies()
                    }
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }

    override fun onMovieClick(movie: Movie) {
        val action = MovieFragmentDirections.actionMovieFragmentToMovieDetailsFragment(
            movie.poster_path,
            movie.backdrop_path,
            movie.vote_average.toFloat(),
            movie.vote_count,
            movie.overview,
            movie.title,
            movie.original_language,
            movie.release_date
        )
        findNavController().navigate(action)
    }

    @SuppressLint("SetTextI18n")
    private fun adaptMovies() {
        binding.progressBar.visibility = View.VISIBLE

        viewModel.fetchMovies(page).observe(viewLifecycleOwner, Observer { movies ->
            if (::adapter.isInitialized) {
                when (movies) {
                    is Result.Loading -> {
                        isLoading = true
                        binding.progressBar.visibility = View.VISIBLE
                        binding.tvError.visibility = View.GONE
                        Log.d("LiveData", "LOADING...")
                    }
                    is Result.Success -> {
                        ++page
                        movies.data.results.forEach {
                            moviesList.add(it)
                        }
                        binding.progressBar.visibility = View.GONE
                        adapter.updateList(moviesList)
                        isLoading = false
                        Log.d("LiveData", "${movies.data}")

                    }
                    is Result.Failure -> {
                        binding.progressBar.visibility = View.GONE
                        binding.tvError.visibility = View.VISIBLE
                        binding.rvMovieList.visibility = View.GONE
                        binding.tvError.text = "Error: ${movies.exception}"
                        isLoading = false
                        Log.d("LiveData", "${movies.exception}")
                    }
                }
            } else {
                when (movies) {
                    is Result.Loading -> {
                        isLoading = true
                        binding.tvError.visibility = View.GONE
                        binding.rvMovieList.visibility = View.GONE
                        Log.d("LiveData", "LOADING...")
                    }
                    is Result.Success -> {
                        movies.data.results.forEach {
                            moviesList.add(it)
                        }
                        binding.progressBar.visibility = View.GONE
                        binding.tvError.visibility = View.GONE
                        binding.rvMovieList.visibility = View.VISIBLE
                        adapter = MovieAdapter(movies.data.results, this)
                        binding.rvMovieList.adapter = adapter
                        isLoading = false
                        ++page
                        Log.d("LiveData", "${movies.data}")

                    }
                    is Result.Failure -> {
                        binding.progressBar.visibility = View.GONE
                        binding.tvError.visibility = View.VISIBLE
                        binding.rvMovieList.visibility = View.GONE
                        binding.tvError.text = "Error: ${movies.exception}"
                        isLoading = false
                        Log.d("LiveData", "${movies.exception}")
                    }
                }
            }
        })
    }
}