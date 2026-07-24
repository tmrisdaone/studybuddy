package com.tmrisdaone.studybuddy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tmrisdaone.studybuddy.domain.StudySession
import com.tmrisdaone.studybuddy.ui.theme.TurboGradients
import com.tmrisdaone.studybuddy.ui.viewmodels.HistoryViewModel

@Composable
fun HistoryScreen(viewModel: HistoryViewModel) {
    val sessions by viewModel.sessions.collectAsStateWithLifecycle(emptyList())
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(false)
    val colors = MaterialTheme.colorScheme

    Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TurboGradients.header)
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("History", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = colors.primary)
                    Text("Your study sessions", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = colors.onSurface)
                }
                IconButton(onClick = { viewModel.refresh() }, enabled = !isLoading) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Refresh", tint = colors.primary)
                }
            }

            if (sessions.isEmpty() && !isLoading) {
                EmptyHistory()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(sessions) { session -> SessionCard(session) }
                    if (isLoading) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = colors.primary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyHistory() {
    val colors = MaterialTheme.colorScheme
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Icon(Icons.Filled.History, contentDescription = null, modifier = Modifier.size(60.dp), tint = colors.primary.copy(alpha = 0.6f))
            Text("No study sessions yet", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = colors.onSurfaceVariant)
            Text("Start chatting or scanning to create sessions", fontSize = 14.sp, color = colors.onSurfaceVariant.copy(alpha = 0.7f))
        }
    }
}

@Composable
private fun SessionCard(session: StudySession) {
    val colors = MaterialTheme.colorScheme
    val typeIcon = when (session.type) {
        "chat" -> Icons.Filled.Chat
        "pdf" -> Icons.Filled.PictureAsPdf
        "youtube" -> Icons.Filled.PlayCircle
        "scan" -> Icons.Filled.DocumentScanner
        "flashcards" -> Icons.Filled.Style
        else -> Icons.Filled.Folder
    }
    val dateStr = session.createdAt.toString().substring(0, 16).replace('T', ' ')

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = colors.surfaceContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(40.dp).background(TurboGradients.accent, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(typeIcon, contentDescription = session.type, tint = colors.onPrimary, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    session.title,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = colors.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(session.type, fontSize = 12.sp, color = colors.onSurfaceVariant)
                    Text("•", fontSize = 12.sp, color = colors.onSurfaceVariant.copy(alpha = 0.5f))
                    Text(dateStr, fontSize = 12.sp, color = colors.onSurfaceVariant)
                }
                session.summary?.let { summary ->
                    Text(summary, fontSize = 13.sp, color = colors.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}
