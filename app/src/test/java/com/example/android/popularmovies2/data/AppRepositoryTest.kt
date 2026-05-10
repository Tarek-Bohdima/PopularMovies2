package com.example.android.popularmovies2.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.popularmovies2.data.local.LocalDataSource
import com.example.android.popularmovies2.data.local.sampleMovie
import com.example.android.popularmovies2.data.model.Movie
import com.example.android.popularmovies2.data.model.Review
import com.example.android.popularmovies2.data.model.Trailer
import com.example.android.popularmovies2.data.network.NetworkDataSource
import org.junit.Assert.assertSame
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.timeout
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class AppRepositoryTest {
    @get:Rule val instantTask = InstantTaskExecutorRule()

    private val network = mock<NetworkDataSource>()
    private val local = mock<LocalDataSource>()
    private val repository = AppRepository(network, local)

    @Test
    fun getPopularMovies_delegatesToNetwork() {
        val expected: LiveData<List<Movie>> = MutableLiveData(emptyList())
        whenever(network.getPopularMoviesLiveData()).thenReturn(expected)
        assertSame(expected, repository.getPopularMovies())
    }

    @Test
    fun getTopRatedMovies_delegatesToNetwork() {
        val expected: LiveData<List<Movie>> = MutableLiveData(emptyList())
        whenever(network.getTopRatedMoviesLiveData()).thenReturn(expected)
        assertSame(expected, repository.getTopRatedMovies())
    }

    @Test
    fun getReviewsByMovieId_delegatesToNetwork() {
        val expected: LiveData<List<Review>?> = MutableLiveData()
        whenever(network.getReviewsLiveDataByMovieId("42")).thenReturn(expected)
        assertSame(expected, repository.getReviewsByMovieId("42"))
    }

    @Test
    fun getTrailersByMovieId_delegatesToNetwork() {
        val expected: LiveData<List<Trailer>?> = MutableLiveData()
        whenever(network.getTrailersLiveDataByMovieId("42")).thenReturn(expected)
        assertSame(expected, repository.getTrailersByMovieId("42"))
    }

    @Test
    fun getAllFavoriteMovies_delegatesToLocal() {
        val expected: LiveData<List<Movie>> = MutableLiveData(emptyList())
        whenever(local.getAllMovies()).thenReturn(expected)
        assertSame(expected, repository.getAllFavoriteMovies())
    }

    @Test
    fun getFavoriteMovieById_delegatesToLocal() {
        val expected: LiveData<Movie> = MutableLiveData()
        whenever(local.getMovieById("9")).thenReturn(expected)
        assertSame(expected, repository.getFavoriteMovieById("9"))
    }

    @Test
    fun insertFavoriteMovie_persistsViaLocal() {
        val movie = sampleMovie(1)
        repository.insertFavoriteMovie(movie)
        verify(local, timeout(2_000)).insertMovie(movie)
    }

    @Test
    fun deleteFavoriteMovie_removesViaLocal() {
        val movie = sampleMovie(2)
        repository.deleteFavoriteMovie(movie)
        verify(local, timeout(2_000)).deleteMovie(movie)
    }

    @Test
    fun deleteAllFavoriteMovies_clearsViaLocal() {
        repository.deleteAllFavoriteMovies()
        verify(local, timeout(2_000)).deleteAllMovies()
    }

    @Test
    fun clearDisposables_propagatesToNetwork() {
        repository.clearDisposables()
        verify(network).clearDisposables()
    }
}
