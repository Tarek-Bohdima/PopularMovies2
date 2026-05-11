/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */
package com.example.android.popularmovies2.ui

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.example.android.popularmovies2.R
import com.example.android.popularmovies2.ui.image.ImageLoaderHost

@BindingAdapter("posterUrl")
fun loadPoster(imageView: ImageView, url: String?) {
    imageView.imageLoader().load(imageView, url, R.drawable.film_poster_placeholder)
}

@BindingAdapter("backDropUrl")
fun loadBackdrop(imageView: ImageView, url: String?) {
    imageView.imageLoader().load(imageView, url)
}

private fun ImageView.imageLoader() =
    (context.applicationContext as ImageLoaderHost).imageLoader
