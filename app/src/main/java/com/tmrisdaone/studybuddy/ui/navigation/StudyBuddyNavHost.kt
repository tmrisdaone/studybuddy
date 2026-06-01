package com.tmrisdaone.studybuddy.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tmrisdaone.studybuddy.ui.screens.*

@Composable
fun StudyBuddyNavHost(viewModel: com.tmrisdaone.studybuddy.ui.viewmodels.SettingsViewModel) {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = "chat") {
        composable("chat") {
            ChatScreen(
                onNavigateToSettings = { nav.navigate("settings") }
            )
        }
        composable("settings") {
            SettingsScreen(
                viewModel = viewModel,
                onNavigateBack = { nav.popBackStack() }
            )
        }
        composable("history") { HistoryScreen(emptyList()) }
        composable("scanner") { ScannerScreen(onDocumentScanned = { nav.popBackStack() }) }
    }
}
