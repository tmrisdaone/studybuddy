package com.tmrisdaone.studybuddy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tmrisdaone.studybuddy.domain.StudySession
import com.tmrisdaone.studybuddy.ui.viewmodels.HistoryViewModel

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onNavigateToChat: () -> Unit
) {
    val sessions by viewModel.sessions.collectAsStateWithLifecycle(emptyList())
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(false)
    val colors = MaterialTheme.colorScheme
    
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("History", fontWeight = FontWeight.Bold) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.surfaceContainer),
            navigationIcon = {
                IconButton(onClick = onNavigateToChat) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            actions = {
                IconButton(onClick = { viewModel.refresh() }, enabled = !isLoading) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Refresh"
                    )
                }
            }
        )
        
        if (sessions.isEmpty() && !isLoading) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.History,
                        contentDescription = "",
                        modifier = Modifier.size(64.dp),
                        tint = colors.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Text(
                        "No study sessions yet",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.onSurfaceVariant
                    )
                    Text(
                        "Start chatting or scanning to create sessions",
                        fontSize = 14.sp,
                        color = colors.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sessions) { session ->
                    SessionCard(session = session)
                }
            }
        }
        
        if (isLoading) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun SessionCard(session: StudySession) {
    val typeIcon = when (session.type) {
        "chat" -> Icons.Filled.Chat
        "pdf" -> Icons.Filled.PictureAsPdf
        "youtube" -> Icons.Filled.PlayCircle
        "scan" -> Icons.Filled.DocumentScanner
        "flashcards" -> Icons.Filled.Style
        else -> Icons.Filled.Folder
    }
    
    // Convert Instant to readable string
    val dateStr = session.createdAt.toString().substring(0, 16).replace('T', ' ')
    val colors = MaterialTheme.colorScheme
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colors.surfaceContainer
        )
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = typeIcon,
                contentDescription = session.type,
                modifier = Modifier.size(24.dp),
                tint = colors.primary
            )
            androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.foundation.layout.width(12.dp))
            androidx.compose.foundation.layout.Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = session.title,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = colors.onSurface,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.TextOverflow.Ellipsis
                )
                androidx.compose.foundation.layout.Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = session.type.capitalize(),
                        fontSize = 12.sp,
                        color = colors.onSurfaceVariant
                    )
                    Text(
                        text = "•",
                        fontSize = 12.sp,
                        color = colors.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Text(
                        text = dateStr,
                        fontSize = 12.sp,
                        color = colors.onSurfaceVariant
                    )
                }
                session.summary?.let { summary ->
                    Text(
                        text = summary,
                        fontSize = 13.sp,
                        color = colors.onSurfaceVariant,
                        maxLines = 2,
                        overflow = androidx.compose.ui.text.TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}