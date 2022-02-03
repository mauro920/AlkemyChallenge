package com.example.alkmovies.ui.moviedetails

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.alkmovies.R
import com.example.alkmovies.databinding.FragmentMovieDetailsBinding
//Fragment for Movies Details
class MovieDetailsFragment : Fragment(R.layout.fragment_movie_details) {

    private lateinit var binding: FragmentMovieDetailsBinding
    private val args by navArgs<MovieDetailsFragmentArgs>()

    //Setup of Args
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMovieDetailsBinding.bind(view)
        Glide.with(requireContext()).load("https://image.tmdb.org/t/p/w500/${args.posterImageUrl}")
            .centerCrop().into(binding.imgMovie)
        Glide.with(requireContext()).load("https://image.tmdb.org/t/p/w500/${args.backgroundImageUrl}")
            .centerCrop().into(binding.bgImage)
        binding.txtDescription.text = args.overview
        binding.title.text = args.title
        binding.txtLanguage.text = "Language: ${args.language}"
        binding.txtRating.text = "${args.voteAverage}(${args.voteCount} reviews)"
        binding.txtRelease.text = "Released ${args.releaseDate}"
    }
}