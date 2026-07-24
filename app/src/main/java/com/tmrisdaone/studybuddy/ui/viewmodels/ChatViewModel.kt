package com.tmrisdaone.studybuddy.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tmrisdaone.studybuddy.data.repo.StudyBuddyRepository
import com.tmrisdaone.studybuddy.domain.ChatMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(private val repo: StudyBuddyRepository) : ViewModel() {
    
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages = _messages.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()
    
    var currentSessionId: Long = 0
    
    fun initSession(sessionId: Long) {
        currentSessionId = sessionId
        loadMessages()
    }
    
    private fun loadMessages() {
        viewModelScope.launch {
            repo.flashcards(currentSessionId).collect { flashcards ->
                // We need a different approach - ChatMessageEntity doesn't have a Flow in DAO
                // For now, we'll handle messages through the chat function
            }
        }
    }
    
    fun sendMessage(userMessage: String, systemPrompt: String, model: String) {
        if (userMessage.isBlank() || _isLoading.value) return
        
        // Add user message immediately
        val userMsg = ChatMessage(
            id = System.currentTimeMillis(),
            sessionId = currentSessionId,
            role = "user",
            content = userMessage,
            createdAt = kotlinx.datetime.Clock.System.now()
        )
        _messages.value = _messages.value + userMsg
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repo.chat(currentSessionId, userMessage, systemPrompt, model)
                
                val assistantMsg = ChatMessage(
                    id = System.currentTimeMillis() + 1,
                    sessionId = currentSessionId,
                    role = "assistant",
                    content = response,
                    createdAt = kotlinx.datetime.Clock.System.now()
                )
                
                _messages.value = _messages.value + assistantMsg
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to send message"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clearError() {
        _error.value = null
    }
}