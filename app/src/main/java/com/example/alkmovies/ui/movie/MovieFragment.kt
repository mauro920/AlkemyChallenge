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
import androidx.recyclerview.widget.LinearLayoutManager
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

    lateinit var adapter: MovieAdapter
    lateinit var layoutManager: LinearLayoutManager
    private lateinit var binding: FragmentMovieBinding
    private val handler: Handler = Handler(Looper.getMainLooper())
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
        adaptMovies()

        binding.rvMovieList.addOnScrollListener(object: RecyclerView.OnScrollListener()
        {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val visibleItemCount = layoutManager.childCount
                val pastVisibleItem = layoutManager.findFirstCompletelyVisibleItemPosition()
                val total = adapter.itemCount

                if (!isLoading) {
                    if ((visibleItemCount + pastVisibleItem) >= total) {
                        page=+ 1
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
        viewModel.fetchMovies(page).observe(viewLifecycleOwner, Observer { movies ->
            when (movies) {
                is Result.Loading -> {
                    isLoading = true
                    binding.progressBar.visibility = View.VISIBLE
                    binding.tvError.visibility = View.GONE
                    binding.rvMovieList.visibility = View.GONE
                    Log.d("LiveData", "LOADING...")
                }
                is Result.Success -> {
                    moviesList.addAll(movies.data.results)
                    binding.progressBar.visibility = View.GONE
                    binding.tvError.visibility = View.GONE
                    binding.rvMovieList.visibility = View.VISIBLE
                    getHandler()
                    isLoading = false
                    Log.d("LiveData", "${movies.data}")

                }
                is Result.Failure -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvError.visibility = View.VISIBLE
                    binding.rvMovieList.visibility = View.GONE
                    binding.tvError.text = "Error: ${movies.exception}"
                    isLoading= false
                    Log.d("LiveData", "${movies.exception}")
                }
            }
        })
    }
    private fun getHandler(){
        handler.postDelayed({
            if(::adapter.isInitialized){
                adapter.notifyDataSetChanged()
            } else {
                adapter = MovieAdapter(moviesList, this)
                binding.rvMovieList.adapter = adapter
            }
        },5000)
    }
}