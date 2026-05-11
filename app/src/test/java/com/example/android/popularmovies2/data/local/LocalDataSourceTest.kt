package com.example.android.popularmovies2.data.local

import com.example.android.popularmovies2.data.model.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class LocalDataSourceTest {
    private val dao = mock<MovieDao>()
    private val source = LocalDataSource(dao)

    @Test
    fun getAllMovies_delegatesToDao() {
        val expected: Flow<List<Movie>> = flowOf(emptyList())
        whenever(dao.getAllMovies()).thenReturn(expected)
        assertSame(expected, source.getAllMovies())
    }

    @Test
    fun getMovieById_delegatesToDao() {
        val expected: Flow<Movie?> = flowOf(null)
        whenever(dao.getMovieById("42")).thenReturn(expected)
        assertSame(expected, source.getMovieById("42"))
    }

    @Test
    fun insertMovie_delegatesToDao() = runTest {
        val movie = sampleMovie(7)
        source.insertMovie(movie)
        verify(dao).insertMovie(movie)
    }

    @Test
    fun deleteMovie_delegatesToDao() = runTest {
        val movie = sampleMovie(7)
        source.deleteMovie(movie)
        verify(dao).delete(movie)
    }

    @Test
    fun deleteAllMovies_delegatesToDao() = runTest {
        source.deleteAllMovies()
        verify(dao).deleteAllMovies()
    }

    @Test
    fun getAllMovies_emitsDaoFlowValues() = runTest {
        val movies = listOf(sampleMovie(1), sampleMovie(2))
        whenever(dao.getAllMovies()).thenReturn(flowOf(movies))
        val emitted = mutableListOf<List<Movie>>()
        source.getAllMovies().collect { emitted += it }
        assertEquals(listOf(movies), emitted)
    }
}

internal fun sampleMovie(id: Int) = Movie(
    movieId = id,
    originalTitle = "title-$id",
    posterPath = "/p$id.jpg",
    backdropPath = "/b$id.jpg",
    overview = "overview $id",
    voteAverage = 7.5,
    releaseDate = "2024-01-0$id".take(10),
    isFavorite = false,
)
