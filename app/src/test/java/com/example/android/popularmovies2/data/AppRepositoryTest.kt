package com.example.android.popularmovies2.data

import com.example.android.popularmovies2.data.local.LocalDataSource
import com.example.android.popularmovies2.data.local.sampleMovie
import com.example.android.popularmovies2.data.model.Movie
import com.example.android.popularmovies2.data.model.Review
import com.example.android.popularmovies2.data.model.Trailer
import com.example.android.popularmovies2.data.network.NetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class AppRepositoryTest {
    private val network = mock<NetworkDataSource>()
    private val local = mock<LocalDataSource>()
    private val repository = AppRepository(network, local)

    @Test
    fun fetchPopularMovies_delegatesToNetwork() = runTest {
        val expected = listOf(sampleMovie(1))
        whenever(network.popularMovies()).thenReturn(expected)
        assertEquals(expected, repository.fetchPopularMovies())
    }

    @Test
    fun fetchTopRatedMovies_delegatesToNetwork() = runTest {
        val expected = listOf(sampleMovie(2))
        whenever(network.topRatedMovies()).thenReturn(expected)
        assertEquals(expected, repository.fetchTopRatedMovies())
    }

    @Test
    fun fetchReviews_delegatesToNetwork() = runTest {
        val expected = listOf(Review())
        whenever(network.reviews("42")).thenReturn(expected)
        assertEquals(expected, repository.fetchReviews("42"))
    }

    @Test
    fun fetchTrailers_delegatesToNetwork() = runTest {
        val expected = listOf(Trailer())
        whenever(network.trailers("42")).thenReturn(expected)
        assertEquals(expected, repository.fetchTrailers("42"))
    }

    @Test
    fun favoriteMovies_delegatesToLocal() {
        val expected: Flow<List<Movie>> = flowOf(emptyList())
        whenever(local.getAllMovies()).thenReturn(expected)
        assertSame(expected, repository.favoriteMovies())
    }

    @Test
    fun favoriteMovieById_delegatesToLocal() {
        val expected: Flow<Movie?> = flowOf(null)
        whenever(local.getMovieById("9")).thenReturn(expected)
        assertSame(expected, repository.favoriteMovieById("9"))
    }

    @Test
    fun insertFavoriteMovie_persistsViaLocal() = runTest {
        val movie = sampleMovie(1)
        repository.insertFavoriteMovie(movie)
        verify(local).insertMovie(movie)
    }

    @Test
    fun deleteFavoriteMovie_removesViaLocal() = runTest {
        val movie = sampleMovie(2)
        repository.deleteFavoriteMovie(movie)
        verify(local).deleteMovie(movie)
    }

    @Test
    fun deleteAllFavoriteMovies_clearsViaLocal() = runTest {
        repository.deleteAllFavoriteMovies()
        verify(local).deleteAllMovies()
    }
}
