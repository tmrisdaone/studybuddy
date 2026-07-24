package com.tmrisdaone.studybuddy.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tmrisdaone.studybuddy.domain.ChatMessage
import com.tmrisdaone.studybuddy.ui.components.MessageBubble
import com.tmrisdaone.studybuddy.ui.components.MessageInput
import com.tmrisdaone.studybuddy.ui.theme.TurboGradients
import com.tmrisdaone.studybuddy.ui.viewmodels.ChatViewModel
import com.tmrisdaone.studybuddy.ui.viewmodels.SettingsViewModel

@Composable
fun ChatScreen(viewModel: ChatViewModel, settingsViewModel: SettingsViewModel) {
    var messageText by remember { mutableStateOf("") }
    val messages by viewModel.messages.collectAsStateWithLifecycle(emptyList())
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(false)
    val error by viewModel.error.collectAsStateWithLifecycle<String?>(null)
    val activeLabel by viewModel.activeLabel.collectAsStateWithLifecycle("")
    val providers by settingsViewModel.providers.collectAsStateWithLifecycle(emptyList())
    val activeId by settingsViewModel.activeProviderId.collectAsStateWithLifecycle(null)
    val models by settingsViewModel.models.collectAsStateWithLifecycle(emptyList())
    val colors = MaterialTheme.colorScheme
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size, isLoading) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.lastIndex)
    }

    Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Header(
                activeLabel = activeLabel,
                providers = providers,
                activeId = activeId,
                models = models,
                onPickProvider = {
                    settingsViewModel.setActiveProvider(it)
                },
                onPickModel = { modelId ->
                    settingsViewModel.setActiveModel(modelId)
                }
            )

            if (messages.isEmpty()) {
                EmptyState(
                    onPrompt = { msg ->
                        messageText = msg
                    }
                )
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(messages) { msg -> MessageBubble(msg) }
                    item {
                        AnimatedVisibility(visible = isLoading, enter = fadeIn(), exit = fadeOut()) {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        strokeWidth = 2.dp,
                                        color = colors.primary
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("Thinking…", color = colors.onSurfaceVariant, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }

            error?.let { err ->
                Surface(
                    color = colors.errorContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        err,
                        color = colors.onErrorContainer,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            MessageInput(
                messageText = messageText,
                onTextChange = { messageText = it },
                onSend = {
                    if (messageText.isNotBlank() && !isLoading) {
                        val systemPrompt = "You are a helpful study assistant. Provide clear, concise explanations."
                        viewModel.sendMessage(messageText, systemPrompt, "")
                        messageText = ""
                    }
                },
                isLoading = isLoading
            )
        }
    }
}

@Composable
private fun Header(
    activeLabel: String,
    providers: List<com.tmrisdaone.studybuddy.domain.ApiProvider>,
    activeId: String?,
    models: List<com.tmrisdaone.studybuddy.domain.ModelInfo>,
    onPickProvider: (String) -> Unit,
    onPickModel: (String) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    var providerMenuOpen by remember { mutableStateOf(false) }
    var modelMenuOpen by remember { mutableStateOf(false) }
    val active = providers.firstOrNull { it.id == activeId }
    val modelOptions = (models.map { it.id } + (active?.defaultModel?.let { listOf(it) } ?: emptyList())).distinct()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(TurboGradients.header)
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        Column {
            Text(
                "StudyBuddy",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.primary
            )
            Text(
                "What do you want to learn today?",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onSurface
            )
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box {
                    Row(
                        modifier = Modifier
                            .background(colors.surfaceContainer, RoundedCornerShape(12.dp))
                            .clickable { providerMenuOpen = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(active?.name ?: "No provider", fontWeight = FontWeight.Medium, color = colors.onSurface, fontSize = 13.sp)
                        Icon(
                            androidx.compose.material.icons.Icons.Filled.ArrowDropDown,
                            contentDescription = null,
                            tint = colors.onSurfaceVariant
                        )
                    }
                    DropdownMenu(expanded = providerMenuOpen, onDismissRequest = { providerMenuOpen = false }) {
                        providers.forEach { p ->
                            DropdownMenuItem(
                                text = { Text(p.name + "  ·  " + p.type.name.replace('_', ' ')) },
                                onClick = { onPickProvider(p.id); providerMenuOpen = false }
                            )
                        }
                        if (providers.isEmpty()) {
                            DropdownMenuItem(text = { Text("Add a provider in Settings") }, onClick = { providerMenuOpen = false })
                        }
                    }
                }
                Spacer(Modifier.width(8.dp))
                Box {
                    Row(
                        modifier = Modifier
                            .background(colors.surfaceContainer, RoundedCornerShape(12.dp))
                            .clickable { modelMenuOpen = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(active?.defaultModel?.ifBlank { "model" } ?: "model", color = colors.onSurface, fontSize = 13.sp)
                        Icon(
                            androidx.compose.material.icons.Icons.Filled.ArrowDropDown,
                            contentDescription = null,
                            tint = colors.onSurfaceVariant
                        )
                    }
                    DropdownMenu(expanded = modelMenuOpen, onDismissRequest = { modelMenuOpen = false }) {
                        if (modelOptions.isEmpty()) {
                            DropdownMenuItem(text = { Text("Select a provider first") }, onClick = { modelMenuOpen = false })
                        } else {
                            modelOptions.forEach { id ->
                                DropdownMenuItem(text = { Text(id) }, onClick = { onPickModel(id); modelMenuOpen = false })
                            }
                        }
                    }
                }
            }
    }
}

@Composable
private fun EmptyState(onPrompt: (String) -> Unit) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Try one of these:", color = colors.onSurfaceVariant, fontSize = 13.sp)
        prompts.forEach { prompt ->
            PromptChip(prompt) { onPrompt(prompt) }
        }
    }
}

@Composable
private fun PromptChip(text: String, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = colors.surfaceContainer,
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Text(
            text,
            color = colors.onSurface,
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
        )
    }
}

private val prompts = listOf(
    "Explain photosynthesis simply",
    "Summarize the French Revolution",
    "Give me 5 quiz questions on algebra",
    "What's the difference between mitosis and meiosis?"
)
