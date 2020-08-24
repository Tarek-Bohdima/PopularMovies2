/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */

package com.example.android.popularmovies2;

import android.app.Application;

import com.example.android.popularmovies2.data.AppRepository;
import com.example.android.popularmovies2.data.local.AppDatabase;
import com.example.android.popularmovies2.data.local.MovieDao;
import com.example.android.popularmovies2.data.network.MovieApi;

public class MoviesApp extends Application {

    private AppDatabase appDatabase;
    private AppExecutors appExecutors;
    private MovieDao movieDao;
    private MovieApi movieApi;


    @Override
    public void onCreate() {
        super.onCreate();

        appDatabase = AppDatabase.getInstance(this);
        appExecutors = AppExecutors.getInstance();
        movieDao = appDatabase.movieDao();
    }

    public AppDatabase getAppDatabase() {
        return AppDatabase.getInstance(this);
    }

    public AppRepository getAppRepository() {
        return AppRepository.getInstance(movieDao, appExecutors, movieApi);
    }
}
