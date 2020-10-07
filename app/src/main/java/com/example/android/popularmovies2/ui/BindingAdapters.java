/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */

package com.example.android.popularmovies2.ui;

import android.view.View;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.example.android.popularmovies2.R;

public class BindingAdapters {
    private String BASE_IMAGE_URL = "https://image.tmdb.org/t/p/";

    @BindingAdapter("imageUrl")
    public static void loadImage(ImageView imageView, String url) {
        if (!url.isEmpty()) {
            Glide.with(imageView.getContext())
                    .load(url)
                    .placeholder(R.drawable.film_poster_placeholder)
                    .centerCrop()
                    .into(imageView);
        }
    }

    @BindingAdapter("visibleGone")
    public static void showHide(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
