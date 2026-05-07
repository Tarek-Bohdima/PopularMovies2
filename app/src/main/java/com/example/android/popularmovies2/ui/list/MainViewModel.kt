/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */
package com.example.android.popularmovies2.ui.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.android.popularmovies2.Constants
import com.example.android.popularmovies2.MoviesApp
import com.example.android.popularmovies2.data.AppRepository
import com.example.android.popularmovies2.data.model.Movie
import timber.log.Timber

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val appRepository: AppRepository =
        (application as MoviesApp).movieComponent.getAppRepository()

    private val popularMoviesLiveData: LiveData<List<Movie>>
    private val topRatedMoviesLiveData: LiveData<List<Movie>>
    private val favoriteMoviesLiveData: LiveData<List<Movie>>

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
}
