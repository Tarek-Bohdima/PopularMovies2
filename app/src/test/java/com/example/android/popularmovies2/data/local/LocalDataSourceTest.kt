package com.example.android.popularmovies2.data.local

import com.example.android.popularmovies2.data.model.Movie
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class LocalDataSourceTest {
    private val dao = mock<MovieDao>()
    private val source = LocalDataSource(dao)

    @Test
    fun getAllMovies_mapsDaoEntitiesToDomain() = runTest {
        val entities = listOf(sampleEntity(1), sampleEntity(2))
        whenever(dao.getAllMovies()).thenReturn(flowOf(entities))
        val emitted = source.getAllMovies().toList()
        assertEquals(1, emitted.size)
        assertEquals(2, emitted.first().size)
        assertEquals(1, emitted.first()[0].movieId)
        assertEquals(true, emitted.first()[0].isFavorite)
    }

    @Test
    fun getMovieByTmdbId_mapsDaoEntityToDomain() = runTest {
        whenever(dao.getMovieByTmdbId(42)).thenReturn(flowOf(sampleEntity(42)))
        val emitted = source.getMovieByTmdbId(42).toList()
        assertEquals(1, emitted.size)
        assertEquals(42, emitted.first()?.movieId)
    }

    @Test
    fun getMovieByTmdbId_passesThroughNull() = runTest {
        whenever(dao.getMovieByTmdbId(99)).thenReturn(flowOf(null))
        val emitted = source.getMovieByTmdbId(99).toList()
        assertEquals(listOf<Movie?>(null), emitted)
    }

    @Test
    fun insertMovie_mapsDomainToEntityAndDelegatesToDao() = runTest {
        source.insertMovie(sampleMovie(7))
        verify(dao).insertMovie(sampleEntity(7).copy(id = 0L))
    }

    @Test
    fun deleteMovie_routesToDeleteByTmdbId() = runTest {
        source.deleteMovie(sampleMovie(7))
        verify(dao).deleteByTmdbId(7)
    }

    @Test
    fun deleteAllMovies_delegatesToDao() = runTest {
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

internal fun sampleEntity(id: Int) = MovieEntity(
    id = 0L,
    tmdbId = id,
    originalTitle = "title-$id",
    posterPath = "/p$id.jpg",
    backdropPath = "/b$id.jpg",
    overview = "overview $id",
    voteAverage = 7.5,
    releaseDate = "2024-01-0$id".take(10),
)
