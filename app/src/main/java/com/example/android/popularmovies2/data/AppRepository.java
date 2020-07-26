/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */

package com.example.android.popularmovies2.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.android.popularmovies2.AppExecutors;
import com.example.android.popularmovies2.data.local.AppDatabase;
import com.example.android.popularmovies2.data.local.MovieDao;
import com.example.android.popularmovies2.data.model.Movie;

import java.util.List;

public class AppRepository {
    private static final String TAG = AppRepository.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static AppRepository sInstance;
    private final MovieDao movieDao;
    private AppExecutors executors;
    private boolean isInitialized = false;
    private LiveData<List<Movie>> movieList;

    private AppRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        movieDao = database.movieDao();
        movieList = movieDao.loadAllMovies();
    }

    public  LiveData<List<Movie>> getMovieList(){
        return movieList;
    }

    public void insert(Movie movie) {

    }

    public void update(Movie movie) {

    }

    public void delete(Movie movie) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {

            }
        });
    }
}
