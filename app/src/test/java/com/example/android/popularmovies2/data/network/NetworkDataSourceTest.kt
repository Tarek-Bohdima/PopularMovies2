package com.example.android.popularmovies2.data.network

import com.example.android.popularmovies2.data.model.MovieDto
import com.example.android.popularmovies2.data.model.MoviesList
import com.example.android.popularmovies2.data.model.Review
import com.example.android.popularmovies2.data.model.ReviewsList
import com.example.android.popularmovies2.data.model.Trailer
import com.example.android.popularmovies2.data.model.TrailerList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.Retrofit

class NetworkDataSourceTest {
    private val retrofit = mock<Retrofit>()
    private val movieApi = mock<MovieApi>()

    private lateinit var dataSource: NetworkDataSource

    @Before
    fun setUp() {
        whenever(retrofit.create(MovieApi::class.java)).thenReturn(movieApi)
        dataSource = NetworkDataSource(retrofit)
    }

    @Test
    fun popularMovies_mapsDtosToDomain() = runTest {
        val dtos = listOf(sampleDto(1), sampleDto(2))
        whenever(movieApi.getMoviesByPath(eq("popular"), anyApiKey()))
            .thenReturn(MoviesList(results = dtos))
        val movies = dataSource.popularMovies()
        assertEquals(2, movies.size)
        assertEquals(1, movies[0].movieId)
        assertEquals("title-1", movies[0].originalTitle)
        assertEquals(false, movies[0].isFavorite)
    }

    @Test
    fun popularMovies_returnsEmptyListWhenResultsEmpty() = runTest {
        whenever(movieApi.getMoviesByPath(eq("popular"), anyApiKey()))
            .thenReturn(MoviesList(results = emptyList()))
        assertEquals(emptyList<Any>(), dataSource.popularMovies())
    }

    @Test
    fun topRatedMovies_mapsDtosToDomain() = runTest {
        whenever(movieApi.getMoviesByPath(eq("top_rated"), anyApiKey()))
            .thenReturn(MoviesList(results = listOf(sampleDto(3))))
        val movies = dataSource.topRatedMovies()
        assertEquals(1, movies.size)
        assertEquals(3, movies[0].movieId)
    }

    @Test
    fun reviews_returnsApiResults() = runTest {
        val reviewList = listOf(
            Review(author = "alice", content = "review by alice"),
            Review(author = "bob", content = "review by bob"),
        )
        whenever(movieApi.getReviews(eq("42"), anyApiKey()))
            .thenReturn(ReviewsList(results = reviewList))
        assertEquals(reviewList, dataSource.reviews("42"))
    }

    @Test
    fun trailers_returnsApiResults() = runTest {
        val trailerList = listOf(
            Trailer(name = "trailer-k1"),
            Trailer(name = "trailer-k2"),
        )
        whenever(movieApi.getTrailers(eq("42"), anyApiKey()))
            .thenReturn(TrailerList(results = trailerList))
        assertEquals(trailerList, dataSource.trailers("42"))
    }
}

private fun anyApiKey(): String = org.mockito.kotlin.any()

private fun sampleDto(id: Int) = MovieDto(
    tmdbId = id,
    originalTitle = "title-$id",
    posterPath = "/p$id.jpg",
    backdropPath = "/b$id.jpg",
    overview = "overview $id",
    voteAverage = 7.5,
    releaseDate = "2024-01-0$id".take(10),
)
