/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */
package com.example.android.popularmovies2.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.android.popularmovies2.Constants
import com.example.android.popularmovies2.data.AppRepository
import com.example.android.popularmovies2.data.model.Movie
import com.example.android.popularmovies2.data.model.Review
import com.example.android.popularmovies2.data.model.Trailer
import timber.log.Timber

class DetailViewModel(
    private val appRepository: AppRepository,
    movieId: String,
) : ViewModel() {
    private val reviews: LiveData<List<Review>?> = appRepository.getReviewsByMovieId(movieId)
    private val trailers: LiveData<List<Trailer>?> = appRepository.getTrailersByMovieId(movieId)
    private val favoriteMovie: LiveData<Movie> = appRepository.getFavoriteMovieById(movieId)

    fun getReviewsByMovieId(): LiveData<List<Review>?> {
        Timber.tag(Constants.TAG).d("DetailViewModel: getReviewsByMovieId() called")
        return reviews
    }

    fun getTrailersByMovieId(): LiveData<List<Trailer>?> {
        Timber.tag(Constants.TAG).d("DetailViewModel: getTrailersByMovieId() called")
        return trailers
    }

    fun getFavoriteMovieById(): LiveData<Movie> {
        Timber.tag(Constants.TAG).d("DetailViewModel: getFavoriteMovieById() called")
        return favoriteMovie
    }

    fun insertFavoriteMovie(movie: Movie) {
        Timber.tag(Constants.TAG).d("DetailViewModel: insertFavoriteMovie() called with: movie = [$movie]")
        appRepository.insertFavoriteMovie(movie)
    }

    fun deleteFavoriteMovie(movie: Movie) {
        Timber.tag(Constants.TAG).d("DetailViewModel: deleteFavoriteMovie() called with: movie = [$movie]")
        appRepository.deleteFavoriteMovie(movie)
    }
}
