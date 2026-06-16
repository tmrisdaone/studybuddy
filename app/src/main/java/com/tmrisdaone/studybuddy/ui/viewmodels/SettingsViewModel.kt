package com.tmrisdaone.studybuddy.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tmrisdaone.studybuddy.data.repo.StudyBuddyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(private val repo: StudyBuddyRepository) : ViewModel() {
    
    private val _apiKey = MutableStateFlow("")
    val apiKey = _apiKey.asStateFlow()
    
    private val _selectedModel = MutableStateFlow("llama-3.1-8b-instant")
    val selectedModel = _selectedModel.asStateFlow()
    
    private val _isSaving = MutableStateFlow(false)
    val isSaving = _isSaving.asStateFlow()
    
    private val _saveResult = MutableStateFlow<String?>(null)
    val saveResult = _saveResult.asStateFlow()
    
    private val availableModels = listOf(
        "llama-3.1-8b-instant",
        "llama-3.1-70b-versatile",
        "mixtral-8x7b-32768",
        "gemma2-9b-it"
    )
    
    fun getAvailableModels() = availableModels
    
    fun loadApiKey() {
        viewModelScope.launch(Dispatchers.IO) {
            val key = repo.getApiKey()
            _apiKey.value = key ?: ""
        }
    }
    
    fun saveApiKey(key: String) {
        _isSaving.value = true
        _saveResult.value = null
        
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repo.saveApiKey(key)
                _apiKey.value = key
                _saveResult.value = "API key saved successfully"
            } catch (e: Exception) {
                _saveResult.value = "Failed to save: ${e.message}"
            } finally {
                _isSaving.value = false
            }
        }
    }
    
    fun setModel(model: String) {
        _selectedModel.value = model
    }
    
    fun clearResult() {
        _saveResult.value = null
    }
}