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
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.android.popularmovies2.Constants;
import com.example.android.popularmovies2.R;
import com.example.android.popularmovies2.data.model.Movie;
import com.example.android.popularmovies2.databinding.ActivityMainBinding;
import com.example.android.popularmovies2.ui.detail.DetailActivity;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static com.example.android.popularmovies2.data.network.ConnectionUtils.isNetworkConnected;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieItemClickListener {
    public static final String MOVIE_OBJECT = "movie_object";
    private static final String POPULAR = "Popular";
    private static final String TOP_RATED = "Top Rated";
    private static final String FAVORITES = "Favorites";
    private final String MENU_ITEM_SELECTED = "menuItemSelected";
    private final String POPULAR_MOVIES_TITLE = "Popular Movies";
    private final String TOP_RATED_MOVIES_TITLE = "Top Rated Movies";
    private final List<Movie> movieList = new ArrayList<>();
    private ActivityMainBinding activityMainBinding;
    private MainViewModel mainActivityViewModel;
    private MovieAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MenuItem menuItem;
    private String menuItemSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        activityMainBinding.setLifecycleOwner(this);
        mainActivityViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        changeSpanCountByOrientation();

        checkStateAfterOrientationChange(savedInstanceState);
        setScreenTitleByMenuSelected(menuItemSelected);
        setRecyclerView();
        swipeToRefresh();
        checkConnectivityAndCall(this, menuItemSelected);
    }

    private void swipeToRefresh() {
        swipeRefreshLayout = activityMainBinding.swiperefresh;
        /*
         * Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
         * performs a swipe-to-refresh gesture.
         */
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // This method performs the actual data-refresh operation.
            // The method calls setRefreshing(false) when it's finished.
            if (!TextUtils.isEmpty(menuItemSelected)) {
                Timber.tag(Constants.TAG).d("MainActivity: onCreate() called with: menuItemSelected = [" + menuItemSelected + "]");
                swipeUpdateOperation();
            }
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void setRecyclerView() {
        activityMainBinding.recyclerView.setHasFixedSize(true);
        adapter = new MovieAdapter(movieList, this);
        activityMainBinding.recyclerView.setAdapter(adapter);
    }

    private void setScreenTitleByMenuSelected(String menuItemSelected) {
        switch (menuItemSelected) {
            case POPULAR:
                setTitle(POPULAR_MOVIES_TITLE);
                break;
            case TOP_RATED:
                setTitle(TOP_RATED_MOVIES_TITLE);
                break;
            case FAVORITES:
                setTitle(FAVORITES);
                Timber.tag(Constants.TAG).d("MainActivity: setScreenTitleByMenuSelected() called with: menuItemSelected = [" + menuItemSelected + "FAVOURITES = " + FAVORITES + "]");
                break;
        }
    }

    private void changeSpanCountByOrientation() {
        // Credits to https://stackoverflow.com/a/44187816/8899344
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setGridLayoutManager(this, 2);
        } else {
            setGridLayoutManager(this, 4);
        }
    }

    private void checkStateAfterOrientationChange(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            menuItemSelected = POPULAR;
        } else {
            menuItemSelected = savedInstanceState.getString(MENU_ITEM_SELECTED);
            Timber.tag(Constants.TAG).d("MainActivity: checkStateAfterOrientationChange() called with: menuItemSelected = [" + menuItemSelected + ", MENU_ITEM_SELECTED=  " + MENU_ITEM_SELECTED + "]");
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (menuItem != null) {
            menuItemSelected = menuItem.getTitle().toString();
        }
        Timber.tag(Constants.TAG).d("onSaveInstanceState() called with: outState = [" + menuItemSelected + "]");
        outState.putString(MENU_ITEM_SELECTED, menuItemSelected);
    }

    private void swipeUpdateOperation() {
        if (menuItem != null) {
            menuItemSelected = menuItem.getTitle().toString();
        }
        // Signal SwipeRefreshLayout to start the progress indicator
        swipeRefreshLayout.setRefreshing(true);
        Timber.tag(Constants.TAG).d("onRefresh() called from SwipeRefreshLayout menuItemSelected = %s", menuItemSelected);
        checkConnectivityAndCall(this, menuItemSelected);
        swipeRefreshLayout.setRefreshing(false);
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

    // TODO: try cleaning this a bit by separating maybe!
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menu_refresh) {
            // Start the refresh background task.
            // This method calls setRefreshing(false) when it's finished.
            swipeUpdateOperation();
            return true;
        } else if (itemId == R.id.sort_by_popularity) {
            menuItem = item;
            Timber.tag(Constants.TAG).d("MainActivity: onOptionsItemSelected() called with: item = [" + item + "]");
            checkConnectivityAndCall(this, POPULAR);
            setTitle(POPULAR_MOVIES_TITLE);
            return true;
        } else if (itemId == R.id.sort_by_rating) {
            menuItem = item;
            Timber.tag(Constants.TAG).d("MainActivity: onOptionsItemSelected() called with: item = [" + item + "]");
            checkConnectivityAndCall(this, TOP_RATED);
            setTitle(TOP_RATED_MOVIES_TITLE);
            return true;
        } else if (itemId == R.id.favorites) {
            menuItem = item;
            Timber.tag(Constants.TAG).d("MainActivity: onOptionsItemSelected() called with: item = [" + item + "]");
            checkConnectivityAndCall(this, FAVORITES);
            setTitle(FAVORITES);
            return true;
        } else if (itemId == R.id.delete_all_favorites) {
            mainActivityViewModel.deleteAllFavoriteMovies();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkConnectivityAndCall(Context context, String menuItemSelected) {

        if (isNetworkConnected(context)) {
            showRecyclerView();
            setupViewModel(menuItemSelected);
        } else if (menuItemSelected.equals(FAVORITES) && !isNetworkConnected(context)) {
            Timber.tag(Constants.TAG).d("MainActivity: checkConnectivityAndCall() called with: menuItemSelected.equals(FAVOURITES) = %s and !isNetworkConnected(context) = %s"
                    , menuItemSelected.equals(FAVORITES), !isNetworkConnected(context));
            showRecyclerView();
            setupViewModel(FAVORITES);
        } else if (!isNetworkConnected(context)) {
            showErrorImage();
        }
    }

    private void showRecyclerView() {
        activityMainBinding.recyclerView.setVisibility(View.VISIBLE);
        activityMainBinding.connectionErrorImageview.setVisibility(View.GONE);
    }

    private void showErrorImage() {
        activityMainBinding.recyclerView.setVisibility(View.GONE);
        activityMainBinding.connectionErrorImageview.setVisibility(View.VISIBLE);
    }

    private void setupViewModel(String path) {

        switch (path) {
            case POPULAR:
                mainActivityViewModel.getPopularMovies().observe(this, movies -> {
                    Timber.tag(Constants.TAG).d("MainActivity: getPopularMovies Observed");
                    adapter.setMovieData(movies);
                });
                break;
            case TOP_RATED:
                mainActivityViewModel.getTopRatedMovies().observe(this, movies -> {
                    Timber.tag(Constants.TAG).d("MainActivity: getTopRatedMovies Observed");
                    adapter.setMovieData(movies);
                });
                break;
            case FAVORITES:
                mainActivityViewModel.getFavoriteMovies().observe(this, movies -> {
                    Timber.tag(Constants.TAG).d("MainActivity: getFavouriteMovies Observed");
                    adapter.setMovieData(movies);
                });
                break;
        }
    }

    @Override
    public void onMovieItemClicked(Movie currentMovie) {
        Intent mainToDetailIntent = new Intent(MainActivity.this, DetailActivity.class);
        mainToDetailIntent.putExtra(MOVIE_OBJECT, currentMovie);
        startActivity(mainToDetailIntent);
    }
}
