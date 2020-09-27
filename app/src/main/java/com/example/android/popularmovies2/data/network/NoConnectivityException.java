/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */

package com.example.android.popularmovies2.data.network;

import androidx.annotation.Nullable;

import java.io.IOException;

public class NoConnectivityException extends IOException {

    @Nullable
    @Override
    public String getMessage() {
//        Timber.tag("MyApp").d("getMessage() called: No network available, please check your WiFi or Data connection");

        // fire callback to show com.example.android.popularmovies2.ui.list.MainActivity.showErrorImageView ?
        return "No network available, please check your WiFi or Data connection";
    }
}
