package com.example.android.popularmovies2.ui.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.popularmovies2.data.AppRepository
import com.example.android.popularmovies2.data.model.Movie
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class MainViewModelTest {
    @get:Rule val instantTask = InstantTaskExecutorRule()

    private val repository = mock<AppRepository>()
    private val popular: LiveData<List<Movie>> = MutableLiveData(emptyList())
    private val topRated: LiveData<List<Movie>> = MutableLiveData(emptyList())
    private val favorites: LiveData<List<Movie>> = MutableLiveData(emptyList())
    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        whenever(repository.getPopularMovies()).thenReturn(popular)
        whenever(repository.getTopRatedMovies()).thenReturn(topRated)
        whenever(repository.getAllFavoriteMovies()).thenReturn(favorites)
        viewModel = MainViewModel(repository)
    }

    @Test
    fun init_pullsThreeCollectionsFromRepository() {
        verify(repository).getPopularMovies()
        verify(repository).getTopRatedMovies()
        verify(repository).getAllFavoriteMovies()
    }

    @Test
    fun getPopularMovies_returnsTheCachedLiveData() {
        assertSame(popular, viewModel.getPopularMovies())
    }

    @Test
    fun getTopRatedMovies_returnsTheCachedLiveData() {
        assertSame(topRated, viewModel.getTopRatedMovies())
    }

    @Test
    fun getFavoriteMovies_returnsTheCachedLiveData() {
        assertSame(favorites, viewModel.getFavoriteMovies())
    }

    @Test
    fun deleteAllFavoriteMovies_propagatesToRepository() {
        viewModel.deleteAllFavoriteMovies()
        verify(repository).deleteAllFavoriteMovies()
    }
}
