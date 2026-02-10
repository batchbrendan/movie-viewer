package com.it2161.movieviewer.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.it2161.movieviewer.data.datastore.FavoritesDataStore
import com.it2161.movieviewer.data.local.entities.UserEntity
import com.it2161.movieviewer.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: UserEntity) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(
    private val userRepository: UserRepository,
    private val favoritesDataStore: FavoritesDataStore
) : ViewModel() {
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()

    init {
        viewModelScope.launch {
            favoritesDataStore.currentUserIdFlow.collect { userId ->
                _currentUserId.value = userId
            }
        }
    }

    fun login(userId: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = userRepository.login(userId, password)
            result.onSuccess { user ->
                favoritesDataStore.setCurrentUserId(user.userId)
                _authState.value = AuthState.Success(user)
            }.onFailure { exception ->
                _authState.value = AuthState.Error(exception.message ?: "Login failed")
            }
        }
    }

    fun register(user: UserEntity) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = userRepository.registerUser(user)
            result.onSuccess {
                favoritesDataStore.setCurrentUserId(user.userId)
                _authState.value = AuthState.Success(user)
            }.onFailure { exception ->
                _authState.value = AuthState.Error(exception.message ?: "Registration failed")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            favoritesDataStore.clearCurrentUserId()
            _currentUserId.value = null
            _authState.value = AuthState.Idle
        }
    }

    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }
}
