/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */

package com.example.android.popularmovies2.data;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.android.popularmovies2.AppExecutors;
import com.example.android.popularmovies2.data.local.AppDatabase;
import com.example.android.popularmovies2.data.local.MovieDao;
import com.example.android.popularmovies2.data.model.Movie;

import java.util.List;

public class AppRepository {
    private static final String TAG = AppRepository.class.getSimpleName();
    private final MovieDao movieDao;
    private LiveData<List<Movie>> movieList;
    private LiveData<List<Movie>> topRatedMovies;
    private LiveData<List<Movie>> popularMovies;
    private LiveData<List<Movie>> favoriteMovies;


    public AppRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        movieDao = database.movieDao();
        movieList = movieDao.getAllMovies();
        topRatedMovies = movieDao.getTopRatedMovies();
        popularMovies = movieDao.getPopularMovies();
        favoriteMovies = movieDao.getFavoriteMovies();
    }


    public  LiveData<List<Movie>> getMovieList(){
        return movieList;
    }

    public LiveData<List<Movie>> getTopRatedMovies() {
        return topRatedMovies;
    }

    public LiveData<List<Movie>> getPopularMovies() {
        return popularMovies;
    }

    public LiveData<List<Movie>> getFavoriteMovies() {
        return favoriteMovies;
    }


    public void insert(Movie movie) {
        Log.d(TAG, "insert: movie inserted " + movie.toString() );
        AppExecutors.getInstance().diskIO().execute(() -> movieDao.insertMovie(movie));
    }

    public void delete(Movie movie) {
        Log.d(TAG, "delete: movie deleted " + movie.toString());
        AppExecutors.getInstance().diskIO().execute(() -> movieDao.delete(movie));
    }

    public void deleteAllMovies(){
        Log.d(TAG, "deleteAllMovies: done!");
        AppExecutors.getInstance().diskIO().execute(() -> {
            movieDao.deleteAllMovies();
        });
    }

    public LiveData<Movie> getMovieById(int id) {
        Log.d(TAG, "getMovieById: " + id);
        return movieDao.getMoviebyId(id);
    }
}
