package com.it2161.movieviewer.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.it2161.movieviewer.data.datastore.FavoritesDataStore
import com.it2161.movieviewer.data.local.entities.MovieEntity
import com.it2161.movieviewer.data.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class MovieDetailState {
    object Loading : MovieDetailState()
    data class Success(val movie: MovieEntity, val isFavorite: Boolean) : MovieDetailState()
    data class Error(val message: String) : MovieDetailState()
}

class MovieDetailViewModel(
    private val movieRepository: MovieRepository,
    private val favoritesDataStore: FavoritesDataStore
) : ViewModel() {
    
    private val _movieDetailState = MutableStateFlow<MovieDetailState>(MovieDetailState.Loading)
    val movieDetailState: StateFlow<MovieDetailState> = _movieDetailState.asStateFlow()

    private val _isOnline = MutableStateFlow(true)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    fun setOnlineStatus(isOnline: Boolean) {
        _isOnline.value = isOnline
    }

    fun loadMovieDetails(movieId: Int) {
        viewModelScope.launch {
            _movieDetailState.value = MovieDetailState.Loading
            try {
                val result = movieRepository.getMovieDetails(movieId, _isOnline.value)
                result.onSuccess { movie ->
                    val isFavorite = checkIfFavorite(movieId)
                    _movieDetailState.value = MovieDetailState.Success(movie, isFavorite)
                }.onFailure { exception ->
                    _movieDetailState.value = MovieDetailState.Error(
                        exception.message ?: "Failed to load movie details"
                    )
                }
            } catch (e: Exception) {
                _movieDetailState.value = MovieDetailState.Error(
                    e.message ?: "Failed to load movie details"
                )
            }
        }
    }

    private suspend fun checkIfFavorite(movieId: Int): Boolean {
        var isFavorite = false
        favoritesDataStore.favoritesFlow.collect { favorites ->
            isFavorite = favorites.contains(movieId)
        }
        return isFavorite
    }

    fun toggleFavorite(movieId: Int) {
        viewModelScope.launch {
            val currentState = _movieDetailState.value
            if (currentState is MovieDetailState.Success) {
                val isFavorite = currentState.isFavorite
                if (isFavorite) {
                    favoritesDataStore.removeFavorite(movieId)
                } else {
                    favoritesDataStore.addFavorite(movieId)
                }
                _movieDetailState.value = currentState.copy(isFavorite = !isFavorite)
            }
        }
    }
}
