package com.example.a1155229161_assignment2

// LoginViewModel.kt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Initial)
    val uiState = _uiState.asStateFlow()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
                NetworkClient.login(username, password)
                    .onSuccess { response ->
                        _uiState.value = LoginUiState.Success(response.message, response.username, response.email)
                    }
                    .onFailure { exception ->
                        _uiState.value = LoginUiState.Error("Login failed: ${exception.message}")
                    }
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error("Login failed: ${e.message}")
            }
        }
    }

    fun register(username: String, password: String, email: String? = null) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
                NetworkClient.register(username, password, email)
                    .onSuccess { response ->
                        _uiState.value = LoginUiState.Success(response.message, response.username, response.email)
                    }
                    .onFailure { exception ->
                        _uiState.value = LoginUiState.Error("Registration failed: ${exception.message}")
                    }
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error("Registration failed: ${e.message}")
            }
        }
    }
}

sealed class LoginUiState {
    data object Initial : LoginUiState()
    data object Loading : LoginUiState()
    data class Success(
        val message: String,
        val username: String,
        val email: String?
    ) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}