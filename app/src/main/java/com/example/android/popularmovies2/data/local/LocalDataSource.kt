/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */
package com.example.android.popularmovies2.data.local

import com.example.android.popularmovies2.data.model.Movie
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val movieDao: MovieDao,
) {
    fun getAllMovies(): Flow<List<Movie>> = movieDao.getAllMovies()

    fun getMovieById(movieId: String): Flow<Movie?> = movieDao.getMovieById(movieId)

    suspend fun insertMovie(movie: Movie) = movieDao.insertMovie(movie)

    suspend fun deleteMovie(movie: Movie) = movieDao.delete(movie)

    suspend fun deleteAllMovies() = movieDao.deleteAllMovies()
}
