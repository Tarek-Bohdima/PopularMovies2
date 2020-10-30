/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */

package com.example.android.popularmovies2.di.module;

import android.content.Context;

import androidx.room.Room;

import com.example.android.popularmovies2.data.local.AppDatabase;
import com.example.android.popularmovies2.data.local.MovieDao;
import com.example.android.popularmovies2.di.scopes.ApplicationScope;

import dagger.Module;
import dagger.Provides;

@Module(includes = ContextModule.class)
public abstract class FavouriteDatabaseModule {

    @ApplicationScope
    @Provides
    public static AppDatabase provideAppDatabase(Context context) {
        return Room.databaseBuilder(context,
                AppDatabase.class, AppDatabase.DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
    }

    @ApplicationScope
    @Provides
    public static MovieDao provideMovieDao(AppDatabase appDatabase) {
        return appDatabase.movieDao();
    }
}
