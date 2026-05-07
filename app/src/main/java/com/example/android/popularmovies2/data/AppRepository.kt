/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */
package com.example.android.popularmovies2.data

import androidx.lifecycle.LiveData
import com.example.android.popularmovies2.AppExecutors
import com.example.android.popularmovies2.Constants
import com.example.android.popularmovies2.data.local.LocalDataSource
import com.example.android.popularmovies2.data.model.Movie
import com.example.android.popularmovies2.data.model.Review
import com.example.android.popularmovies2.data.model.Trailer
import com.example.android.popularmovies2.data.network.NetworkDataSource
import com.example.android.popularmovies2.di.scopes.ApplicationScope
import timber.log.Timber
import javax.inject.Inject

@ApplicationScope
class AppRepository @Inject internal constructor(
    private val networkDataSource: NetworkDataSource,
    private val localDataSource: LocalDataSource,
) {
    fun getPopularMovies(): LiveData<List<Movie>> {
        Timber.tag(Constants.TAG).d("AppRepository: getPopularMovies")
        return networkDataSource.getPopularMoviesLiveData()
    }

    fun getTopRatedMovies(): LiveData<List<Movie>> {
        Timber.tag(Constants.TAG).d("AppRepository: getTopRatedMovies")
        return networkDataSource.getTopRatedMoviesLiveData()
    }

    fun getReviewsByMovieId(movieId: String): LiveData<List<Review>?> {
        Timber.tag(Constants.TAG)
            .d("AppRepository: getReviewsByMovieId() called with: movieId = [ %s]", movieId)
        return networkDataSource.getReviewsLiveDataByMovieId(movieId)
    }

    fun getTrailersByMovieId(movieId: String): LiveData<List<Trailer>?> {
        Timber.tag(Constants.TAG)
            .d("AppRepository: getTrailersByMovieId() called with: movieId = [ %s ]", movieId)
        return networkDataSource.getTrailersLiveDataByMovieId(movieId)
    }

    fun getAllFavoriteMovies(): LiveData<List<Movie>> {
        Timber.tag(Constants.TAG).d("AppRepository: getAllFavoriteMovies() called")
        return localDataSource.getAllMovies()
    }

    fun getFavoriteMovieById(movieId: String): LiveData<Movie> {
        Timber.tag(Constants.TAG)
            .d("AppRepository: getFavoriteMovieById() called with: movieId = [ %s ]", movieId)
        return localDataSource.getMovieById(movieId)
    }

    fun insertFavoriteMovie(movie: Movie) {
        Timber.tag(Constants.TAG)
            .d("AppRepository: insertFavoriteMovie() called with: movie = [ %s ]", movie)
        AppExecutors.getInstance().diskIO().execute { localDataSource.insertMovie(movie) }
    }

    fun deleteFavoriteMovie(movie: Movie) {
        Timber.tag(Constants.TAG)
            .d("AppRepository: deleteFavoriteMovie() called with: movie = [ %s ]", movie)
        AppExecutors.getInstance().diskIO().execute { localDataSource.deleteMovie(movie) }
    }

    fun deleteAllFavoriteMovies() {
        Timber.tag(Constants.TAG).d("AppRepository: deleteAllFavoriteMovies() called")
        AppExecutors.getInstance().diskIO().execute { localDataSource.deleteAllMovies() }
    }

    fun clearDisposables() {
        networkDataSource.clearDisposables()
        Timber.tag(Constants.TAG).d("AppRepository: clearDisposables() called")
    }
}
