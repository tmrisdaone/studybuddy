package com.tmrisdaone.studybuddy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tmrisdaone.studybuddy.ui.viewmodels.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateToChat: () -> Unit
) {
    var apiKey by remember { mutableStateOf("") }
    
    val savedApiKey by viewModel.apiKey.collectAsStateWithLifecycle("")
    val selectedModel by viewModel.selectedModel.collectAsStateWithLifecycle("llama-3.1-8b-instant")
    val isSaving by viewModel.isSaving.collectAsStateWithLifecycle(false)
    val saveResult by viewModel.saveResult.collectAsStateWithLifecycle<String?>(null)
    val colors = MaterialTheme.colorScheme
    
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Settings", fontWeight = FontWeight.Bold) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.surfaceContainer),
            navigationIcon = {
                IconButton(onClick = onNavigateToChat) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        )
        
        androidx.compose.foundation.layout.Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // API Key Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = colors.surfaceContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Groq API Key", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = colors.onSurface)
                    androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.foundation.layout.height(12.dp))
                    
                    TextField(
                        value = apiKey,
                        onValueChange = { apiKey = it },
                        label = { Text("Enter your Groq API Key") },
                        placeholder = { Text("sk-...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        isError = apiKey.isNotBlank() && !apiKey.startsWith("sk-"),
                        colors = androidx.compose.material3.TextFieldDefaults.textFieldColors(
                            containerColor = colors.surface
                        )
                    )
                    
                    androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.foundation.layout.height(12.dp))
                    
                    Button(
                        onClick = {
                            if (apiKey.isNotBlank()) {
                                viewModel.saveApiKey(apiKey)
                            }
                        },
                        enabled = apiKey.isNotBlank() && !isSaving,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = colors.onPrimary
                            )
                        } else {
                            Text("Save API Key")
                        }
                    }
                }
            }
            
            // Model Selection - simple text display
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = colors.surfaceContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Model", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = colors.onSurface)
                    androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.foundation.layout.height(8.dp))
                    Text("Current: $selectedModel", color = colors.onSurfaceVariant)
                    androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.foundation.layout.height(4.dp))
                    Text("Available: llama-3.1-8b-instant, llama-3.1-70b-versatile, mixtral-8x7b-32768, gemma2-9b-it",
                         color = colors.onSurfaceVariant.copy(alpha = 0.7f), fontSize = 12.sp)
                }
            }
            
            // Status Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = colors.surfaceContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Status", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = colors.onSurface)
                    androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.foundation.layout.height(12.dp))
                    
                    if (savedApiKey.isNotBlank()) {
                        androidx.compose.foundation.layout.Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("API Key: Configured", color = colors.onSurface)
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "Configured",
                                tint = colors.primary
                            )
                        }
                    } else {
                        Text("API Key: Not configured", color = colors.onSurfaceVariant)
                    }
                    
                    androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.foundation.layout.height(8.dp))
                    
                    androidx.compose.foundation.layout.Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Model: $selectedModel", color = colors.onSurface)
                    }
                }
            }
            
            // Save Result
            saveResult?.let { result ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (result.contains("Failed")) 
                            colors.errorContainer 
                        else 
                            colors.tertiaryContainer
                    )
                ) {
                    Text(
                        result,
                        color = if (result.contains("Failed")) 
                            colors.onErrorContainer 
                        else 
                            colors.onTertiaryContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}