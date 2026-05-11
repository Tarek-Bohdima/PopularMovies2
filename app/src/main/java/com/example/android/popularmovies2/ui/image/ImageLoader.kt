/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */
package com.example.android.popularmovies2.ui.image

import android.widget.ImageView
import androidx.annotation.DrawableRes

/**
 * UI-facing image loader. Callsites (the `@BindingAdapter` functions, future
 * Compose `AsyncImage` wrapper, etc.) depend on this interface; the concrete
 * loading library (Coil today, possibly something else tomorrow) is bound by the
 * `Application`-scoped [ImageLoaderHost] implementation.
 */
interface ImageLoader {
    fun load(view: ImageView, url: String?, @DrawableRes placeholderRes: Int? = null)
}

/**
 * Marker implemented by the `Application` so any [android.content.Context] can reach
 * the app-scoped [ImageLoader] without knowing the concrete `Application` type.
 *
 * Callsites: `(context.applicationContext as ImageLoaderHost).imageLoader.load(...)`.
 */
interface ImageLoaderHost {
    val imageLoader: ImageLoader
}
