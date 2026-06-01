package com.tmrisdaone.studybuddy.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tmrisdaone.studybuddy.data.local.StudyBuddyDatabase
import com.tmrisdaone.studybuddy.data.remote.GroqClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class ChatViewModel(private val db: StudyBuddyDatabase) : ViewModel() {
    private val _messages = MutableStateFlow<List<com.tmrisdaone.studybuddy.domain.ChatMessage>>(emptyList())
    val messages: StateFlow<List<com.tmrisdaone.studybuddy.domain.ChatMessage>> = _messages.asStateFlow()

    private val _sessionId = MutableStateFlow<Long>(0)
    val sessionId: StateFlow<Long> = _sessionId.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val id = db.studySessionDao().insert(
                com.tmrisdaone.studybuddy.data.local.StudySessionEntity(
                    type = "chat",
                    title = "New Chat",
                    inputType = "text",
                    createdAt = Clock.System.now()
                )
            )
            _sessionId.value = id
        }
    }

    fun send(userMsg: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val apiKey = db.preferenceDao().get("groq_api_key", "") ?: return@launch
            val model = db.preferenceDao().get("llm_model", "llama-3.1-8b-instant") ?: "llama-3.1-8b-instant"
            val client = GroqClient(apiKey)
            val system = "You are Delsin, chill, sarcastic, helpful study assistant. Keep it short."
            try {
                val reply = client.chat(system, userMsg, model)
                _messages.value += com.tmrisdaone.studybuddy.domain.ChatMessage(role = "user", content = userMsg)
                _messages.value += com.tmrisdaone.studybuddy.domain.ChatMessage(role = "assistant", content = reply)
            } catch (e: Exception) {
                _messages.value += com.tmrisdaone.studybuddy.domain.ChatMessage(role = "assistant", content = "Error: ${e.message}")
            }
        }
    }

    fun generateQuiz(context: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val apiKey = db.preferenceDao().get("groq_api_key", "") ?: return@launch
            val client = GroqClient(apiKey)
            try {
                val quizJson = client.generateQuiz(context)
                // parsed + stored — stub here, full in deliverable 3
                _messages.value += com.tmrisdaone.studybuddy.domain.ChatMessage(role = "assistant", content = "Quiz generated: $quizJson")
            } catch (e: Exception) {
                _messages.value += com.tmrisdaone.studybuddy.domain.ChatMessage(role = "assistant", content = "Quiz failed: ${e.message}")
            }
        }
    }
}
