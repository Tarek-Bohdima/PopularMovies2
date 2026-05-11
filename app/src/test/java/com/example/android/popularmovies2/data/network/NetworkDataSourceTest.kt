package com.example.android.popularmovies2.data.network

import com.example.android.popularmovies2.data.local.sampleMovie
import com.example.android.popularmovies2.data.model.Movie
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
    fun popularMovies_returnsApiResults() = runTest {
        val movies = listOf(sampleMovie(1), sampleMovie(2))
        whenever(movieApi.getMoviesByPath(eq("popular"), anyApiKey()))
            .thenReturn(moviesListOf(movies))
        assertEquals(movies, dataSource.popularMovies())
    }

    @Test
    fun popularMovies_returnsEmptyListWhenResultsNull() = runTest {
        whenever(movieApi.getMoviesByPath(eq("popular"), anyApiKey()))
            .thenReturn(MoviesList::class.java.newInstance())
        assertEquals(emptyList<Movie>(), dataSource.popularMovies())
    }

    @Test
    fun topRatedMovies_returnsApiResults() = runTest {
        val movies = listOf(sampleMovie(3))
        whenever(movieApi.getMoviesByPath(eq("top_rated"), anyApiKey()))
            .thenReturn(moviesListOf(movies))
        assertEquals(movies, dataSource.topRatedMovies())
    }

    @Test
    fun reviews_returnsApiResults() = runTest {
        val reviewList = listOf(sampleReview("alice"), sampleReview("bob"))
        whenever(movieApi.getReviews(eq("42"), anyApiKey()))
            .thenReturn(reviewsListOf(reviewList))
        assertEquals(reviewList, dataSource.reviews("42"))
    }

    @Test
    fun trailers_returnsApiResults() = runTest {
        val trailerList = listOf(sampleTrailer("k1"), sampleTrailer("k2"))
        whenever(movieApi.getTrailers(eq("42"), anyApiKey()))
            .thenReturn(trailerListOf(trailerList))
        assertEquals(trailerList, dataSource.trailers("42"))
    }
}

private fun anyApiKey(): String = org.mockito.kotlin.any()

private fun moviesListOf(movies: List<Movie>): MoviesList = MoviesList::class.java
    .newInstance()
    .also { setPrivate(it, "results", movies) }

private fun reviewsListOf(reviews: List<Review>): ReviewsList = ReviewsList::class.java
    .newInstance()
    .also { setPrivate(it, "reviews", reviews) }

private fun trailerListOf(trailers: List<Trailer>): TrailerList = TrailerList::class.java
    .newInstance()
    .also { setPrivate(it, "trailers", trailers) }

private fun sampleReview(authorName: String): Review = Review().apply {
    author = authorName
    content = "review by $authorName"
}

private fun sampleTrailer(trailerKey: String): Trailer = Trailer().apply {
    name = "trailer-$trailerKey"
}

private fun setPrivate(target: Any, fieldName: String, value: Any?) {
    val field = target.javaClass.getDeclaredField(fieldName)
    field.isAccessible = true
    field.set(target, value)
}
