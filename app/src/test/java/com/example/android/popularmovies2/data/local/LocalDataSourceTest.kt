package com.example.android.popularmovies2.data.local

import androidx.lifecycle.LiveData
import com.example.android.popularmovies2.data.model.Movie
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
        val expected = mock<LiveData<List<Movie>>>()
        whenever(dao.getAllMovies()).thenReturn(expected)
        assertSame(expected, source.getAllMovies())
    }

    @Test
    fun getMovieById_delegatesToDao() {
        val expected = mock<LiveData<Movie>>()
        whenever(dao.getMovieById("42")).thenReturn(expected)
        assertSame(expected, source.getMovieById("42"))
    }

    @Test
    fun insertMovie_delegatesToDao() {
        val movie = sampleMovie(7)
        source.insertMovie(movie)
        verify(dao).insertMovie(movie)
    }

    @Test
    fun deleteMovie_delegatesToDao() {
        val movie = sampleMovie(7)
        source.deleteMovie(movie)
        verify(dao).delete(movie)
    }

    @Test
    fun deleteAllMovies_delegatesToDao() {
        source.deleteAllMovies()
        verify(dao).deleteAllMovies()
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
