/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */

package com.example.android.popularmovies2;

import android.app.Application;
import android.util.Log;

import com.example.android.popularmovies2.data.AppRepository;

public class MoviesApp extends Application {
    private static final String TAG = MoviesApp.class.getSimpleName();


    @Override
    public void onCreate() {
        super.onCreate();

    }


    public AppRepository getAppRepository() {
        Log.d(TAG, "getAppRepository: from MoviesApp");
        return AppRepository.getInstance(this);
    }

}
