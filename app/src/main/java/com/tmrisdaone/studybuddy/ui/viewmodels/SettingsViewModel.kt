package com.tmrisdaone.studybuddy.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tmrisdaone.studybuddy.data.repo.StudyBuddyRepository
import com.tmrisdaone.studybuddy.domain.ApiProvider
import com.tmrisdaone.studybuddy.domain.ModelInfo
import com.tmrisdaone.studybuddy.domain.NvidiaNim
import com.tmrisdaone.studybuddy.domain.ProviderType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val repo: StudyBuddyRepository) : ViewModel() {

    val providers: StateFlow<List<ApiProvider>> = repo.providers
    val activeProviderId: StateFlow<String?> = repo.activeProviderId

    private val _activeModel = MutableStateFlow("")
    val activeModel: StateFlow<String> = _activeModel.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _saveResult = MutableStateFlow<String?>(null)
    val saveResult: StateFlow<String?> = _saveResult.asStateFlow()

    private val _models = MutableStateFlow<List<ModelInfo>>(emptyList())
    val models: StateFlow<List<ModelInfo>> = _models.asStateFlow()

    private val _isFetchingModels = MutableStateFlow(false)
    val isFetchingModels: StateFlow<Boolean> = _isFetchingModels.asStateFlow()

    private val _connectionStatus = MutableStateFlow<String?>(null)
    val connectionStatus: StateFlow<String?> = _connectionStatus.asStateFlow()

    // Legacy compat surfaces (SettingsScreen reads apiKey + selectedModel).
    val apiKey: StateFlow<String> = MutableStateFlow("")
    val selectedModel: StateFlow<String> = MutableStateFlow("llama-3.1-8b-instant")

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repo.initProviders()
            refreshLegacyState()
        }
    }

    private fun refreshLegacyState() {
        val active = repo.activeProvider()
        (apiKey as MutableStateFlow).value = active?.apiKey.orEmpty()
        (selectedModel as MutableStateFlow).value = active?.defaultModel?.ifBlank { NvidiaNim.SUGGESTED_MODELS.first() } ?: "llama-3.1-8b-instant"
        _activeModel.value = (selectedModel as MutableStateFlow).value
    }

    fun setActiveProvider(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.setActiveProvider(id)
            refreshLegacyState()
            repo.activeProvider()?.let { refreshModels(it) }
        }
    }

    fun setActiveModel(model: String) {
        _activeModel.value = model
        repo.activeProvider()?.let { p ->
            viewModelScope.launch(Dispatchers.IO) {
                repo.updateProvider(p.id) { it.copy(defaultModel = model) }
                refreshLegacyState()
            }
        }
    }

    fun saveProvider(
        id: String?,
        name: String,
        type: ProviderType,
        baseUrl: String,
        apiKey: String,
        defaultModel: String,
        headers: Map<String, String> = emptyMap()
    ) {
        _isSaving.value = true
        _saveResult.value = null
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val provider = ApiProvider(
                    id = id?.ifBlank { UUID.randomUUID().toString() } ?: UUID.randomUUID().toString(),
                    name = name.ifBlank { "Untitled" },
                    type = type,
                    baseUrl = baseUrl,
                    apiKey = apiKey,
                    defaultModel = defaultModel,
                    headers = headers
                )
                if (repo.providers.value.none { it.id == provider.id }) {
                    repo.addProvider(provider)
                } else {
                    repo.updateProvider(provider.id) { provider }
                }
                repo.setActiveProvider(provider.id)
                _saveResult.value = "Saved ${provider.name}"
                refreshLegacyState()
                refreshModels(provider)
            } catch (e: Exception) {
                _saveResult.value = "Failed to save: ${e.message}"
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun deleteProvider(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repo.deleteProvider(id)
                refreshLegacyState()
            } catch (_: Exception) { }
        }
    }

    fun refreshModels(provider: ApiProvider) {
        _isFetchingModels.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val list = repo.listModels(provider)
                if (list.isEmpty()) {
                    _models.value = when (provider.type) {
                        ProviderType.NVIDIA_NIM -> NvidiaNim.SUGGESTED_MODELS.map { ModelInfo(it, it, provider.id) }
                        else -> listOf(ModelInfo(provider.defaultModel, provider.defaultModel, provider.id))
                    }
                } else {
                    _models.value = list
                }
            } finally {
                _isFetchingModels.value = false
            }
        }
    }

    fun testConnection(provider: ApiProvider) {
        _connectionStatus.value = "Testing…"
        viewModelScope.launch(Dispatchers.IO) {
            val ok = repo.testConnection(provider)
            _connectionStatus.value = if (ok) "Connected to ${provider.name}" else "Could not reach ${provider.name}"
        }
    }

    fun clearSaveResult() { _saveResult.value = null }
    fun clearConnectionStatus() { _connectionStatus.value = null }
}
