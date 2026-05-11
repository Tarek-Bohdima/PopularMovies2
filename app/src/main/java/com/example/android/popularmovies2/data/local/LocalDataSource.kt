/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */
package com.example.android.popularmovies2.data.local

import com.example.android.popularmovies2.data.model.Movie
import com.example.android.popularmovies2.data.toDomain
import com.example.android.popularmovies2.data.toEntity
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class LocalDataSource @Inject constructor(
    private val movieDao: MovieDao,
) {
    fun getAllMovies(): Flow<List<Movie>> =
        movieDao.getAllMovies().map { list -> list.map(MovieEntity::toDomain) }

    fun getMovieByTmdbId(tmdbId: Int): Flow<Movie?> =
        movieDao.getMovieByTmdbId(tmdbId).map { it?.toDomain() }

    suspend fun insertMovie(movie: Movie) = movieDao.insertMovie(movie.toEntity())

    suspend fun deleteMovie(movie: Movie) = movieDao.deleteByTmdbId(movie.movieId)

    suspend fun deleteAllMovies() = movieDao.deleteAllMovies()
}
