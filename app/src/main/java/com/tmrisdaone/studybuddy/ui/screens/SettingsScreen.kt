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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.tmrisdaone.studybuddy.ui.theme.TurboGradients
import com.tmrisdaone.studybuddy.ui.viewmodels.SettingsViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel, onNavigateToProviders: () -> Unit = {}) {
    var apiKey by remember { mutableStateOf("") }
    var showKey by remember { mutableStateOf(false) }

    val savedApiKey by viewModel.apiKey.collectAsStateWithLifecycle("")
    val selectedModel by viewModel.selectedModel.collectAsStateWithLifecycle("llama-3.1-8b-instant")
    val isSaving by viewModel.isSaving.collectAsStateWithLifecycle(false)
    val saveResult by viewModel.saveResult.collectAsStateWithLifecycle<String?>(null)
    val providers by viewModel.providers.collectAsStateWithLifecycle(emptyList())
    val activeId by viewModel.activeProviderId.collectAsStateWithLifecycle(null)
    val colors = MaterialTheme.colorScheme
    val activeProvider = providers.firstOrNull { it.id == activeId }

    Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TurboGradients.header)
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                Text("Settings", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = colors.primary)
                Text("Configure your StudyBuddy", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
            }

            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // API Providers entry card
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = colors.surfaceContainer,
                    modifier = Modifier.fillMaxWidth().clickable { onNavigateToProviders() }
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(32.dp).background(TurboGradients.accent, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.VpnKey, contentDescription = null, tint = colors.onPrimary, modifier = Modifier.size(18.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text("API Providers", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = colors.onSurface)
                            Text(
                                activeProvider?.let { "Active: ${it.name} · ${it.defaultModel.ifBlank { "default" }}" }
                                    ?: "Add NVIDIA NIM, Ollama, or custom endpoints",
                                fontSize = 12.sp,
                                color = colors.onSurfaceVariant
                            )
                        }
                        Icon(
                            androidx.compose.material.icons.Icons.Filled.ChevronRight,
                            contentDescription = null,
                            tint = colors.onSurfaceVariant
                        )
                    }
                }

                // API Key card (legacy, edits the active provider's key)
                SettingCard(title = "Active API Key", icon = Icons.Filled.VpnKey) {
                    OutlinedTextField(
                        value = apiKey,
                        onValueChange = { apiKey = it },
                        label = { Text("Enter your Groq API Key") },
                        placeholder = { Text("sk-...") },
                        singleLine = true,
                        visualTransformation = if (showKey) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            if (showKey) "Hide" else "Show",
                            color = colors.primary,
                            fontSize = 13.sp,
                            modifier = Modifier.clickable { showKey = !showKey }
                        )
                        Spacer(Modifier.weight(1f))
                        Button(
                            onClick = { if (apiKey.isNotBlank()) viewModel.saveApiKey(apiKey) },
                            enabled = apiKey.isNotBlank() && !isSaving,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = colors.primary, contentColor = colors.onPrimary)
                        ) {
                            if (isSaving) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = colors.onPrimary, strokeWidth = 2.dp)
                            } else {
                                Text("Save")
                            }
                        }
                    }
                }

                // Model card
                SettingCard(title = "Model", icon = Icons.Filled.CheckCircle) {
                    Text("Current", fontSize = 12.sp, color = colors.onSurfaceVariant)
                    Text(selectedModel, fontWeight = FontWeight.Medium, color = colors.onSurface, fontSize = 15.sp)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Available: llama-3.1-8b-instant, llama-3.1-70b-versatile, mixtral-8x7b-32768, gemma2-9b-it",
                        fontSize = 12.sp,
                        color = colors.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }

                // Status card
                SettingCard(title = "Status", icon = Icons.Filled.CheckCircle) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("API Key", color = colors.onSurface)
                        if (savedApiKey.isNotBlank()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Configured", color = colors.primary, fontSize = 13.sp)
                                Spacer(Modifier.width(6.dp))
                                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = colors.primary, modifier = Modifier.size(18.dp))
                            }
                        } else {
                            Text("Not configured", color = colors.onSurfaceVariant, fontSize = 13.sp)
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Model", color = colors.onSurface)
                        Text(selectedModel, color = colors.onSurfaceVariant, fontSize = 13.sp)
                    }
                }

                saveResult?.let { result ->
                    val isFail = result.contains("Failed")
                    Surface(
                        color = if (isFail) colors.errorContainer else colors.tertiaryContainer,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            result,
                            color = if (isFail) colors.onErrorContainer else colors.onTertiaryContainer,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = colors.surfaceContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(32.dp).background(TurboGradients.accent, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = colors.onPrimary, modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text(title, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = colors.onSurface)
            }
            Spacer(Modifier.height(14.dp))
            content()
        }
    }
}
