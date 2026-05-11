/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */
package com.example.android.popularmovies2.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.popularmovies2.Constants
import com.example.android.popularmovies2.data.AppRepository
import com.example.android.popularmovies2.data.model.Movie
import com.example.android.popularmovies2.data.network.NetworkMonitor
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber

class MainViewModel(
    private val appRepository: AppRepository,
    networkMonitor: NetworkMonitor,
) : ViewModel() {
    private val popularMoviesLiveData: LiveData<List<Movie>>
    private val topRatedMoviesLiveData: LiveData<List<Movie>>
    private val favoriteMoviesLiveData: LiveData<List<Movie>>

    val isOnline: StateFlow<Boolean> = networkMonitor.isOnline
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STATE_FLOW_STOP_TIMEOUT_MS),
            initialValue = false,
        )

    init {
        Timber.tag(Constants.TAG).d("MainViewModel: get AppRepository instance")
        popularMoviesLiveData = appRepository.getPopularMovies()
        topRatedMoviesLiveData = appRepository.getTopRatedMovies()
        favoriteMoviesLiveData = appRepository.getAllFavoriteMovies()
    }

    fun getPopularMovies(): LiveData<List<Movie>> {
        Timber.tag(Constants.TAG).d("MainViewModel: getPopularMovies() called")
        return popularMoviesLiveData
    }

    fun getTopRatedMovies(): LiveData<List<Movie>> {
        Timber.tag(Constants.TAG).d("MainViewModel: getTopRatedMovies() called")
        return topRatedMoviesLiveData
    }

    fun getFavoriteMovies(): LiveData<List<Movie>> {
        Timber.tag(Constants.TAG).d("MainViewModel: getFavoriteMovies() called")
        return favoriteMoviesLiveData
    }

    fun deleteAllFavoriteMovies() {
        Timber.tag(Constants.TAG).d("MainViewModel: deleteAllFavoriteMovies() called")
        appRepository.deleteAllFavoriteMovies()
    }

    override fun onCleared() {
        super.onCleared()
        appRepository.clearDisposables()
        Timber.tag(Constants.TAG).d("MainViewModel: onCleared() called")
    }

    private companion object {
        // 5s lets the upstream survive a config-change subscription gap.
        const val STATE_FLOW_STOP_TIMEOUT_MS = 5_000L
    }
}
