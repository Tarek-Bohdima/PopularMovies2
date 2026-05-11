package com.example.android.popularmovies2.ui.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.popularmovies2.data.AppRepository
import com.example.android.popularmovies2.data.model.Movie
import com.example.android.popularmovies2.data.network.NetworkMonitor
import com.example.android.popularmovies2.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {
    @get:Rule val instantTask = InstantTaskExecutorRule()
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val repository = mock<AppRepository>()
    private val networkMonitor = mock<NetworkMonitor>()
    private val online = MutableStateFlow(false)
    private val popular: LiveData<List<Movie>> = MutableLiveData(emptyList())
    private val topRated: LiveData<List<Movie>> = MutableLiveData(emptyList())
    private val favorites: LiveData<List<Movie>> = MutableLiveData(emptyList())
    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        whenever(repository.getPopularMovies()).thenReturn(popular)
        whenever(repository.getTopRatedMovies()).thenReturn(topRated)
        whenever(repository.getAllFavoriteMovies()).thenReturn(favorites)
        whenever(networkMonitor.isOnline).thenReturn(online)
        viewModel = MainViewModel(repository, networkMonitor)
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

    @Test
    fun isOnline_mirrorsNetworkMonitorEmissions() = runTest {
        online.value = true
        assertEquals(true, viewModel.isOnline.first())
        online.value = false
        assertEquals(false, viewModel.isOnline.first())
    }
}
