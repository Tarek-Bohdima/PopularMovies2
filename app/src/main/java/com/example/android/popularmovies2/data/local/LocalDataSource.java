/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */

package com.example.android.popularmovies2.data.local;

import androidx.lifecycle.LiveData;

import com.example.android.popularmovies2.data.model.Movie;

import java.util.List;

import javax.inject.Inject;

public class LocalDataSource {
    private final MovieDao movieDao;

    @Inject
    public LocalDataSource(MovieDao movieDao) {
        this.movieDao = movieDao;
    }

    public LiveData<List<Movie>> getAllMovies() {
        return movieDao.getAllMovies();
    }

    public LiveData<Movie> getMovieById(String movieId) {
        return movieDao.getMovieById(movieId);
    }

    public void insertMovie(Movie movie) {
        movieDao.insertMovie(movie);
    }

    public void deleteMovie(Movie movie) {
        movieDao.delete(movie);
    }

    public void deleteAllMovies() {
        movieDao.deleteAllMovies();
    }
}
