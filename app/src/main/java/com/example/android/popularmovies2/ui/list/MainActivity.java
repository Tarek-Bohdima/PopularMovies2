/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */

package com.example.android.popularmovies2.ui.list;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.popularmovies2.BuildConfig;
import com.example.android.popularmovies2.MoviesApp;
import com.example.android.popularmovies2.R;
import com.example.android.popularmovies2.data.AppRepository;
import com.example.android.popularmovies2.data.model.Movie;
import com.example.android.popularmovies2.data.model.MoviesList;
import com.example.android.popularmovies2.data.network.APIError;
import com.example.android.popularmovies2.data.network.ErrorUtils;
import com.example.android.popularmovies2.data.network.MovieApi;
import com.example.android.popularmovies2.data.network.RetrofitClientInstance;
import com.example.android.popularmovies2.ui.detail.DetailActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterClickListener {
    public static final String MOVIE_OBJECT = "movie_object";
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String POPULAR = "popular";
    private static final String TOP_RATED = "top_rated";
    public List<Movie> movieList = new ArrayList<>();
    public MovieApi movieApi;
    // please acquire your API KEY from https://www.themoviedb.org/ then:
    // user your API key in the project's gradle.properties file: MY_TMDB_API_KEY="<your API KEY>"
    String MY_TMDB_API_KEY = BuildConfig.TMDB_API_KEY;
    String BASE_URL = "http://api.themoviedb.org/3/movie/";
    private ImageView errorImageView;
    private RecyclerView recyclerView;
    private MovieAdapter adapter;
    private AppRepository repository;
    LiveData<List<Movie>> topMoviesLiveData;
    LiveData<List<Movie>> popularMoviesLiveData;

    // Credits to https://stackoverflow.com/a/61566780/8899344
    private static boolean isNetworkConnected(Context context) {

        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                NetworkCapabilities capabilities = connectivityManager.
                        getNetworkCapabilities(connectivityManager.getActiveNetwork());
                return capabilities != null &&
                        (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                                || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
            } else {
                NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                return activeNetwork != null && activeNetwork.isConnected();
            }
        }

        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycler_view);
        errorImageView = findViewById(R.id.connection_error_imageview);
        Context context = this;

        repository = ((MoviesApp) getApplication()).getAppRepository();

        // Credits to https://stackoverflow.com/a/44187816/8899344
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setGridLayoutManager(context, 2);
        } else {
            setGridLayoutManager(context, 4);
        }

        recyclerView.setHasFixedSize(true);

        adapter = new MovieAdapter(context, movieList, (MovieAdapter.MovieAdapterClickListener) context);
        recyclerView.setAdapter(adapter);

        /*Create handle for the RetrofitInstance interface*/
        movieApi = RetrofitClientInstance.getRetrofitInstance().create(MovieApi.class);

        checkAndCall(context, POPULAR);
    }

    private void getMovies(String path) {
        Call<MoviesList> callMoviesByPath = movieApi.getMoviesByPath(path, MY_TMDB_API_KEY);

        callMoviesByPath.enqueue(new Callback<MoviesList>() {
            @Override
            public void onResponse(Call<MoviesList> call, Response<MoviesList> response) {
                if (!response.isSuccessful()) {
                    /* TODO notify user about response error in UI */
                    // parse the response body …
                    APIError error = ErrorUtils.parseError(response);
                    Log.d(TAG, "onResponse: " + response.code());
                    Toast.makeText(MainActivity.this, "OnResponse " +
                            error.message(), Toast.LENGTH_LONG).show();
                }
                MoviesList moviesLists = response.body();
                List<Movie> movies = null;
                if (moviesLists != null) {
                    movies = moviesLists.getMovies();
                } else {
                    Log.d(TAG, "onResponse: Error getting movies");
                }
                for (Movie movie : movies) {
                    switch (path) {
                        case POPULAR:
                            movie.setPopular(true);
                            break;
                        case TOP_RATED:
                            movie.setTopRated(true);
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + path);
                    }
                    if (movie != null) {

                        repository.insert(movie);
                    } else {
                        Log.d(TAG, "onResponse: Error inserting movie");
                    }
                }

                topMoviesLiveData = new MutableLiveData<>();
                popularMoviesLiveData = new MutableLiveData<>();

                switch (path) {
                    case POPULAR:
                        popularMoviesLiveData = repository.getPopularMovies();
                        break;
                    case TOP_RATED:
                        topMoviesLiveData = repository.getTopRatedMovies();
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + path);
                }

                topMoviesLiveData.observe(MainActivity.this, new Observer<List<Movie>>() {
                    @Override
                    public void onChanged(List<Movie> movies) {
                        adapter.setMovieData(movies);
                    }
                });

                popularMoviesLiveData.observe(MainActivity.this, new Observer<List<Movie>>() {
                    @Override
                    public void onChanged(List<Movie> movies) {
                        adapter.setMovieData(movies);
                    }
                });
            }

            @Override
            public void onFailure(Call<MoviesList> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                Toast.makeText(MainActivity.this, "onFailure: " +
                        t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void setGridLayoutManager(Context context, int spanCount) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, spanCount);
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    private void checkAndCall(Context context, String path) {
        if (isNetworkConnected(context)) {
            makeMoviesQuery(path);
        } else {
            showErrorImageView();
        }
    }

    private void makeMoviesQuery(String path) {
        showRecyclerView();
        getMovies(path);
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
                checkAndCall(this, POPULAR);
                setTitle("Popular Movies");
                return true;
            case R.id.sort_by_rating:
                checkAndCall(this, TOP_RATED);
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
