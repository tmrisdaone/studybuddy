package com.tmrisdaone.studybuddy.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tmrisdaone.studybuddy.data.repo.StudyBuddyRepository
import com.tmrisdaone.studybuddy.domain.ChatMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(private val repo: StudyBuddyRepository) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    /** Name of the active provider + model, shown in the chat header selector. */
    private val _activeLabel = MutableStateFlow("")
    val activeLabel = _activeLabel.asStateFlow()

    var currentSessionId: Long = 0

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repo.initProviders()
            refreshActiveLabel()
        }
    }

    fun initSession(sessionId: Long) {
        currentSessionId = sessionId
    }

    private fun refreshActiveLabel() {
        val p = repo.activeProvider()
        _activeLabel.value = if (p == null) {
            "No provider"
        } else {
            val model = p.defaultModel.ifBlank { "default" }
            "${p.name} · $model"
        }
    }

    /**
     * Sends [userMessage]. If [model] is blank, the active provider's
     * default model is used. Streams the assistant response token-by-token
     * when the active provider supports SSE; falls back to a single-shot
     * request otherwise.
     */
    fun sendMessage(userMessage: String, systemPrompt: String, model: String) {
        if (userMessage.isBlank() || _isLoading.value) return

        val userMsg = ChatMessage(
            id = System.currentTimeMillis(),
            sessionId = currentSessionId,
            role = "user",
            content = userMessage,
            createdAt = Clock.System.now()
        )
        _messages.value = _messages.value + userMsg
        _isLoading.value = true
        _error.value = null

        val provider = repo.activeProvider()
        if (provider == null) {
            _error.value = "No active API provider configured. Open Settings to add one."
            _isLoading.value = false
            return
        }

        val resolvedModel = model.ifBlank { provider.defaultModel.ifBlank { "default" } }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                streamResponse(systemPrompt, userMessage, resolvedModel)
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to send message"
                // Non-streaming fallback so a missing SSE surface still works.
                try {
                    val response = repo.chat(currentSessionId, userMessage, systemPrompt, resolvedModel)
                    appendAssistant(response)
                } catch (e2: Exception) {
                    _error.value = e2.message ?: _error.value
                }
            } finally {
                _isLoading.value = false
                refreshActiveLabel()
            }
        }
    }

    private suspend fun streamResponse(system: String, user: String, model: String) {
        val assistantId = System.currentTimeMillis() + 1
        _messages.value = _messages.value + ChatMessage(
            id = assistantId,
            sessionId = currentSessionId,
            role = "assistant",
            content = "",
            createdAt = Clock.System.now()
        )
        val builder = StringBuilder()
        repo.chatStream(system, user, model).collect { delta ->
            builder.append(delta)
            updateLastAssistant(builder.toString())
        }
        if (builder.isEmpty()) {
            throw RuntimeException("Empty stream from provider")
        }
    }

    private fun updateLastAssistant(content: String) {
        val list = _messages.value.toMutableList()
        val idx = list.indexOfLast { it.role == "assistant" }
        if (idx >= 0) {
            list[idx] = list[idx].copy(content = content)
            _messages.value = list
        }
    }

    private fun appendAssistant(content: String) {
        _messages.value = _messages.value + ChatMessage(
            id = System.currentTimeMillis() + 1,
            sessionId = currentSessionId,
            role = "assistant",
            content = content,
            createdAt = Clock.System.now()
        )
    }

    fun clearError() {
        _error.value = null
    }
}
