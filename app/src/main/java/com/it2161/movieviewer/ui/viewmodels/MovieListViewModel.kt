package com.it2161.movieviewer.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.it2161.movieviewer.data.local.entities.MovieEntity
import com.it2161.movieviewer.data.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class MovieListState {
    object Loading : MovieListState()
    data class Success(val movies: List<MovieEntity>) : MovieListState()
    data class Error(val message: String) : MovieListState()
}

class MovieListViewModel(
    private val movieRepository: MovieRepository
) : ViewModel() {
    
    private val _movieListState = MutableStateFlow<MovieListState>(MovieListState.Loading)
    val movieListState: StateFlow<MovieListState> = _movieListState.asStateFlow()

    private val _selectedCategory = MutableStateFlow("popular")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _isOnline = MutableStateFlow(true)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    fun setOnlineStatus(isOnline: Boolean) {
        _isOnline.value = isOnline
    }

    fun loadMovies(category: String) {
        _selectedCategory.value = category
        viewModelScope.launch {
            _movieListState.value = MovieListState.Loading
            try {
                when (category) {
                    "popular" -> movieRepository.getPopularMovies(_isOnline.value)
                    "top_rated" -> movieRepository.getTopRatedMovies(_isOnline.value)
                    "now_playing" -> movieRepository.getNowPlayingMovies(_isOnline.value)
                    "upcoming" -> movieRepository.getUpcomingMovies(_isOnline.value)
                    else -> movieRepository.getPopularMovies(_isOnline.value)
                }.collect { result ->
                    result.onSuccess { movies ->
                        _movieListState.value = MovieListState.Success(movies)
                    }.onFailure { exception ->
                        _movieListState.value = MovieListState.Error(
                            exception.message ?: "Failed to load movies"
                        )
                    }
                }
            } catch (e: Exception) {
                _movieListState.value = MovieListState.Error(e.message ?: "Failed to load movies")
            }
        }
    }

    fun refresh() {
        loadMovies(_selectedCategory.value)
    }
}
