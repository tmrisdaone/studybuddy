package com.tmrisdaone.studybuddy.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tmrisdaone.studybuddy.ui.screens.ChatScreen
import com.tmrisdaone.studybuddy.ui.screens.HistoryScreen
import com.tmrisdaone.studybuddy.ui.screens.ScannerScreen
import com.tmrisdaone.studybuddy.ui.screens.SettingsScreen
import com.tmrisdaone.studybuddy.ui.viewmodels.ChatViewModel
import com.tmrisdaone.studybuddy.ui.viewmodels.HistoryViewModel
import com.tmrisdaone.studybuddy.ui.viewmodels.ScannerViewModel
import com.tmrisdaone.studybuddy.ui.viewmodels.SettingsViewModel

@Composable
fun StudyBuddyNavHost(
    chatViewModel: ChatViewModel,
    settingsViewModel: SettingsViewModel,
    historyViewModel: HistoryViewModel,
    scannerViewModel: ScannerViewModel
) {
    val navController = rememberNavController()
    val startDestination = remember { mutableStateOf("chat") }
    
    NavHost(navController, startDestination = startDestination.value) {
        composable("chat") {
            ChatScreen(
                viewModel = chatViewModel,
                onNavigateToHistory = { startDestination.value = "history" },
                onNavigateToSettings = { startDestination.value = "settings" },
                onNavigateToScanner = { startDestination.value = "scanner" }
            )
        }
        composable("history") {
            HistoryScreen(
                viewModel = historyViewModel,
                onNavigateToChat = { startDestination.value = "chat" }
            )
        }
        composable("scanner") {
            ScannerScreen(
                viewModel = scannerViewModel,
                onNavigateToChat = { startDestination.value = "chat" }
            )
        }
        composable("settings") {
            SettingsScreen(
                viewModel = settingsViewModel,
                onNavigateToChat = { startDestination.value = "chat" }
            )
        }
    }
}