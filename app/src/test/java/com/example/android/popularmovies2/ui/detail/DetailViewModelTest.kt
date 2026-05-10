package com.example.android.popularmovies2.ui.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.popularmovies2.data.AppRepository
import com.example.android.popularmovies2.data.local.sampleMovie
import com.example.android.popularmovies2.data.model.Movie
import com.example.android.popularmovies2.data.model.Review
import com.example.android.popularmovies2.data.model.Trailer
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class DetailViewModelTest {
    @get:Rule val instantTask = InstantTaskExecutorRule()

    private val repository = mock<AppRepository>()
    private val movieId = "42"
    private val reviews: LiveData<List<Review>?> = MutableLiveData()
    private val trailers: LiveData<List<Trailer>?> = MutableLiveData()
    private val favorite: LiveData<Movie> = MutableLiveData()
    private lateinit var viewModel: DetailViewModel

    @Before
    fun setUp() {
        whenever(repository.getReviewsByMovieId(movieId)).thenReturn(reviews)
        whenever(repository.getTrailersByMovieId(movieId)).thenReturn(trailers)
        whenever(repository.getFavoriteMovieById(movieId)).thenReturn(favorite)
        viewModel = DetailViewModel(repository, movieId)
    }

    @Test
    fun init_pullsReviewsTrailersAndFavoriteForMovieId() {
        verify(repository).getReviewsByMovieId(movieId)
        verify(repository).getTrailersByMovieId(movieId)
        verify(repository).getFavoriteMovieById(movieId)
    }

    @Test
    fun getReviewsByMovieId_returnsCachedLiveData() {
        assertSame(reviews, viewModel.getReviewsByMovieId())
    }

    @Test
    fun getTrailersByMovieId_returnsCachedLiveData() {
        assertSame(trailers, viewModel.getTrailersByMovieId())
    }

    @Test
    fun getFavoriteMovieById_returnsCachedLiveData() {
        assertSame(favorite, viewModel.getFavoriteMovieById())
    }

    @Test
    fun insertFavoriteMovie_propagatesToRepository() {
        val movie = sampleMovie(1)
        viewModel.insertFavoriteMovie(movie)
        verify(repository).insertFavoriteMovie(movie)
    }

    @Test
    fun deleteFavoriteMovie_propagatesToRepository() {
        val movie = sampleMovie(2)
        viewModel.deleteFavoriteMovie(movie)
        verify(repository).deleteFavoriteMovie(movie)
    }
}
