/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */
package com.example.android.popularmovies2.data

import com.example.android.popularmovies2.data.model.Movie
import com.example.android.popularmovies2.data.model.Review
import com.example.android.popularmovies2.data.model.Trailer
import kotlinx.coroutines.flow.Flow

/**
 * UI/ViewModel-facing repository contract. ViewModels depend on this interface;
 * the concrete implementation (`MovieRepositoryImpl`) is bound by the Hilt
 * `RepositoryModule`. Tests can substitute a fake implementation without mocking.
 */
interface MovieRepository {
    suspend fun fetchPopularMovies(): List<Movie>
    suspend fun fetchTopRatedMovies(): List<Movie>
    suspend fun fetchReviews(movieId: String): List<Review>
    suspend fun fetchTrailers(movieId: String): List<Trailer>

    fun favoriteMovies(): Flow<List<Movie>>
    fun favoriteMovieById(movieId: String): Flow<Movie?>

    suspend fun insertFavoriteMovie(movie: Movie)
    suspend fun deleteFavoriteMovie(movie: Movie)
    suspend fun deleteAllFavoriteMovies()
}
