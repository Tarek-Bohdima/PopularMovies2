/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */
package com.example.android.popularmovies2.data.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.popularmovies2.BuildConfig
import com.example.android.popularmovies2.Constants
import com.example.android.popularmovies2.data.model.Movie
import com.example.android.popularmovies2.data.model.Review
import com.example.android.popularmovies2.data.model.Trailer
import com.example.android.popularmovies2.di.scopes.ApplicationScope
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.Retrofit
import timber.log.Timber
import javax.inject.Inject

@ApplicationScope
class NetworkDataSource @Inject constructor(
    private val retrofit: Retrofit,
) {
    private val popularMovies = MutableLiveData<List<Movie>>()
    private val topRatedMovies = MutableLiveData<List<Movie>>()
    private val reviewsLiveData = MutableLiveData<List<Review>?>()
    private val trailersLiveData = MutableLiveData<List<Trailer>?>()
    private val compositeDisposable = CompositeDisposable()
    private val apiKey: String = BuildConfig.TMDB_API_KEY
    private lateinit var movieApi: MovieApi

    fun getPopularMoviesLiveData(): LiveData<List<Movie>> {
        getMoviesByPath(POPULAR)
        Timber.tag(Constants.TAG).d("NetworkDataSource: return PopularMovies LiveData")
        return popularMovies
    }

    fun getTopRatedMoviesLiveData(): LiveData<List<Movie>> {
        getMoviesByPath(TOP_RATED)
        Timber.tag(Constants.TAG).d("NetworkDataSource: return TopRatedMovies LiveData")
        return topRatedMovies
    }

    private fun getMoviesByPath(path: String) {
        movieApi = retrofit.create(MovieApi::class.java)
        val moviesListSingle = movieApi.getMoviesByPath(path, apiKey)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

        when (path) {
            POPULAR -> compositeDisposable.add(
                moviesListSingle.subscribe(
                    { popularMovies.postValue(it.getMovies()) },
                    { e ->
                        Timber.tag(Constants.TAG)
                            .d("NetworkDataSource: getMoviesByPath() called with: error = [${e.message}]")
                    },
                ),
            )
            TOP_RATED -> compositeDisposable.add(
                moviesListSingle.subscribe(
                    { topRatedMovies.postValue(it.getMovies()) },
                    { e ->
                        Timber.tag(Constants.TAG)
                            .d("NetworkDataSource: getMoviesByPath() called with: error = [${e.message}]")
                    },
                ),
            )
        }
    }

    fun getReviewsLiveDataByMovieId(movieId: String): LiveData<List<Review>?> {
        getReviewsByMovie(movieId)
        Timber.tag(Constants.TAG)
            .d("NetWorkDataSource: getReviewsLiveDataByMovieId() called with: movieId = [$movieId]")
        return reviewsLiveData
    }

    private fun getReviewsByMovie(movieId: String) {
        val reviewsListSingle = movieApi.getReviews(movieId, apiKey)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

        compositeDisposable.add(
            reviewsListSingle.subscribe(
                { reviewsLiveData.postValue(it.getReviews()) },
                { e ->
                    Timber.tag(Constants.TAG)
                        .d("NetWorkDataSource: getReviewsByMovie() error: [${e.message}]")
                },
            ),
        )
    }

    fun getTrailersLiveDataByMovieId(movieId: String): LiveData<List<Trailer>?> {
        getTrailersByMovie(movieId)
        Timber.tag(Constants.TAG)
            .d("NetWorkDataSource: getTrailersLiveDataByMovieId() called with: movieId = [$movieId]")
        return trailersLiveData
    }

    private fun getTrailersByMovie(movieId: String) {
        val trailerListSingle = movieApi.getTrailers(movieId, apiKey)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

        compositeDisposable.add(
            trailerListSingle.subscribe(
                { trailersLiveData.postValue(it.getTrailers()) },
                { e ->
                    Timber.tag(Constants.TAG)
                        .d("NetworkDataSource: getTrailersByMovie() error = [${e.message}]")
                },
            ),
        )
    }

    fun clearDisposables() {
        compositeDisposable.clear()
        Timber.tag(Constants.TAG).d("NetworkDataSource: clearDisposables() called")
    }

    private companion object {
        const val POPULAR = "popular"
        const val TOP_RATED = "top_rated"
    }
}
