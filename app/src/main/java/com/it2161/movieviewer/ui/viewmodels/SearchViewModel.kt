package com.it2161.movieviewer.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.it2161.movieviewer.data.local.entities.MovieEntity
import com.it2161.movieviewer.data.repository.MovieRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

sealed class SearchState {
    object Idle : SearchState()
    object Loading : SearchState()
    data class Success(val movies: List<MovieEntity>) : SearchState()
    object Empty : SearchState()
    data class Error(val message: String) : SearchState()
}

@OptIn(FlowPreview::class)
class SearchViewModel(
    private val movieRepository: MovieRepository
) : ViewModel() {
    
    private val _searchState = MutableStateFlow<SearchState>(SearchState.Idle)
    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .collect { query ->
                    if (query.isNotBlank()) {
                        searchMovies(query)
                    } else {
                        _searchState.value = SearchState.Idle
                    }
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    private fun searchMovies(query: String) {
        viewModelScope.launch {
            _searchState.value = SearchState.Loading
            try {
                movieRepository.searchMovies(query).collect { result ->
                    result.onSuccess { movies ->
                        _searchState.value = if (movies.isEmpty()) {
                            SearchState.Empty
                        } else {
                            SearchState.Success(movies)
                        }
                    }.onFailure { exception ->
                        _searchState.value = SearchState.Error(
                            exception.message ?: "Search failed"
                        )
                    }
                }
            } catch (e: Exception) {
                _searchState.value = SearchState.Error(e.message ?: "Search failed")
            }
        }
    }
}
