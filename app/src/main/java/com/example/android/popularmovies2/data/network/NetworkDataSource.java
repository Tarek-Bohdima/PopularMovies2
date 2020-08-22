/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */

package com.example.android.popularmovies2.data.network;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.android.popularmovies2.AppExecutors;
import com.example.android.popularmovies2.data.model.Movie;

import java.util.List;

public class NetworkDataSource {
    private static final String TAG = NetworkDataSource.class.getSimpleName();

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static NetworkDataSource sInstance;
    private final Context mContext;

    // LiveData storing the latest downloaded movies
    private MutableLiveData<List<Movie>> donwloadedMovies;
    private AppExecutors executors;

    private NetworkDataSource(Context mContext, AppExecutors executors) {
        this.mContext = mContext;
        this.executors = executors;
        donwloadedMovies = new MutableLiveData<>();
    }

    /**
     * Get the singleton for this class
     */
    public static NetworkDataSource getInstance(Context context, AppExecutors executors) {
        Log.d(TAG, "getting the network data source");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new NetworkDataSource(context.getApplicationContext(), executors);
                Log.d(TAG, "Made new Network Data source");
            }
        }
        return sInstance;
    }

    public LiveData<List<Movie>> getCurrentMovies() {
        return donwloadedMovies;
    }

}
