package com.tmrisdaone.studybuddy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tmrisdaone.studybuddy.domain.ApiProvider
import com.tmrisdaone.studybuddy.domain.NvidiaNim
import com.tmrisdaone.studybuddy.domain.ProviderType
import com.tmrisdaone.studybuddy.ui.theme.TurboGradients
import com.tmrisdaone.studybuddy.ui.viewmodels.SettingsViewModel

@Composable
fun ProviderSettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit
) {
    val providers by viewModel.providers.collectAsStateWithLifecycle(emptyList())
    val activeId by viewModel.activeProviderId.collectAsStateWithLifecycle(null)
    val connectionStatus by viewModel.connectionStatus.collectAsStateWithLifecycle()
    val colors = MaterialTheme.colorScheme
    var editing by remember { mutableStateOf<ApiProvider?>(null) }
    var showEditor by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        Column(Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().background(TurboGradients.header).padding(horizontal = 12.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = colors.onSurface)
                }
                Column(Modifier.weight(1f).padding(start = 8.dp)) {
                    Text("API Providers", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = colors.primary)
                    Text("Add NVIDIA NIM, Ollama, or custom endpoints", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
                }
                IconButton(onClick = { editing = null; showEditor = true }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add provider", tint = colors.primary)
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(providers, key = { it.id }) { provider ->
                    ProviderRow(
                        provider = provider,
                        isActive = provider.id == activeId,
                        onActivate = { viewModel.setActiveProvider(provider.id) },
                        onEdit = { editing = provider; showEditor = true },
                        onDelete = { viewModel.deleteProvider(provider.id) },
                        onTest = { viewModel.testConnection(provider) }
                    )
                }
                connectionStatus?.let { status ->
                    item {
                        Surface(color = colors.surfaceContainer, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                            Text(status, color = colors.onSurfaceVariant, fontSize = 13.sp, modifier = Modifier.padding(12.dp))
                        }
                    }
                }
            }
        }
    }

    if (showEditor) {
        ProviderEditorDialog(
            existing = editing,
            onDismiss = { showEditor = false },
            onSave = { name, type, baseUrl, key, model, headers ->
                viewModel.saveProvider(editing?.id, name, type, baseUrl, key, model, headers)
                showEditor = false
            },
            onFetchModels = { provider ->
                viewModel.refreshModels(provider)
            },
            viewModel = viewModel
        )
    }
}

@Composable
private fun ProviderRow(
    provider: ApiProvider,
    isActive: Boolean,
    onActivate: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onTest: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = colors.surfaceContainer,
        modifier = Modifier.fillMaxWidth().clickable { onActivate() }
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(40.dp).background(TurboGradients.accent, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.VpnKey, contentDescription = null, tint = colors.onPrimary, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(provider.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = colors.onSurface)
                    Spacer(Modifier.width(8.dp))
                    Text(provider.type.name, fontSize = 11.sp, color = colors.onSurfaceVariant)
                }
                Text(provider.baseUrl, fontSize = 12.sp, color = colors.onSurfaceVariant, maxLines = 1)
                Text(
                    if (provider.apiKey.isNotBlank()) "Key configured · model: ${provider.defaultModel.ifBlank { "none" }}"
                    else "No key · model: ${provider.defaultModel.ifBlank { "none" }}",
                    fontSize = 12.sp,
                    color = colors.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
            if (isActive) {
                Icon(Icons.Filled.CheckCircle, contentDescription = "Active", tint = colors.primary, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(4.dp))
            }
            IconButton(onClick = onTest) { Icon(Icons.Filled.CheckCircle, contentDescription = "Test", tint = colors.secondary) }
            IconButton(onClick = onEdit) { Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = colors.onSurfaceVariant) }
            IconButton(onClick = onDelete) { Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = colors.error) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProviderEditorDialog(
    existing: ApiProvider?,
    onDismiss: () -> Unit,
    onSave: (String, ProviderType, String, String, String, Map<String, String>) -> Unit,
    onFetchModels: (ApiProvider) -> Unit,
    viewModel: SettingsViewModel
) {
    var name by remember { mutableStateOf(existing?.name.orEmpty()) }
    var type by remember { mutableStateOf(existing?.type ?: ProviderType.OPENAI_COMPATIBLE) }
    var baseUrl by remember { mutableStateOf(existing?.baseUrl.orEmpty()) }
    var apiKey by remember { mutableStateOf(existing?.apiKey.orEmpty()) }
    var showKey by remember { mutableStateOf(false) }
    var defaultModel by remember { mutableStateOf(existing?.defaultModel.orEmpty()) }
    val models by viewModel.models.collectAsStateWithLifecycle(emptyList())
    val isFetching by viewModel.isFetchingModels.collectAsStateWithLifecycle(false)
    val colors = MaterialTheme.colorScheme

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = { onSave(name, type, baseUrl, apiKey, defaultModel, emptyMap()) }) {
                Text(if (existing == null) "Add Provider" else "Save")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        title = { Text(if (existing == null) "New API Provider" else "Edit Provider", color = colors.onSurface) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(name, { name = it }, label = { Text("Display name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Text("Provider type", fontSize = 12.sp, color = colors.onSurfaceVariant)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ProviderType.values().forEach { t ->
                        FilterChip(
                            selected = type == t,
                            onClick = {
                                type = t
                                if (t == ProviderType.NVIDIA_NIM && baseUrl.isBlank()) {
                                    baseUrl = NvidiaNim.DEFAULT_BASE_URL
                                    if (defaultModel.isBlank()) defaultModel = NvidiaNim.SUGGESTED_MODELS.first()
                                }
                            },
                            label = { Text(t.name.replace('_', ' '), fontSize = 11.sp) }
                        )
                    }
                }
                OutlinedTextField(baseUrl, { baseUrl = it }, label = { Text("Base URL") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(
                    apiKey, { apiKey = it },
                    label = { Text("API key") }, singleLine = true,
                    visualTransformation = if (showKey) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        TextButton(onClick = { showKey = !showKey }) { Text(if (showKey) "Hide" else "Show") }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                ExposedDropdownMenuBox(expanded = false, onExpandedChange = {}) {
                    OutlinedTextField(
                        defaultModel, { defaultModel = it },
                        label = { Text("Default model") }, singleLine = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    DropdownMenu(expanded = false, onDismissRequest = {}) {
                        if (isFetching) {
                            DropdownMenuItem(text = { Text("Fetching models…") }, onClick = {})
                        } else {
                            (models.map { it.id } + NvidiaNim.SUGGESTED_MODELS).distinct().forEach { id ->
                                DropdownMenuItem(text = { Text(id) }, onClick = { defaultModel = id })
                            }
                        }
                    }
                }
                TextButton(onClick = {
                    if (name.isNotBlank() && baseUrl.isNotBlank()) {
                        onSave(name, type, baseUrl, apiKey, defaultModel, emptyMap())
                        onFetchModels(ApiProvider("preview", name, type, baseUrl, apiKey, defaultModel))
                    }
                }) { Text("Save & fetch models") }
                if (isFetching) Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp, color = colors.primary)
                    Spacer(Modifier.width(8.dp))
                    Text("Querying /v1/models…", fontSize = 12.sp)
                }
            }
        }
    )
}
