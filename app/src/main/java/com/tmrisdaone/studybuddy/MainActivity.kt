package com.tmrisdaone.studybuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tmrisdaone.studybuddy.ui.navigation.StudyBuddyNavHost
import com.tmrisdaone.studybuddy.ui.screens.*
import com.tmrisdaone.studybuddy.ui.theme.StudyBuddyTheme
import com.tmrisdaone.studybuddy.ui.viewmodels.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudyBuddyTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val settingsVm: SettingsViewModel = viewModel(factory = SettingsViewModel.factory((application as StudyBuddyApp).database))
                    val chatVm: ChatViewModel = viewModel(factory = ChatViewModel.factory((application as StudyBuddyApp).database))
                    var selectedTab by remember { mutableIntStateOf(0) }

                    Scaffold(
                        bottomBar = {
                            NavigationBar {
                                NavigationBarItem(
                                    selected = selectedTab == 0,
                                    onClick = { selectedTab = 0 },
                                    icon = { Icon(Icons.Default.Chat, null) },
                                    label = { Text("Chat") }
                                )
                                NavigationBarItem(
                                    selected = selectedTab == 1,
                                    onClick = { selectedTab = 1 },
                                    icon = { Icon(Icons.Default.History, null) },
                                    label = { Text("History") }
                                )
                                NavigationBarItem(
                                    selected = selectedTab == 2,
                                    onClick = { selectedTab = 2 },
                                    icon = { Icon(Icons.Default.Scanner, null) },
                                    label = { Text("Scan") }
                                )
                                NavigationBarItem(
                                    selected = selectedTab == 3,
                                    onClick = { selectedTab = 3 },
                                    icon = { Icon(Icons.Default.Settings, null) },
                                    label = { Text("Settings") }
                                )
                            }
                        }
                    ) { padding ->
                        Box(modifier = Modifier.padding(padding)) {
                            when (selectedTab) {
                                0 -> ChatScreen(
                                    messages = chatVm.messages.collectAsState(emptyList()).value,
                                    sessionId = chatVm.sessionId.collectAsState(0L).value,
                                    onSend = chatVm::send,
                                    onGenerateQuiz = { chatVm.generateQuiz("user context") },
                                    onGenerateFlashcards = {}
                                )
                                1 -> HistoryScreen(emptyList())
                                2 -> ScannerScreen { text -> chatVm.send("Scanned: $text") }
                                3 -> SettingsScreen(
                                    viewModel = settingsVm,
                                    onNavigateBack = {}
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
