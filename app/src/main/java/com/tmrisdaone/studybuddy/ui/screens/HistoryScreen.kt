package com.tmrisdaone.studybuddy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tmrisdaone.studybuddy.domain.StudySession
import com.tmrisdaone.studybuddy.ui.theme.StudyBuddyTheme
import com.tmrisdaone.studybuddy.ui.viewmodels.HistoryViewModel
import kotlinx.datetime.format.DateTimeFormatter
import kotlinx.datetime.format.Companion.custom

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onNavigateToChat: () -> Unit
) {
    val sessions by viewModel.sessions.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("History", fontWeight = FontWeight.Bold) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = StudyBuddyTheme.colorScheme.surfaceContainer),
            navigationIcon = {
                IconButton(onClick = onNavigateToChat) {
                    Icon(
                        painter = androidx.compose.material.icons.Icons.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            actions = {
                IconButton(onClick = { viewModel.refresh() }, enabled = !isLoading) {
                    Icon(
                        painter = androidx.compose.material.icons.Icons.Filled.Refresh,
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
                        painter = androidx.compose.material.icons.Icons.Filled.History,
                        contentDescription = "",
                        modifier = Modifier.size(64.dp),
                        tint = StudyBuddyTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Text(
                        "No study sessions yet",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = StudyBuddyTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Start chatting or scanning to create sessions",
                        fontSize = 14.sp,
                        color = StudyBuddyTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
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
                androidx.compose.material3.CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun SessionCard(session: StudySession) {
    val typeIcon = when (session.type) {
        "chat" -> androidx.compose.material.icons.Icons.Filled.Chat
        "pdf" -> androidx.compose.material.icons.Icons.Filled.PictureAsPdf
        "youtube" -> androidx.compose.material.icons.Icons.Filled.PlayCircle
        "scan" -> androidx.compose.material.icons.Icons.Filled.DocumentScanner
        "flashcards" -> androidx.compose.material.icons.Icons.Filled.Style
        else -> androidx.compose.material.icons.Icons.Filled.Folder
    }
    
    val formatter = DateTimeFormatter.ofPattern("MMM d, HH:mm")
    val dateStr = session.createdAt.format(formatter)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = StudyBuddyTheme.colorScheme.surfaceContainer
        )
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.Icon(
                painter = typeIcon,
                contentDescription = session.type,
                modifier = Modifier.size(24.dp),
                tint = StudyBuddyTheme.colorScheme.primary
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(12.dp))
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
                    color = StudyBuddyTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.TextOverflow.Ellipsis
                )
                androidx.compose.foundation.layout.Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = session.type.capitalize(),
                        fontSize = 12.sp,
                        color = StudyBuddyTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "•",
                        fontSize = 12.sp,
                        color = StudyBuddyTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Text(
                        text = dateStr,
                        fontSize = 12.sp,
                        color = StudyBuddyTheme.colorScheme.onSurfaceVariant
                    )
                }
                session.summary?.let { summary ->
                    Text(
                        text = summary,
                        fontSize = 13.sp,
                        color = StudyBuddyTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = androidx.compose.ui.text.TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}