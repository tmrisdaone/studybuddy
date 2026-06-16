package com.tmrisdaone.studybuddy.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tmrisdaone.studybuddy.data.repo.StudyBuddyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ScannerViewModel(private val repo: StudyBuddyRepository) : ViewModel() {
    
    private val _scannedText = MutableStateFlow("")
    val scannedText = _scannedText.asStateFlow()
    
    private val _isScanning = MutableStateFlow(false)
    val isScanning = _isScanning.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()
    
    fun scanFromCamera() {
        _isScanning.value = true
        _error.value = null
        
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Placeholder - would integrate with CameraX/ML Kit
                // For now, simulate a scan
                Thread.sleep(1000)
                _scannedText.value = "Scanned text would appear here. Camera integration pending."
            } catch (e: Exception) {
                _error.value = e.message ?: "Scan failed"
            } finally {
                _isScanning.value = false
            }
        }
    }
    
    fun scanFromGallery() {
        _isScanning.value = true
        _error.value = null
        
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Placeholder - would pick from gallery
                Thread.sleep(1000)
                _scannedText.value = "Gallery scan text would appear here. Integration pending."
            } catch (e: Exception) {
                _error.value = e.message ?: "Scan failed"
            } finally {
                _isScanning.value = false
            }
        }
    }
    
    fun clearText() {
        _scannedText.value = ""
    }
    
    fun clearError() {
        _error.value = null
    }
}