package com.example.android.popularmovies2.data.network

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.android.popularmovies2.data.local.sampleMovie
import com.example.android.popularmovies2.data.model.Movie
import com.example.android.popularmovies2.data.model.MoviesList
import com.example.android.popularmovies2.data.model.Review
import com.example.android.popularmovies2.data.model.ReviewsList
import com.example.android.popularmovies2.data.model.Trailer
import com.example.android.popularmovies2.data.model.TrailerList
import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.Retrofit
import java.lang.reflect.Field

class NetworkDataSourceTest {
    @get:Rule val instantTask = InstantTaskExecutorRule()

    private val retrofit = mock<Retrofit>()
    private val movieApi = mock<MovieApi>()

    private lateinit var dataSource: NetworkDataSource

    @Before
    fun setUp() {
        // Run all RxJava operations synchronously on the calling thread.
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setMainThreadSchedulerHandler { Schedulers.trampoline() }

        whenever(retrofit.create(MovieApi::class.java)).thenReturn(movieApi)
        dataSource = NetworkDataSource(retrofit)
    }

    @After
    fun tearDown() {
        RxJavaPlugins.reset()
        RxAndroidPlugins.reset()
    }

    @Test
    fun getPopularMoviesLiveData_postsFetchedListOnSuccess() {
        val movies = listOf(sampleMovie(1), sampleMovie(2))
        whenever(movieApi.getMoviesByPath(eq("popular"), anyApiKey())).thenReturn(
            Single.just(moviesListOf(movies)),
        )

        val live = dataSource.getPopularMoviesLiveData()
        val captured = live.observeForeverCapturing<List<Movie>>()
        assertEquals(movies, captured)
    }

    @Test
    fun getTopRatedMoviesLiveData_postsFetchedListOnSuccess() {
        val movies = listOf(sampleMovie(3))
        whenever(movieApi.getMoviesByPath(eq("top_rated"), anyApiKey())).thenReturn(
            Single.just(moviesListOf(movies)),
        )

        val live = dataSource.getTopRatedMoviesLiveData()
        val captured = live.observeForeverCapturing<List<Movie>>()
        assertEquals(movies, captured)
    }

    @Test
    fun getPopularMoviesLiveData_swallowsErrorAndKeepsLiveDataNull() {
        whenever(movieApi.getMoviesByPath(eq("popular"), anyApiKey())).thenReturn(
            Single.error(RuntimeException("boom")),
        )

        val live = dataSource.getPopularMoviesLiveData()
        assertNotNull(live)
        assertNull(live.value)
    }

    @Test
    fun getTopRatedMoviesLiveData_swallowsErrorAndKeepsLiveDataNull() {
        whenever(movieApi.getMoviesByPath(eq("top_rated"), anyApiKey())).thenReturn(
            Single.error(RuntimeException("boom")),
        )

        val live = dataSource.getTopRatedMoviesLiveData()
        assertNull(live.value)
    }

    @Test
    fun getReviewsLiveDataByMovieId_postsFetchedListOnSuccess() {
        // Prime the lateinit movieApi field via the popular call first.
        whenever(movieApi.getMoviesByPath(eq("popular"), anyApiKey())).thenReturn(
            Single.just(moviesListOf(emptyList())),
        )
        dataSource.getPopularMoviesLiveData()

        val reviewList = listOf(sampleReview("alice"), sampleReview("bob"))
        whenever(movieApi.getReviews(eq("42"), anyApiKey())).thenReturn(
            Single.just(reviewsListOf(reviewList)),
        )

        val live = dataSource.getReviewsLiveDataByMovieId("42")
        val captured = live.observeForeverCapturing<List<Review>?>()
        assertEquals(reviewList, captured)
    }

    @Test
    fun getReviewsLiveDataByMovieId_swallowsError() {
        whenever(movieApi.getMoviesByPath(eq("popular"), anyApiKey())).thenReturn(
            Single.just(moviesListOf(emptyList())),
        )
        dataSource.getPopularMoviesLiveData()

        whenever(movieApi.getReviews(eq("42"), anyApiKey())).thenReturn(
            Single.error(RuntimeException("nope")),
        )

        val live = dataSource.getReviewsLiveDataByMovieId("42")
        assertNull(live.value)
    }

    @Test
    fun getTrailersLiveDataByMovieId_postsFetchedListOnSuccess() {
        whenever(movieApi.getMoviesByPath(eq("popular"), anyApiKey())).thenReturn(
            Single.just(moviesListOf(emptyList())),
        )
        dataSource.getPopularMoviesLiveData()

        val trailerList = listOf(sampleTrailer("k1"), sampleTrailer("k2"))
        whenever(movieApi.getTrailers(eq("42"), anyApiKey())).thenReturn(
            Single.just(trailerListOf(trailerList)),
        )

        val live = dataSource.getTrailersLiveDataByMovieId("42")
        val captured = live.observeForeverCapturing<List<Trailer>?>()
        assertEquals(trailerList, captured)
    }

    @Test
    fun getTrailersLiveDataByMovieId_swallowsError() {
        whenever(movieApi.getMoviesByPath(eq("popular"), anyApiKey())).thenReturn(
            Single.just(moviesListOf(emptyList())),
        )
        dataSource.getPopularMoviesLiveData()

        whenever(movieApi.getTrailers(eq("42"), anyApiKey())).thenReturn(
            Single.error(RuntimeException("nope")),
        )

        val live = dataSource.getTrailersLiveDataByMovieId("42")
        assertNull(live.value)
    }

    @Test
    fun clearDisposables_emptiesCompositeDisposable() {
        whenever(movieApi.getMoviesByPath(eq("popular"), anyApiKey())).thenReturn(
            Single.never(),
        )
        dataSource.getPopularMoviesLiveData()

        dataSource.clearDisposables()
        // Reflectively read the private field to assert it was cleared.
        val field: Field =
            NetworkDataSource::class.java.getDeclaredField("compositeDisposable").also {
                it.isAccessible = true
            }
        val composite =
            field.get(dataSource) as io.reactivex.rxjava3.disposables.CompositeDisposable
        assertEquals(0, composite.size())
        assertTrue(!composite.isDisposed)
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

private fun <T> androidx.lifecycle.LiveData<T>.observeForeverCapturing(): T {
    var captured: T? = null
    val observer = Observer<T> { captured = it }
    observeForever(observer)
    try {
        @Suppress("UNCHECKED_CAST")
        return captured as T
    } finally {
        removeObserver(observer)
    }
}
