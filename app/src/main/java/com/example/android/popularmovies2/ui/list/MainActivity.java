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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.android.popularmovies2.Constants;
import com.example.android.popularmovies2.R;
import com.example.android.popularmovies2.data.model.Movie;
import com.example.android.popularmovies2.databinding.ActivityMainBinding;
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
    ActivityMainBinding activityMainBinding;
    MainViewModel mainActivityViewModel;
    private MovieAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        Context context = this;

        mainActivityViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // Credits to https://stackoverflow.com/a/44187816/8899344
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setGridLayoutManager(context, 2);
        } else {
            setGridLayoutManager(context, 4);
        }
        activityMainBinding.recyclerView.setHasFixedSize(true);
        adapter = new MovieAdapter(movieList, (MovieAdapter.MovieAdapterClickListener) context);
        activityMainBinding.recyclerView.setAdapter(adapter);

        checkConnectivityAndCall(context, POPULAR);
    }

    private void checkConnectivityAndCall(Context context, String path) {
        if (isNetworkConnected(context)) {
            activityMainBinding.setIsConnected(true);
            setupViewModel(path);
        } else {
            activityMainBinding.setIsConnected(false);
        }
    }

    private void setupViewModel(String path) {

        switch (path) {
            case POPULAR:
                mainActivityViewModel.getPopularMovies().observe(this, new Observer<List<Movie>>() {
                    @Override
                    public void onChanged(List<Movie> movies) {
                        Timber.tag(Constants.TAG).d("MainActivity: getPopularMovies Observed");
                        adapter.setMovieData(movies);
                    }
                });
                break;
            case TOP_RATED:
                mainActivityViewModel.getTopRatedMovies().observe(this, new Observer<List<Movie>>() {
                    @Override
                    public void onChanged(List<Movie> movies) {
                        Timber.tag(Constants.TAG).d("MainActivity: getTopRatedMovies Observed");
                        adapter.setMovieData(movies);
                    }
                });
                break;
        }
    }

    private void setGridLayoutManager(Context context, int spanCount) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, spanCount);
        activityMainBinding.recyclerView.setLayoutManager(gridLayoutManager);
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
            case R.id.favorite:
                setTitle("Favorites");
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onListItemClick(Movie currentMovie) {
        Intent mainToDetailIntent = new Intent(MainActivity.this, DetailActivity.class);
        mainToDetailIntent.putExtra(MOVIE_OBJECT, currentMovie);
        startActivity(mainToDetailIntent);
    }
}
