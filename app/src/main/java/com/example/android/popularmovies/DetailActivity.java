/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */

package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import static com.example.android.popularmovies.MainActivity.MOVIE_OBJECT;
import static com.example.android.popularmovies.MovieAdapter.buildBackdropImageUrl;
import static com.example.android.popularmovies.MovieAdapter.buildPosterImageUrl;

public class DetailActivity extends AppCompatActivity {

    private final Context context = DetailActivity.this;
    private Movie detailMovie;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ImageView poster = findViewById(R.id.imageview_poster);
        ImageView backdrop = findViewById(R.id.imageview_backdrop);
        TextView originalTitle = findViewById(R.id.original_title);
        TextView releaseDate = findViewById(R.id.release_date);
        TextView userRating = findViewById(R.id.user_rating);
        TextView synopsisText = findViewById(R.id.synopsis_text);


        Intent extraIntent = getIntent();
        if (extraIntent != null) {
            if (extraIntent.hasExtra(MOVIE_OBJECT)) {
                detailMovie = extraIntent.getParcelableExtra(MOVIE_OBJECT);
            }
        }

        Glide.with(context)
                .load(buildPosterImageUrl(detailMovie.getPosterPath()))
                .into(poster);

        Glide.with(context)
                .load(buildBackdropImageUrl(detailMovie.getBackdropPath()))
                .into(backdrop);

        originalTitle.setText(detailMovie.getOriginalTitle());
        releaseDate.setText(detailMovie.getReleaseDate());
        userRating.setText(String.valueOf(detailMovie.getVoteAverage()));
        synopsisText.setText(detailMovie.getOverview());

        setTitle(detailMovie.getOriginalTitle());

    }
}
