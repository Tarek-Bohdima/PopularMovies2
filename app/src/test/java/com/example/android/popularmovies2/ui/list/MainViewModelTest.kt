package com.example.android.popularmovies2.ui.list

import com.example.android.popularmovies2.data.MovieRepository
import com.example.android.popularmovies2.data.local.sampleMovie
import com.example.android.popularmovies2.data.model.Movie
import com.example.android.popularmovies2.data.network.NetworkMonitor
import com.example.android.popularmovies2.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val repository = mock<MovieRepository>()
    private val networkMonitor = mock<NetworkMonitor>()
    private val online = MutableStateFlow(false)
    private val favorites = MutableSharedFlow<List<Movie>>(replay = 1)

    @Before
    fun setUp() {
        whenever(networkMonitor.isOnline).thenReturn(online)
        whenever(repository.favoriteMovies()).thenReturn(favorites)
    }

    private fun newViewModel(): MainViewModel = MainViewModel(repository, networkMonitor)

    @Test
    fun init_kicksOffPopularAndTopRatedFetches() = runTest {
        whenever(repository.fetchPopularMovies()).thenReturn(emptyList())
        whenever(repository.fetchTopRatedMovies()).thenReturn(emptyList())
        newViewModel()
        runCurrent()
        verify(repository).fetchPopularMovies()
        verify(repository).fetchTopRatedMovies()
    }

    @Test
    fun popular_emitsRepositoryResultAfterRefresh() = runTest(UnconfinedTestDispatcher()) {
        val movies = listOf(sampleMovie(1), sampleMovie(2))
        whenever(repository.fetchPopularMovies()).thenReturn(movies)
        whenever(repository.fetchTopRatedMovies()).thenReturn(emptyList())
        val vm = newViewModel()
        assertEquals(movies, vm.popular.value)
    }

    @Test
    fun topRated_emitsRepositoryResultAfterRefresh() = runTest(UnconfinedTestDispatcher()) {
        val movies = listOf(sampleMovie(3))
        whenever(repository.fetchPopularMovies()).thenReturn(emptyList())
        whenever(repository.fetchTopRatedMovies()).thenReturn(movies)
        val vm = newViewModel()
        assertEquals(movies, vm.topRated.value)
    }

    @Test
    fun popular_swallowsFetchFailure() = runTest(UnconfinedTestDispatcher()) {
        whenever(repository.fetchPopularMovies()).thenThrow(RuntimeException("boom"))
        whenever(repository.fetchTopRatedMovies()).thenReturn(emptyList())
        val vm = newViewModel()
        assertEquals(emptyList<Movie>(), vm.popular.value)
    }

    @Test
    fun favorites_collectsFromRepositoryFlow() = runTest(UnconfinedTestDispatcher()) {
        whenever(repository.fetchPopularMovies()).thenReturn(emptyList())
        whenever(repository.fetchTopRatedMovies()).thenReturn(emptyList())
        val vm = newViewModel()
        val sink = mutableListOf<List<Movie>>()
        val job = launch { vm.favorites.toList(sink) }
        favorites.emit(listOf(sampleMovie(4)))
        favorites.emit(listOf(sampleMovie(4), sampleMovie(5)))
        job.cancel()
        // Initial value from stateIn + the two emissions
        assertEquals(3, sink.size)
        assertEquals(emptyList<Movie>(), sink[0])
        assertEquals(1, sink[1].size)
        assertEquals(2, sink[2].size)
    }

    @Test
    fun isOnline_mirrorsNetworkMonitor() = runTest(UnconfinedTestDispatcher()) {
        whenever(repository.fetchPopularMovies()).thenReturn(emptyList())
        whenever(repository.fetchTopRatedMovies()).thenReturn(emptyList())
        val vm = newViewModel()
        val sink = mutableListOf<Boolean>()
        val job = launch { vm.isOnline.toList(sink) }
        online.value = true
        online.value = false
        job.cancel()
        assertEquals(listOf(false, true, false), sink)
    }

    @Test
    fun deleteAllFavoriteMovies_propagatesToRepository() = runTest {
        whenever(repository.fetchPopularMovies()).thenReturn(emptyList())
        whenever(repository.fetchTopRatedMovies()).thenReturn(emptyList())
        whenever(repository.favoriteMovies()).thenReturn(flowOf(emptyList()))
        val vm = newViewModel()
        vm.deleteAllFavoriteMovies()
        runCurrent()
        verify(repository).deleteAllFavoriteMovies()
    }
}
