package com.it2161.movieviewer.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.it2161.movieviewer.data.local.entities.UserEntity
import com.it2161.movieviewer.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val user: UserEntity) : ProfileState()
    data class Error(val message: String) : ProfileState()
}

class ProfileViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    private val _updateState = MutableStateFlow<String?>(null)
    val updateState: StateFlow<String?> = _updateState.asStateFlow()

    fun loadProfile(userId: String) {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            try {
                val user = userRepository.getUserById(userId)
                if (user != null) {
                    _profileState.value = ProfileState.Success(user)
                } else {
                    _profileState.value = ProfileState.Error("User not found")
                }
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error(e.message ?: "Failed to load profile")
            }
        }
    }

    fun updateProfile(user: UserEntity) {
        viewModelScope.launch {
            try {
                val result = userRepository.updateUser(user)
                result.onSuccess {
                    _updateState.value = "Profile updated successfully"
                    _profileState.value = ProfileState.Success(user)
                }.onFailure { exception ->
                    _updateState.value = exception.message ?: "Failed to update profile"
                }
            } catch (e: Exception) {
                _updateState.value = e.message ?: "Failed to update profile"
            }
        }
    }

    fun clearUpdateState() {
        _updateState.value = null
    }
}
