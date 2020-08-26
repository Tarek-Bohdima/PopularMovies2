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
import com.example.android.popularmovies2.data.local.MovieDao;
import com.example.android.popularmovies2.data.model.Movie;
import com.example.android.popularmovies2.data.network.MovieApi;
import com.example.android.popularmovies2.data.network.NetworkDataSource;

import java.util.List;

public class AppRepository {
    private static final String TAG = AppRepository.class.getSimpleName();
    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static AppRepository sInstance;

    private  MovieDao movieDao;

    private  AppExecutors executors;

    private  MovieApi movieApi;

    private NetworkDataSource networkDataSource;



    private AppRepository(Application application) {
        networkDataSource = NetworkDataSource.getInstance(application);
    }

    public synchronized static AppRepository getInstance(Application application) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(TAG, "getInstance: Creating new repository instance");
                sInstance = new AppRepository(application);
            }
        }
        return sInstance;
    }


//    public LiveData<List<Movie>> getPopularMovies() {
//        Log.d(TAG, "getPopularMovies: getting popular movies from database ");
//        return movieDao.getPopularMovies();
//    }
//
//    public LiveData<List<Movie>> getTopRatedMovies() {
//        Log.d(TAG, "getTopRatedMovies: getting top rated movies from database");
//        return movieDao.getTopRatedMovies();
//    }

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
        AppExecutors.getInstance().diskIO().execute(movieDao::deleteAllMovies);
    }

    public LiveData<Movie> getMovieById(int id) {
        Log.d(TAG, "getMovieById: " + id);
        return movieDao.getMoviebyId(id);
    }

    public LiveData<List<Movie>> getPopularMovies() {
        return networkDataSource.getPopularMoviesLiveData();
    }

    public LiveData<List<Movie>> getTopRatedMovies() {
//        return networkDataSource.getTopRatedMoviesLiveData();
        return networkDataSource.getTopRatedMoviesLiveData();
    }
}
