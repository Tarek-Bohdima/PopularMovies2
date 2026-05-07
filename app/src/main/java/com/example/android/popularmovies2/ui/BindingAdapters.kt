/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */
package com.example.android.popularmovies2.ui

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.android.popularmovies2.R

@BindingAdapter("posterUrl")
fun loadPoster(imageView: ImageView, url: String?) {
    if (!url.isNullOrEmpty()) {
        Glide.with(imageView.context)
            .load(url)
            .placeholder(R.drawable.film_poster_placeholder)
            .centerCrop()
            .into(imageView)
    }
}

@BindingAdapter("backDropUrl")
fun loadBackdrop(imageView: ImageView, url: String?) {
    if (!url.isNullOrEmpty()) {
        Glide.with(imageView.context)
            .load(url)
            .centerCrop()
            .into(imageView)
    }
}
