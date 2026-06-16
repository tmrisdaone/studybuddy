package com.tmrisdaone.studybuddy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.spacer
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import com.tmrisdaone.studybuddy.ui.viewmodels.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateToChat: () -> Unit
) {
    var apiKey by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    
    val savedApiKey by viewModel.apiKey.observeAsState("")
    val selectedModel by viewModel.selectedModel.observeAsState("llama-3.1-8b-instant")
    val availableModels = viewModel.getAvailableModels()
    val isSaving by viewModel.isSaving.observeAsState(false)
    val saveResult by viewModel.saveResult.observeAsState<String?>(null)
    val colors = MaterialTheme.colorScheme
    
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Settings", fontWeight = FontWeight.Bold) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.surfaceContainer),
            navigationIcon = {
                IconButton(onClick = onNavigateToChat) {
                    Icon(
                        painter = Icons.Filled.ArrowBack,
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
                    
                    androidx.compose.material3.TextField(
                        value = apiKey,
                        onValueChange = { apiKey = it },
                        label = { Text("Enter your Groq API Key") },
                        placeholder = { Text("sk-...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        isError = apiKey.isNotBlank() && !apiKey.startsWith("sk-"),
                        colors = androidx.compose.material3.TextFieldDefaults.textFieldColors(
                            focusedBorderColor = colors.primary,
                            unfocusedBorderColor = colors.outline,
                            labelColor = colors.onSurfaceVariant,
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
            
            // Model Selection Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = colors.surfaceContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Model", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = colors.onSurface)
                    androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.foundation.layout.height(12.dp))
                    
                    // Simple dropdown using ExposedDropdownMenuBox
                    androidx.compose.material3.ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        androidx.compose.material3.TextField(
                            value = selectedModel,
                            onValueChange = { /* handled by menu */ },
                            label = { Text("Select Model") },
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    painter = if (expanded) 
                                        Icons.Filled.KeyboardArrowUp 
                                    else 
                                        Icons.Filled.KeyboardArrowDown,
                                    contentDescription = "Expand"
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = androidx.compose.material3.TextFieldDefaults.textFieldColors(
                                focusedBorderColor = colors.primary,
                                unfocusedBorderColor = colors.outline,
                                containerColor = colors.surface
                            )
                        )
                        
                        androidx.compose.material3.DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            availableModels.forEach { model ->
                                androidx.compose.material3.DropdownMenuItem(
                                    onClick = {
                                        viewModel.setModel(model)
                                        expanded = false
                                    },
                                    enabled = true
                                ) {
                                    Text(
                                        text = model,
                                        color = if (model == selectedModel) colors.primary else colors.onSurface
                                    )
                                }
                            }
                        }
                    }
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
                                painter = Icons.Filled.CheckCircle,
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