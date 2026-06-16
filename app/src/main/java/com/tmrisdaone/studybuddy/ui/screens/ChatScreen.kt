package com.tmrisdaone.studybuddy.ui.screens

import androidx.activity.compose.rememberCoroutineScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.spacer
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.DocumentScanner
import com.tmrisdaone.studybuddy.domain.ChatMessage
import com.tmrisdaone.studybuddy.ui.components.MessageBubble
import com.tmrisdaone.studybuddy.ui.components.MessageInput
import com.tmrisdaone.studybuddy.ui.viewmodels.ChatViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.material3.MaterialTheme

@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToScanner: () -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    val messages by viewModel.messages.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val error by viewModel.error.observeAsState<String?>(null)
    
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        TopAppBar(
            title = { Text("StudyBuddy Chat", fontWeight = FontWeight.Bold) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            navigationIcon = {
                IconButton(onClick = onNavigateToHistory) {
                    Icon(
                        painter = Icons.Filled.History,
                        contentDescription = "History"
                    )
                }
            },
            actions = {
                IconButton(onClick = onNavigateToScanner) {
                    Icon(
                        painter = Icons.Filled.DocumentScanner,
                        contentDescription = "Scanner"
                    )
                }
                IconButton(onClick = onNavigateToSettings) {
                    Icon(
                        painter = Icons.Filled.Settings,
                        contentDescription = "Settings"
                    )
                }
            }
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 0.dp, bottom = 0.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (messages.isEmpty()) {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Start a conversation\nTap the scanner to capture text",
                        textAlign = androidx.compose.ui.text.TextAlign.Center,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                androidx.compose.foundation.lazy.LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    reverseLayout = true
                ) {
                    items(messages.reversed()) { message ->
                        MessageBubble(message = message)
                    }
                    item {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            )
                        }
                    }
                }
            }
            
            error?.let { err ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        err,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
        
        MessageInput(
            messageText = messageText,
            onTextChange = { messageText = it },
            onSend = {
                if (messageText.isNotBlank() && !isLoading) {
                    val systemPrompt = "You are a helpful study assistant. Provide clear, concise explanations."
                    val model = "llama-3.1-8b-instant"
                    viewModel.sendMessage(messageText, systemPrompt, model)
                    messageText = ""
                    coroutineScope.launch {
                        kotlinx.coroutines.delay(100)
                        scrollState.animateScrollTo(0)
                    }
                }
            },
            isLoading = isLoading
        )
    }
}