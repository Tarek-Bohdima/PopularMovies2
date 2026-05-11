/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */
package com.example.android.popularmovies2.data

import com.example.android.popularmovies2.data.local.LocalDataSource
import com.example.android.popularmovies2.data.model.Movie
import com.example.android.popularmovies2.data.model.Review
import com.example.android.popularmovies2.data.model.Trailer
import com.example.android.popularmovies2.data.network.NetworkDataSource
import com.example.android.popularmovies2.di.scopes.ApplicationScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ApplicationScope
class AppRepository @Inject internal constructor(
    private val networkDataSource: NetworkDataSource,
    private val localDataSource: LocalDataSource,
) {
    suspend fun fetchPopularMovies(): List<Movie> = networkDataSource.popularMovies()

    suspend fun fetchTopRatedMovies(): List<Movie> = networkDataSource.topRatedMovies()

    suspend fun fetchReviews(movieId: String): List<Review> = networkDataSource.reviews(movieId)

    suspend fun fetchTrailers(movieId: String): List<Trailer> = networkDataSource.trailers(movieId)

    fun favoriteMovies(): Flow<List<Movie>> = localDataSource.getAllMovies()

    fun favoriteMovieById(movieId: String): Flow<Movie?> = localDataSource.getMovieById(movieId)

    suspend fun insertFavoriteMovie(movie: Movie) = localDataSource.insertMovie(movie)

    suspend fun deleteFavoriteMovie(movie: Movie) = localDataSource.deleteMovie(movie)

    suspend fun deleteAllFavoriteMovies() = localDataSource.deleteAllMovies()
}
