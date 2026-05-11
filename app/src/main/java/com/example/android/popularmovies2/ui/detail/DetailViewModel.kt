/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */
package com.example.android.popularmovies2.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.popularmovies2.Constants
import com.example.android.popularmovies2.data.MovieRepository
import com.example.android.popularmovies2.data.model.Movie
import com.example.android.popularmovies2.data.model.Review
import com.example.android.popularmovies2.data.model.Trailer
import com.example.android.popularmovies2.ui.list.MainActivity.Companion.MOVIE_OBJECT
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: MovieRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    // `DetailActivity` is launched with a parcelable `Movie` extra under
    // `MainActivity.MOVIE_OBJECT`; ComponentActivity's `defaultViewModelCreationExtras`
    // promotes Intent extras into the SavedStateHandle, so reading by the same key
    // here gives us the movie without an explicit Activity → VM hand-off.
    private val movie: Movie = checkNotNull(savedStateHandle.get<Movie>(MOVIE_OBJECT)) {
        "DetailViewModel requires a '$MOVIE_OBJECT' extra on the launching Intent"
    }
    private val movieId: String = movie.movieId.toString()

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    private val _trailers = MutableStateFlow<List<Trailer>>(emptyList())
    val trailers: StateFlow<List<Trailer>> = _trailers.asStateFlow()

    val favoriteMovie: StateFlow<Movie?> = repository.favoriteMovieById(movieId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STATE_FLOW_STOP_TIMEOUT_MS),
            initialValue = null,
        )

    init {
        viewModelScope.launch {
            runCatching { repository.fetchReviews(movieId) }
                .onSuccess { _reviews.value = it }
                .onFailure {
                    Timber.tag(Constants.TAG).w(it, "DetailViewModel: reviews fetch failed")
                }
        }
        viewModelScope.launch {
            runCatching { repository.fetchTrailers(movieId) }
                .onSuccess { _trailers.value = it }
                .onFailure {
                    Timber.tag(Constants.TAG).w(it, "DetailViewModel: trailers fetch failed")
                }
        }
    }

    fun insertFavoriteMovie(movie: Movie) {
        Timber.tag(Constants.TAG).d("DetailViewModel: insertFavoriteMovie() called with: movie = [$movie]")
        viewModelScope.launch { repository.insertFavoriteMovie(movie) }
    }

    fun deleteFavoriteMovie(movie: Movie) {
        Timber.tag(Constants.TAG).d("DetailViewModel: deleteFavoriteMovie() called with: movie = [$movie]")
        viewModelScope.launch { repository.deleteFavoriteMovie(movie) }
    }

    private companion object {
        const val STATE_FLOW_STOP_TIMEOUT_MS = 5_000L
    }
}
