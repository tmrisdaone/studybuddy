package com.tmrisdaone.studybuddy.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(sessions: List<StudySession>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { Text("Study History", style = MaterialTheme.typography.headlineMedium) }

        if (sessions.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No sessions yet. Start studying!", color = MaterialTheme.colorScheme.outline)
                }
            }
        }

        items(sessions) { s ->
            Card(onClick = { /* open detail */ }) {
                ListItem(
                    headlineContent = { Text(s.title) },
                    supportingContent = { Text("${s.type} · ${s.inputType}") },
                    leadingContent = {
                        Icon(
                            when (s.inputType) {
                                "pdf" -> Icons.Default.PictureAsPdf
                                "youtube_url", "youtube" -> Icons.Default.PlayCircle
                                "image" -> Icons.Default.Image
                                else -> Icons.Default.Description
                            },
                            null
                        )
                    },
                    trailingContent = { Text(s.createdAt.toString().take(10)) }
                )
            }
        }
    }
}
