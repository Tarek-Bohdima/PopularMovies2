/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */

package com.example.android.popularmovies2.ui.detail;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.android.popularmovies2.MoviesApp;
import com.example.android.popularmovies2.data.AppRepository;

import timber.log.Timber;

public class DetailViewModel extends AndroidViewModel {

    public DetailViewModel(@NonNull Application application) {
        super(application);
        AppRepository appRepository = ((MoviesApp) getApplication()).getAppRepository();
        Timber.tag("MyApp").d("DetailViewModel: get AppRepository instance");
    }
}
