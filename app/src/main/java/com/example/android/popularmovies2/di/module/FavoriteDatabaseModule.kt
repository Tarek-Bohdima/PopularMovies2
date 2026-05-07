/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */
package com.example.android.popularmovies2.di.module

import android.content.Context
import androidx.room.Room
import com.example.android.popularmovies2.data.local.AppDatabase
import com.example.android.popularmovies2.data.local.MovieDao
import com.example.android.popularmovies2.di.scopes.ApplicationScope
import dagger.Module
import dagger.Provides

@Module(includes = [ContextModule::class])
object FavoriteDatabaseModule {
    @ApplicationScope
    @Provides
    fun provideAppDatabase(context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()

    @ApplicationScope
    @Provides
    fun provideMovieDao(appDatabase: AppDatabase): MovieDao = appDatabase.movieDao()
}
