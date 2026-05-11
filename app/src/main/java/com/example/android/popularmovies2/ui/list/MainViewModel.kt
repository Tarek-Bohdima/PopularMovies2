/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */
package com.example.android.popularmovies2.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.popularmovies2.Constants
import com.example.android.popularmovies2.data.AppRepository
import com.example.android.popularmovies2.data.model.Movie
import com.example.android.popularmovies2.data.network.NetworkMonitor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel(
    private val appRepository: AppRepository,
    networkMonitor: NetworkMonitor,
) : ViewModel() {

    private val _popular = MutableStateFlow<List<Movie>>(emptyList())
    val popular: StateFlow<List<Movie>> = _popular.asStateFlow()

    private val _topRated = MutableStateFlow<List<Movie>>(emptyList())
    val topRated: StateFlow<List<Movie>> = _topRated.asStateFlow()

    val favorites: StateFlow<List<Movie>> = appRepository.favoriteMovies()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STATE_FLOW_STOP_TIMEOUT_MS),
            initialValue = emptyList(),
        )

    val isOnline: StateFlow<Boolean> = networkMonitor.isOnline
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STATE_FLOW_STOP_TIMEOUT_MS),
            initialValue = false,
        )

    init {
        Timber.tag(Constants.TAG).d("MainViewModel: get AppRepository instance")
        refreshPopular()
        refreshTopRated()
    }

    fun refreshPopular() {
        viewModelScope.launch {
            runCatching { appRepository.fetchPopularMovies() }
                .onSuccess { _popular.value = it }
                .onFailure {
                    Timber.tag(Constants.TAG).w(it, "MainViewModel: popular fetch failed")
                }
        }
    }

    fun refreshTopRated() {
        viewModelScope.launch {
            runCatching { appRepository.fetchTopRatedMovies() }
                .onSuccess { _topRated.value = it }
                .onFailure {
                    Timber.tag(Constants.TAG).w(it, "MainViewModel: topRated fetch failed")
                }
        }
    }

    fun deleteAllFavoriteMovies() {
        Timber.tag(Constants.TAG).d("MainViewModel: deleteAllFavoriteMovies() called")
        viewModelScope.launch { appRepository.deleteAllFavoriteMovies() }
    }

    private companion object {
        const val STATE_FLOW_STOP_TIMEOUT_MS = 5_000L
    }
}
