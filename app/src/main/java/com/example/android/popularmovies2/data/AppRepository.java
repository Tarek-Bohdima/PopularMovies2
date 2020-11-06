/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */

package com.example.android.popularmovies2.data;

import androidx.lifecycle.LiveData;

import com.example.android.popularmovies2.Constants;
import com.example.android.popularmovies2.data.local.LocalDataSource;
import com.example.android.popularmovies2.data.model.Movie;
import com.example.android.popularmovies2.data.model.Review;
import com.example.android.popularmovies2.data.model.Trailer;
import com.example.android.popularmovies2.data.network.NetworkDataSource;
import com.example.android.popularmovies2.di.scopes.ApplicationScope;

import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

@ApplicationScope
public class AppRepository {

    private final NetworkDataSource networkDataSource;
    private final LocalDataSource localDataSource;

    @Inject
    AppRepository(NetworkDataSource networkDataSource, LocalDataSource localDataSource) {
        this.networkDataSource = networkDataSource;
        this.localDataSource = localDataSource;
    }

    public LiveData<List<Movie>> getPopularMovies() {
        Timber.tag(Constants.TAG).d("AppRepository: getPopularMovies");
        return networkDataSource.getPopularMoviesLiveData();
    }

    public LiveData<List<Movie>> getTopRatedMovies() {
        Timber.tag(Constants.TAG).d("AppRepository: getTopRatedMovies");
        return networkDataSource.getTopRatedMoviesLiveData();
    }

    public LiveData<List<Review>> getReviewsByMovieId(String movieId) {
        Timber.tag(Constants.TAG).d("AppRepository: getReviewsByMovieId() called with: movieId = [ %s]", movieId);
        return networkDataSource.getReviewsLiveDataByMovieId(movieId);
    }

    public LiveData<List<Trailer>> getTrailersByMovieId(String movieId) {
        Timber.tag(Constants.TAG).d("AppRepository: getTrailersByMovieId() called with: movieId = [ %s ]", movieId);
        return networkDataSource.getTrailersLiveDataByMovieId(movieId);
    }

    public LiveData<List<Movie>> getAllFavoriteMovies() {
        Timber.tag(Constants.TAG).d("AppRepository: getAllFavoriteMovies() called");
        return localDataSource.getAllMovies();
    }

    public LiveData<Movie> getFavoriteMovieById(String movieId) {
        Timber.tag(Constants.TAG).d("AppRepository: getFavoriteMovieById() called with: movieId = [ %s ]", movieId);
        return localDataSource.getMovieById(movieId);
    }

    public void insertFavoriteMovie(Movie movie) {
        Timber.tag(Constants.TAG).d("AppRepository: insertFavoriteMovie() called with: movie = [ %s ]", movie);
        localDataSource.insertMovie(movie);
    }

    public void deleteFavoriteMovie(Movie movie) {
        Timber.tag(Constants.TAG).d("AppRepository: deleteFavoriteMovie() called with: movie = [ %s ]", movie);
        localDataSource.deleteMovie(movie);
    }

    public void deleteAllFavoriteMovies() {
        Timber.tag(Constants.TAG).d("AppRepository: deleteAllFavoriteMovies() called");
        localDataSource.deleteAllMovies();
    }

    public void clearDisposables() {
        networkDataSource.clearDisposables();
        Timber.tag(Constants.TAG).d("AppRepository: clearDisposables() called");
    }
}
