/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */
package com.example.android.popularmovies2.ui.list

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.popularmovies2.MoviesApp
import com.example.android.popularmovies2.data.network.ConnectivityManagerNetworkMonitor

class MainViewModelFactory(
    private val application: Application,
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        MainViewModel(
            appRepository = (application as MoviesApp).movieComponent.getAppRepository(),
            networkMonitor = ConnectivityManagerNetworkMonitor(application),
        ) as T
}
