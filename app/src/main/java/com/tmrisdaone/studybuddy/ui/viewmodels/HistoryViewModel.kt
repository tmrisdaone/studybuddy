package com.tmrisdaone.studybuddy.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tmrisdaone.studybuddy.data.repo.StudyBuddyRepository
import com.tmrisdaone.studybuddy.domain.StudySession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(private val repo: StudyBuddyRepository) : ViewModel() {
    
    private val _sessions = MutableStateFlow<List<StudySession>>(emptyList())
    val sessions = _sessions.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    
    init {
        loadSessions()
    }
    
    private fun loadSessions() {
        _isLoading.value = true
        viewModelScope.launch {
            repo.sessions.collect { list ->
                _sessions.value = list
                _isLoading.value = false
            }
        }
    }
    
    fun refresh() {
        loadSessions()
    }
}