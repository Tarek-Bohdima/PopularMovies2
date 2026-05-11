package com.example.android.popularmovies2.ui.detail

import androidx.lifecycle.SavedStateHandle
import com.example.android.popularmovies2.data.MovieRepository
import com.example.android.popularmovies2.data.local.sampleMovie
import com.example.android.popularmovies2.data.model.Movie
import com.example.android.popularmovies2.data.model.Review
import com.example.android.popularmovies2.data.model.Trailer
import com.example.android.popularmovies2.ui.list.MainActivity.Companion.MOVIE_OBJECT
import com.example.android.popularmovies2.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
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
class DetailViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private val repository = mock<MovieRepository>()
    private val seedMovie = sampleMovie(42)
    private val movieId = seedMovie.movieId.toString()
    private val favoriteFlow = MutableSharedFlow<Movie?>(replay = 1)

    private fun handle() = SavedStateHandle(mapOf(MOVIE_OBJECT to seedMovie))

    @Before
    fun setUp() {
        whenever(repository.favoriteMovieById(movieId)).thenReturn(favoriteFlow)
    }

    private fun newViewModel(): DetailViewModel = DetailViewModel(repository, handle())

    @Test
    fun init_fetchesReviewsAndTrailersForMovieId() = runTest {
        whenever(repository.fetchReviews(movieId)).thenReturn(emptyList())
        whenever(repository.fetchTrailers(movieId)).thenReturn(emptyList())
        newViewModel()
        runCurrent()
        verify(repository).fetchReviews(movieId)
        verify(repository).fetchTrailers(movieId)
    }

    @Test
    fun reviews_emitsRepositoryResult() = runTest(UnconfinedTestDispatcher()) {
        val reviews = listOf(Review(), Review())
        whenever(repository.fetchReviews(movieId)).thenReturn(reviews)
        whenever(repository.fetchTrailers(movieId)).thenReturn(emptyList())
        val vm = newViewModel()
        assertEquals(reviews, vm.reviews.value)
    }

    @Test
    fun trailers_emitsRepositoryResult() = runTest(UnconfinedTestDispatcher()) {
        val trailers = listOf(Trailer())
        whenever(repository.fetchReviews(movieId)).thenReturn(emptyList())
        whenever(repository.fetchTrailers(movieId)).thenReturn(trailers)
        val vm = newViewModel()
        assertEquals(trailers, vm.trailers.value)
    }

    @Test
    fun reviews_swallowsFetchFailure() = runTest(UnconfinedTestDispatcher()) {
        whenever(repository.fetchReviews(movieId)).thenThrow(RuntimeException("boom"))
        whenever(repository.fetchTrailers(movieId)).thenReturn(emptyList())
        val vm = newViewModel()
        assertEquals(emptyList<Review>(), vm.reviews.value)
    }

    @Test
    fun favoriteMovie_collectsFromRepositoryFlow() = runTest(UnconfinedTestDispatcher()) {
        whenever(repository.fetchReviews(movieId)).thenReturn(emptyList())
        whenever(repository.fetchTrailers(movieId)).thenReturn(emptyList())
        val vm = newViewModel()
        val sink = mutableListOf<Movie?>()
        val job = launch { vm.favoriteMovie.toList(sink) }
        favoriteFlow.emit(sampleMovie(1))
        favoriteFlow.emit(null)
        job.cancel()
        assertEquals(3, sink.size)
        assertEquals(null, sink[0])
        assertEquals(sampleMovie(1).movieId, sink[1]?.movieId)
        assertEquals(null, sink[2])
    }

    @Test
    fun insertFavoriteMovie_propagatesToRepository() = runTest {
        whenever(repository.fetchReviews(movieId)).thenReturn(emptyList())
        whenever(repository.fetchTrailers(movieId)).thenReturn(emptyList())
        whenever(repository.favoriteMovieById(movieId)).thenReturn(flowOf(null))
        val vm = newViewModel()
        val movie = sampleMovie(1)
        vm.insertFavoriteMovie(movie)
        runCurrent()
        verify(repository).insertFavoriteMovie(movie)
    }

    @Test
    fun deleteFavoriteMovie_propagatesToRepository() = runTest {
        whenever(repository.fetchReviews(movieId)).thenReturn(emptyList())
        whenever(repository.fetchTrailers(movieId)).thenReturn(emptyList())
        whenever(repository.favoriteMovieById(movieId)).thenReturn(flowOf(null))
        val vm = newViewModel()
        val movie = sampleMovie(2)
        vm.deleteFavoriteMovie(movie)
        runCurrent()
        verify(repository).deleteFavoriteMovie(movie)
    }
}
