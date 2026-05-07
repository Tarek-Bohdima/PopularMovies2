/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */
package com.example.android.popularmovies2

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class AppExecutors private constructor(
    private val diskIO: Executor,
    private val networkIO: Executor,
    private val mainThread: Executor,
) {
    fun diskIO(): Executor = diskIO

    fun networkIO(): Executor = networkIO

    fun mainThread(): Executor = mainThread

    private class MainThreadExecutor : Executor {
        private val mainThreadHandler = Handler(Looper.getMainLooper())

        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }
    }

    companion object {
        @Volatile
        private var instance: AppExecutors? = null

        @JvmStatic
        fun getInstance(): AppExecutors =
            instance ?: synchronized(this) {
                instance ?: AppExecutors(
                    Executors.newSingleThreadExecutor(),
                    Executors.newFixedThreadPool(3),
                    MainThreadExecutor(),
                ).also { instance = it }
            }
    }
}
