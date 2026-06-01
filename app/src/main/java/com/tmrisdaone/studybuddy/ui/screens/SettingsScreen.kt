package com.tmrisdaone.studybuddy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun SettingsScreen(
    groqKey: String,
    onKeyChange: (String) -> Unit,
    sttModel: String,
    onSttChange: (String) -> Unit,
    llmModel: String,
    onLlmChange: (String) -> Unit
) {
    var keyVisible by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = groqKey,
            onValueChange = onKeyChange,
            label = { Text("Groq API Key") },
            visualTransformation = if (keyVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { keyVisible = !keyVisible }) {
                    Icon(if (keyVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        var sttExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = sttExpanded, onExpandedChange = { sttExpanded = it }) {
            OutlinedTextField(
                value = sttModel,
                onValueChange = {},
                readOnly = true,
                label = { Text("STT Model") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sttExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(expanded = sttExpanded, onDismissRequest = { sttExpanded = false }) {
                listOf("whisper-large-v3-turbo", "whisper-large-v3", "distil-whisper-large-v3-en").forEach {
                    DropdownMenuItem(text = { Text(it) }, onClick = { onSttChange(it); sttExpanded = false })
                }
            }
        }

        var llmExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = llmExpanded, onExpandedChange = { llmExpanded = it }) {
            OutlinedTextField(
                value = llmModel,
                onValueChange = {},
                readOnly = true,
                label = { Text("LLM Model") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = llmExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(expanded = llmExpanded, onDismissRequest = { llmExpanded = false }) {
                listOf("llama-3.1-8b-instant", "llama-3.3-70b-specdec", "mixtral-8x7b-32768", "deepseek-r1-distill-llama-70b-specdec", "gemma2-9b-it").forEach {
                    DropdownMenuItem(text = { Text(it) }, onClick = { onLlmChange(it); llmExpanded = false })
                }
            }
        }
    }
}
