/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */
package com.example.android.popularmovies2.ui.list

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.android.popularmovies2.Constants
import com.example.android.popularmovies2.R
import com.example.android.popularmovies2.data.model.Movie
import com.example.android.popularmovies2.databinding.ActivityMainBinding
import com.example.android.popularmovies2.ui.detail.DetailActivity
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivity : AppCompatActivity(), MovieAdapter.MovieItemClickListener {
    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var mainActivityViewModel: MainViewModel
    private lateinit var adapter: MovieAdapter
    private val movieList: List<Movie> = ArrayList()
    private var menuItem: MenuItem? = null
    private var menuItemSelected: String = POPULAR

    // Tracks the LiveData currently being observed so we don't stack observers each
    // time connectivity flips. Cleared when the user selects a new menu (forces re-observe).
    private var observedPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        activityMainBinding.lifecycleOwner = this
        mainActivityViewModel = ViewModelProvider(
            this,
            MainViewModelFactory(application),
        )[MainViewModel::class.java]
        changeSpanCountByOrientation()

        checkStateAfterOrientationChange(savedInstanceState)
        setScreenTitleByMenuSelected(menuItemSelected)
        setRecyclerView()
        swipeToRefresh()
        observeConnectivity()
    }

    private fun observeConnectivity() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainActivityViewModel.isOnline.collect { online ->
                    renderForConnectivity(online, menuItemSelected)
                }
            }
        }
    }

    private fun swipeToRefresh() {
        activityMainBinding.swiperefresh.setOnRefreshListener {
            if (menuItemSelected.isNotEmpty()) {
                Timber.tag(Constants.TAG)
                    .d("MainActivity: onCreate() called with: menuItemSelected = [$menuItemSelected]")
                swipeUpdateOperation()
            }
            activityMainBinding.swiperefresh.isRefreshing = false
        }
    }

    private fun setRecyclerView() {
        activityMainBinding.recyclerView.setHasFixedSize(true)
        adapter = MovieAdapter(movieList, this)
        activityMainBinding.recyclerView.adapter = adapter
    }

    private fun setScreenTitleByMenuSelected(menuItemSelected: String) {
        when (menuItemSelected) {
            POPULAR -> title = POPULAR_MOVIES_TITLE
            TOP_RATED -> title = TOP_RATED_MOVIES_TITLE
            FAVORITES -> {
                title = FAVORITES
                Timber.tag(Constants.TAG).d(
                    "MainActivity: setScreenTitleByMenuSelected() called with: menuItemSelected = [${menuItemSelected}FAVOURITES = $FAVORITES]",
                )
            }
        }
    }

    private fun changeSpanCountByOrientation() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setGridLayoutManager(this, 2)
        } else {
            setGridLayoutManager(this, 4)
        }
    }

    private fun checkStateAfterOrientationChange(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            menuItemSelected = POPULAR
        } else {
            menuItemSelected = savedInstanceState.getString(MENU_ITEM_SELECTED) ?: POPULAR
            Timber.tag(Constants.TAG).d(
                "MainActivity: checkStateAfterOrientationChange() called with: menuItemSelected = [$menuItemSelected, MENU_ITEM_SELECTED=  $MENU_ITEM_SELECTED]",
            )
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        menuItem?.let { menuItemSelected = it.title.toString() }
        Timber.tag(Constants.TAG).d("onSaveInstanceState() called with: outState = [$menuItemSelected]")
        outState.putString(MENU_ITEM_SELECTED, menuItemSelected)
    }

    private fun swipeUpdateOperation() {
        menuItem?.let { menuItemSelected = it.title.toString() }
        activityMainBinding.swiperefresh.isRefreshing = true
        Timber.tag(Constants.TAG)
            .d("onRefresh() called from SwipeRefreshLayout menuItemSelected = %s", menuItemSelected)
        observedPath = null
        renderForConnectivity(mainActivityViewModel.isOnline.value, menuItemSelected)
        activityMainBinding.swiperefresh.isRefreshing = false
    }

    private fun setGridLayoutManager(context: Context, spanCount: Int) {
        activityMainBinding.recyclerView.layoutManager = GridLayoutManager(context, spanCount)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_refresh -> {
                swipeUpdateOperation()
                true
            }
            R.id.sort_by_popularity -> {
                menuItem = item
                Timber.tag(Constants.TAG)
                    .d("MainActivity: onOptionsItemSelected() called with: item = [$item]")
                selectMenu(POPULAR)
                title = POPULAR_MOVIES_TITLE
                true
            }
            R.id.sort_by_rating -> {
                menuItem = item
                Timber.tag(Constants.TAG)
                    .d("MainActivity: onOptionsItemSelected() called with: item = [$item]")
                selectMenu(TOP_RATED)
                title = TOP_RATED_MOVIES_TITLE
                true
            }
            R.id.favorites -> {
                menuItem = item
                Timber.tag(Constants.TAG)
                    .d("MainActivity: onOptionsItemSelected() called with: item = [$item]")
                selectMenu(FAVORITES)
                title = FAVORITES
                true
            }
            R.id.delete_all_favorites -> {
                mainActivityViewModel.deleteAllFavoriteMovies()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun selectMenu(newMenu: String) {
        menuItemSelected = newMenu
        observedPath = null
        renderForConnectivity(mainActivityViewModel.isOnline.value, newMenu)
    }

    private fun renderForConnectivity(online: Boolean, menuItem: String) {
        val canShowContent = online || menuItem == FAVORITES
        if (!canShowContent) {
            showErrorImage()
            observedPath = null
            return
        }
        showRecyclerView()
        val targetPath = if (online) menuItem else FAVORITES
        if (observedPath != targetPath) {
            setupViewModel(targetPath)
            observedPath = targetPath
        }
    }

    private fun showRecyclerView() {
        activityMainBinding.recyclerView.visibility = View.VISIBLE
        activityMainBinding.connectionErrorImageview.visibility = View.GONE
    }

    private fun showErrorImage() {
        activityMainBinding.recyclerView.visibility = View.GONE
        activityMainBinding.connectionErrorImageview.visibility = View.VISIBLE
    }

    private fun setupViewModel(path: String) {
        when (path) {
            POPULAR -> mainActivityViewModel.getPopularMovies().observe(this) { movies ->
                Timber.tag(Constants.TAG).d("MainActivity: getPopularMovies Observed")
                adapter.setMovieData(movies)
            }
            TOP_RATED -> mainActivityViewModel.getTopRatedMovies().observe(this) { movies ->
                Timber.tag(Constants.TAG).d("MainActivity: getTopRatedMovies Observed")
                adapter.setMovieData(movies)
            }
            FAVORITES -> mainActivityViewModel.getFavoriteMovies().observe(this) { movies ->
                Timber.tag(Constants.TAG).d("MainActivity: getFavouriteMovies Observed")
                adapter.setMovieData(movies)
            }
        }
    }

    override fun onMovieItemClicked(movie: Movie) {
        val mainToDetailIntent = Intent(this@MainActivity, DetailActivity::class.java)
        mainToDetailIntent.putExtra(MOVIE_OBJECT, movie)
        startActivity(mainToDetailIntent)
    }

    companion object {
        const val MOVIE_OBJECT = "movie_object"
        private const val POPULAR = "Popular"
        private const val TOP_RATED = "Top Rated"
        private const val FAVORITES = "Favorites"
        private const val MENU_ITEM_SELECTED = "menuItemSelected"
        private const val POPULAR_MOVIES_TITLE = "Popular Movies"
        private const val TOP_RATED_MOVIES_TITLE = "Top Rated Movies"
    }
}
