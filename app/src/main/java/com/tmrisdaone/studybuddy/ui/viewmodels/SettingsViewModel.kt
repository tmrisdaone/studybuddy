package com.tmrisdaone.studybuddy.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tmrisdaone.studybuddy.data.local.StudyBuddyDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(private val db: StudyBuddyDatabase) : ViewModel() {
    private val _groqKey = MutableStateFlow("")
    val groqKey: StateFlow<String> = _groqKey.asStateFlow()

    private val _sttModel = MutableStateFlow("whisper-large-v3-turbo")
    val sttModel: StateFlow<String> = _sttModel.asStateFlow()

    private val _llmModel = MutableStateFlow("llama-3.1-8b-instant")
    val llmModel: StateFlow<String> = _llmModel.asStateFlow()

    init {
        viewModelScope.launch {
            _groqKey.value = db.preferenceDao().get("groq_api_key") ?: ""
            _sttModel.value = db.preferenceDao().get("stt_model") ?: "whisper-large-v3-turbo"
            _llmModel.value = db.preferenceDao().get("llm_model") ?: "llama-3.1-8b-instant"
        }
    }

    fun setGroqKey(key: String) {
        _groqKey.value = key
        viewModelScope.launch { db.preferenceDao().put(com.tmrisdaone.studybuddy.data.local.PreferenceEntity("groq_api_key", key)) }
    }

    fun setSttModel(model: String) {
        _sttModel.value = model
        viewModelScope.launch { db.preferenceDao().put(com.tmrisdaone.studybuddy.data.local.PreferenceEntity("stt_model", model)) }
    }

    fun setLlmModel(model: String) {
        _llmModel.value = model
        viewModelScope.launch { db.preferenceDao().put(com.tmrisdaone.studybuddy.data.local.PreferenceEntity("llm_model", model)) }
    }

    companion object {
        fun factory(db: StudyBuddyDatabase) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = SettingsViewModel(db) as T
        }
    }
}
