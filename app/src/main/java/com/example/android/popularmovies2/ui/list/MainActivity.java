/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */

package com.example.android.popularmovies2.ui.list;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.popularmovies2.R;
import com.example.android.popularmovies2.data.model.Movie;
import com.example.android.popularmovies2.ui.detail.DetailActivity;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static com.example.android.popularmovies2.data.network.ConnectionUtils.isNetworkConnected;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterClickListener {
    public static final String MOVIE_OBJECT = "movie_object";
    private static final String POPULAR = "popular";
    private static final String TOP_RATED = "top_rated";
    public List<Movie> movieList = new ArrayList<>();
    private ImageView errorImageView;
    private RecyclerView recyclerView;
    private MovieAdapter adapter;
    MainViewModel mainActivityViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycler_view);
        errorImageView = findViewById(R.id.connection_error_imageview);
        Context context = this;

        mainActivityViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // Credits to https://stackoverflow.com/a/44187816/8899344
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setGridLayoutManager(context, 2);
        } else {
            setGridLayoutManager(context, 4);
        }

        recyclerView.setHasFixedSize(true);
        adapter = new MovieAdapter(context, movieList, (MovieAdapter.MovieAdapterClickListener) context);
        recyclerView.setAdapter(adapter);

        checkConnectivityAndCall(this, POPULAR);
    }

    private void checkConnectivityAndCall(Context context, String path) {
        if (isNetworkConnected(context)) {
            makeMoviesQuery(path);
        } else {
            showErrorImageView();
        }
    }

    private void setupViewModel(String path) {

        switch (path) {
            case POPULAR:
                mainActivityViewModel.getPopularMovies().observe(this, new Observer<List<Movie>>() {
                    @Override
                    public void onChanged(List<Movie> movies) {
                        Timber.tag("MyApp").d("MainActivity: getPopularMovies Observed");
                        adapter.setMovieData(movies);
                    }
                });
                break;
            case TOP_RATED:
                mainActivityViewModel.getTopRatedMovies().observe(this, new Observer<List<Movie>>() {
                    @Override
                    public void onChanged(List<Movie> movies) {
                        Timber.tag("MyApp").d("MainActivity: getTopRatedMovies Observed");
                        adapter.setMovieData(movies);
                    }
                });
                break;
        }
    }

    private void setGridLayoutManager(Context context, int spanCount) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, spanCount);
        recyclerView.setLayoutManager(gridLayoutManager);
    }


    private void makeMoviesQuery(String path) {
        showRecyclerView();
        setupViewModel(path);
    }

    private void showRecyclerView() {
        errorImageView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorImageView() {
        errorImageView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.sort_by_popularity:
                checkConnectivityAndCall(this, POPULAR);
                setTitle("Popular Movies");
                return true;
            case R.id.sort_by_rating:
                checkConnectivityAndCall(this, TOP_RATED);
                setTitle("Top Rated Movies");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onListItemClick(Movie currentMovie) {
        Intent MainToDetailIntent = new Intent(MainActivity.this, DetailActivity.class);
        MainToDetailIntent.putExtra(MOVIE_OBJECT, currentMovie);
        startActivity(MainToDetailIntent);
    }
}
