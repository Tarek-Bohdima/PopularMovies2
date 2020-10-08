/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */

package com.example.android.popularmovies2.di.module;

import android.content.Context;

import com.example.android.popularmovies2.Constants;
import com.example.android.popularmovies2.data.network.NoConnectionInterceptor;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import timber.log.Timber;

@Module(includes = ContextModule.class)
public class OkHttpClientModule {

    @Provides
    public OkHttpClient okHttpClient(NoConnectionInterceptor noConnectionInterceptor,
                                     Context context) {
        return new OkHttpClient()
                .newBuilder()
//                .addInterceptor(httpLoggingInterceptor)
//                .addInterceptor(noConnectionInterceptor)
//                .addInterceptor(new NoConnectionInterceptor(context))
                .build();
    }


    @Provides
    public HttpLoggingInterceptor httpLoggingInterceptor() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Timber.tag(Constants.TAG).d("httpLoggingInterceptor%s", message);
            }
        });
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return httpLoggingInterceptor;
    }

}
