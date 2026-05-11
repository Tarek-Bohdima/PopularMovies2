/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */
package com.example.android.popularmovies2.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.popularmovies2.Constants
import com.example.android.popularmovies2.data.AppRepository
import com.example.android.popularmovies2.data.model.Movie
import com.example.android.popularmovies2.data.model.Review
import com.example.android.popularmovies2.data.model.Trailer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

class DetailViewModel(
    private val appRepository: AppRepository,
    private val movieId: String,
) : ViewModel() {

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    private val _trailers = MutableStateFlow<List<Trailer>>(emptyList())
    val trailers: StateFlow<List<Trailer>> = _trailers.asStateFlow()

    val favoriteMovie: StateFlow<Movie?> = appRepository.favoriteMovieById(movieId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STATE_FLOW_STOP_TIMEOUT_MS),
            initialValue = null,
        )

    init {
        viewModelScope.launch {
            runCatching { appRepository.fetchReviews(movieId) }
                .onSuccess { _reviews.value = it }
                .onFailure {
                    Timber.tag(Constants.TAG).w(it, "DetailViewModel: reviews fetch failed")
                }
        }
        viewModelScope.launch {
            runCatching { appRepository.fetchTrailers(movieId) }
                .onSuccess { _trailers.value = it }
                .onFailure {
                    Timber.tag(Constants.TAG).w(it, "DetailViewModel: trailers fetch failed")
                }
        }
    }

    fun insertFavoriteMovie(movie: Movie) {
        Timber.tag(Constants.TAG).d("DetailViewModel: insertFavoriteMovie() called with: movie = [$movie]")
        viewModelScope.launch { appRepository.insertFavoriteMovie(movie) }
    }

    fun deleteFavoriteMovie(movie: Movie) {
        Timber.tag(Constants.TAG).d("DetailViewModel: deleteFavoriteMovie() called with: movie = [$movie]")
        viewModelScope.launch { appRepository.deleteFavoriteMovie(movie) }
    }

    private companion object {
        const val STATE_FLOW_STOP_TIMEOUT_MS = 5_000L
    }
}
