package com.example.alkmovies.ui.movie

import MovieAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
                if (dy > 0) {
                    if (!isLoading) {
                        if ((visibleItemCount + pastVisibleItem) >= total) {
                            ++page
                            adaptMovies()
                        }
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

    private fun handAdapter() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (::adapter.isInitialized) {
                adapter.updateList(moviesList)
                binding.rvMovieList.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
                binding.tvError.visibility = View.GONE
                isLoading = false
            } else {
                adapter = MovieAdapter(moviesList, this)
                binding.rvMovieList.adapter = adapter
                binding.rvMovieList.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
                binding.tvError.visibility = View.GONE
                isLoading = false
            }
        }, 3000)
    }

    @SuppressLint("SetTextI18n")
    private fun adaptMovies() {
        binding.progressBar.visibility = View.VISIBLE

        viewModel.fetchMovies(page).observe(viewLifecycleOwner, Observer { movies ->
            when (movies) {
                is Result.Loading -> {
                    isLoading = true
                    binding.progressBar.visibility = View.VISIBLE
                    binding.tvError.visibility = View.GONE
                    Log.d("LiveData", "LOADING...")
                }
                is Result.Success -> {
                    movies.data.results.forEach {
                        moviesList.add(it)
                    }
                    handAdapter()
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
        })
    }
}