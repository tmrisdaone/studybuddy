package com.tmrisdaone.studybuddy.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tmrisdaone.studybuddy.data.local.StudyBuddyDatabase
import com.tmrisdaone.studybuddy.data.repo.StudyBuddyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers

class ChatViewModel(private val db: StudyBuddyDatabase, private val context: Context) : ViewModel() {
    private val repository: StudyBuddyRepository by lazy { StudyBuddyRepository(db, context) }

    private val _messages = MutableStateFlow<List<com.tmrisdaone.studybuddy.domain.ChatMessage>>(emptyList())
    val messages: StateFlow<List<com.tmrisdaone.studybuddy.domain.ChatMessage>> = _messages.asStateFlow()

    private val _sessionId = MutableStateFlow<Long>(0)
    val sessionId: StateFlow<Long> = _sessionId.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _currentContext = MutableStateFlow("")
    val currentContext: StateFlow<String> = _currentContext.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val id = repository.createSession("chat", "New Chat", "text")
            _sessionId.value = id
        }
    }

    fun send(userMsg: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.value = true
                val reply = repository.chat(
                    _sessionId.value,
                    userMsg,
                    "You are Delsin, chill, sarcastic, helpful study assistant. Keep it short.",
                    "llama-3.1-8b-instant"
                )
                _messages.value += com.tmrisdaone.studybuddy.domain.ChatMessage(role = "user", content = userMsg)
                _messages.value += com.tmrisdaone.studybuddy.domain.ChatMessage(role = "assistant", content = reply)
                _currentContext.value += "\n\n$reply"
            } catch (e: Exception) {
                _messages.value += com.tmrisdaone.studybuddy.domain.ChatMessage(
                    role = "assistant",
                    content = "Error: ${e.message}"
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun generateQuiz() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.value = true
                val contextText = _currentContext.value.ifBlank { "General knowledge" }
                val quizId = repository.generateQuiz(_sessionId.value, contextText, "Generated Quiz")
                _messages.value += com.tmrisdaone.studybuddy.domain.ChatMessage(
                    role = "assistant",
                    content = "Quiz generated (ID: $quizId)"
                )
            } catch (e: Exception) {
                _messages.value += com.tmrisdaone.studybuddy.domain.ChatMessage(
                    role = "assistant",
                    content = "Quiz failed: ${e.message}"
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun generateFlashcards() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.value = true
                val contextText = _currentContext.value.ifBlank { "General study material" }
                val deckId = repository.generateFlashcards(_sessionId.value, contextText, "Flashcards")
                _messages.value += com.tmrisdaone.studybuddy.domain.ChatMessage(
                    role = "assistant",
                    content = "Flashcards generated (deck: $deckId)"
                )
            } catch (e: Exception) {
                _messages.value += com.tmrisdaone.studybuddy.domain.ChatMessage(
                    role = "assistant",
                    content = "Flashcards failed: ${e.message}"
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun summarize() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.value = true
                val contextText = _currentContext.value.ifBlank { "No context yet" }
                val summary = repository.summarizeText(contextText)
                _messages.value += com.tmrisdaone.studybuddy.domain.ChatMessage(
                    role = "assistant",
                    content = "Summary:\n\n$summary"
                )
            } catch (e: Exception) {
                _messages.value += com.tmrisdaone.studybuddy.domain.ChatMessage(
                    role = "assistant",
                    content = "Summarization failed: ${e.message}"
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    companion object {
        fun factory(db: StudyBuddyDatabase, ctx: Context) = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = ChatViewModel(db, ctx) as T
        }
    }
}
