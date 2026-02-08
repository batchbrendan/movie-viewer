package com.it2161.movieviewer.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.it2161.movieviewer.data.local.entities.MovieEntity
import com.it2161.movieviewer.data.repository.FavoriteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class FavoriteState {
    object Loading : FavoriteState()
    data class Success(val movies: List<MovieEntity>) : FavoriteState()
    object Empty : FavoriteState()
    data class Error(val message: String) : FavoriteState()
}

class FavoriteViewModel(
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {
    
    private val _favoriteState = MutableStateFlow<FavoriteState>(FavoriteState.Loading)
    val favoriteState: StateFlow<FavoriteState> = _favoriteState.asStateFlow()

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            _favoriteState.value = FavoriteState.Loading
            try {
                favoriteRepository.getFavoriteMovies().collect { movies ->
                    _favoriteState.value = if (movies.isEmpty()) {
                        FavoriteState.Empty
                    } else {
                        FavoriteState.Success(movies)
                    }
                }
            } catch (e: Exception) {
                _favoriteState.value = FavoriteState.Error(e.message ?: "Failed to load favorites")
            }
        }
    }
}
