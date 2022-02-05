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
import com.example.alkmovies.utils.Utils.adapter
import com.example.alkmovies.utils.Utils.isLoading
import com.example.alkmovies.utils.Utils.layoutManager
import com.example.alkmovies.utils.Utils.moviesList
import com.example.alkmovies.utils.Utils.page

//Fragment who contains a RecyclerView, who contains all the movies.
class MovieFragment : Fragment(R.layout.fragment_movie), MovieAdapter.OnMovieClickListener {
    private lateinit var binding: FragmentMovieBinding
    //variable for verify, if is the first time that the fragment is opened.
    private var fragmentInit = false
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
        adapter = MovieAdapter(moviesList, this)
        binding.rvMovieList.adapter = adapter
        //verify if is the first time that the fragment is opened.
        if (fragmentInit){
            onScrollUpdate()
        } else {
            loadMovies()
            onScrollUpdate()
            fragmentInit = true
        }
    }

    //Function for navigation and sending arguments(safeargs) to details fragment
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

    //Scroll navigation function, where on reaching the last item, reload a new page.
    private fun onScrollUpdate() {
        binding.rvMovieList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val pastVisibleItem = layoutManager.findLastCompletelyVisibleItemPosition()
                if (!isLoading) {
                    if (pastVisibleItem == moviesList.size - 1) {
                        loadMovies()
                    }
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }

    //Function who adds items, and refresh, to the adapter's list.
    private fun addMovies(movies: List<Movie>) {
        moviesList.addAll(movies)
        adapter.notifyDataSetChanged()
    }

    //This, is the main function of the fragment, makes the call, to the api, and control
    //the layout items, has a handler, to give it a delay.
    @SuppressLint("SetTextI18n")
    private fun loadMovies() {
        viewModel.fetchMovies(page).observe(viewLifecycleOwner, Observer { movies ->
            when (movies) {
                //Make a LiveData result, loading status.
                is Result.Loading -> {
                    isLoading = true
                    binding.progressBar.visibility = View.VISIBLE
                    binding.tvError.visibility = View.GONE
                    Log.d("LiveData", "LOADING...")
                }
                //returns the movies, and updates all ui things. Return Success status.
                is Result.Success -> {
                    addMovies(movies.data.results)
                    binding.rvMovieList.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                    binding.tvError.visibility = View.GONE
                    isLoading = false
                    ++page
                    Log.d("LiveData", "Success")

                }
                //Put the error in the screen.
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