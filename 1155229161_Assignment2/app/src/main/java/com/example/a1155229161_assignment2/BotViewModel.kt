package com.example.a1155229161_assignment2

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class Bot(
    val id: String,
    val name: String,
    val prompt: String,
    val lastMessage: String? = null
)

class BotViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<BotUiState>(BotUiState.Initial)
    val uiState: StateFlow<BotUiState> = _uiState

    private val _bots = MutableStateFlow<List<Bot>>(emptyList())
    val bots: StateFlow<List<Bot>> = _bots
    fun createChatbot(username:String, name: String, prompt: String) {
        viewModelScope.launch {
            _uiState.value = BotUiState.Loading
            try {
                val result = NetworkClient.createChatbot(username, name, prompt)
                result.fold(
                    onSuccess = { response ->
                        _uiState.value = BotUiState.Success(response)
                    },
                    onFailure = { exception ->
                        _uiState.value = BotUiState.Error(exception.message ?: "Unknown error")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = BotUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
    fun fetchUserBots(username: String) {
        viewModelScope.launch {
            try {
                val result = NetworkClient.getUserBots(username)
                result.onSuccess { botListResponse ->
                    _bots.value = botListResponse.bots.map { botResponse ->
                        Bot(
                            id = botResponse.id,
                            name = botResponse.name,
                            prompt = botResponse.prompt,
                            lastMessage = botResponse.last_message?.content?.take(10)
                        )
                    }
                }.onFailure { exception ->
                    _uiState.value = BotUiState.Error("Failed to fetch bots: ${exception.message}")
                }
            } catch (e: Exception) {
                _uiState.value = BotUiState.Error("Failed to fetch bots: ${e.message}")
            }
        }
    }
}

sealed class BotUiState {
    data object Initial : BotUiState()
    data object Loading : BotUiState()
    data class Success(val chatbot: ChatbotResponse) : BotUiState()
    data class Error(val message: String) : BotUiState()
}