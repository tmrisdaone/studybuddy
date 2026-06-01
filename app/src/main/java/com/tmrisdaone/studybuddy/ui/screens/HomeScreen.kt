package com.tmrisdaone.studybuddy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        TopAppBar(
            title = { Text("StudyBuddy") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )

        NavigationBar {
            listOf(
                "Chat" to Icons.Default.Chat,
                "History" to Icons.Default.History,
                "Scanner" to Icons.Default.Scanner, // or Camera
                "Settings" to Icons.Default.Settings
            ).forEachIndexed { i, (label, icon) ->
                NavigationBarItem(
                    selected = selectedTab == i,
                    onClick = {
                        selectedTab = i
                        when (i) {
                            0 -> navController.navigate("chat")
                            1 -> navController.navigate("history")
                            2 -> navController.navigate("scanner")
                            3 -> navController.navigate("settings")
                        }
                    },
                    icon = { Icon(icon, null) },
                    label = { Text(label) }
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(
                text = when (selectedTab) {
                    0 -> "Chat with AI"
                    1 -> "Study History"
                    2 -> "Scan Document"
                    else -> "Settings"
                },
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
