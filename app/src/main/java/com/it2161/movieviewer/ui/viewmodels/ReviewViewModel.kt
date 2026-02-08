package com.it2161.movieviewer.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.it2161.movieviewer.data.local.entities.ReviewEntity
import com.it2161.movieviewer.data.repository.ReviewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ReviewState {
    object Loading : ReviewState()
    data class Success(val reviews: List<ReviewEntity>) : ReviewState()
    object Empty : ReviewState()
    data class Error(val message: String) : ReviewState()
}

class ReviewViewModel(
    private val reviewRepository: ReviewRepository
) : ViewModel() {
    
    private val _reviewState = MutableStateFlow<ReviewState>(ReviewState.Loading)
    val reviewState: StateFlow<ReviewState> = _reviewState.asStateFlow()

    private val _isOnline = MutableStateFlow(true)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    fun setOnlineStatus(isOnline: Boolean) {
        _isOnline.value = isOnline
    }

    fun loadReviews(movieId: Int) {
        viewModelScope.launch {
            _reviewState.value = ReviewState.Loading
            try {
                reviewRepository.getMovieReviews(movieId, _isOnline.value).collect { result ->
                    result.onSuccess { reviews ->
                        _reviewState.value = if (reviews.isEmpty()) {
                            ReviewState.Empty
                        } else {
                            ReviewState.Success(reviews)
                        }
                    }.onFailure { exception ->
                        _reviewState.value = ReviewState.Error(
                            exception.message ?: "Failed to load reviews"
                        )
                    }
                }
            } catch (e: Exception) {
                _reviewState.value = ReviewState.Error(e.message ?: "Failed to load reviews")
            }
        }
    }
}
