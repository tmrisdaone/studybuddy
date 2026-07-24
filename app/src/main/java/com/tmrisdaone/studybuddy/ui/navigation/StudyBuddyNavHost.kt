package com.tmrisdaone.studybuddy.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.material3.MaterialTheme
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tmrisdaone.studybuddy.ui.screens.ChatScreen
import com.tmrisdaone.studybuddy.ui.screens.HistoryScreen
import com.tmrisdaone.studybuddy.ui.screens.ProviderSettingsScreen
import com.tmrisdaone.studybuddy.ui.screens.ScannerScreen
import com.tmrisdaone.studybuddy.ui.screens.SettingsScreen
import com.tmrisdaone.studybuddy.ui.viewmodels.ChatViewModel
import com.tmrisdaone.studybuddy.ui.viewmodels.HistoryViewModel
import com.tmrisdaone.studybuddy.ui.viewmodels.ScannerViewModel
import com.tmrisdaone.studybuddy.ui.viewmodels.SettingsViewModel

private sealed class TopRoute(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Chat : TopRoute("chat", "Chat", Icons.Filled.ChatBubble)
    object Scan : TopRoute("scanner", "Scan", Icons.Filled.DocumentScanner)
    object History : TopRoute("history", "History", Icons.Filled.History)
    object Settings : TopRoute("settings", "Settings", Icons.Filled.Settings)
}

private val routes = listOf(TopRoute.Chat, TopRoute.Scan, TopRoute.History, TopRoute.Settings)

@Composable
fun StudyBuddyNavHost(
    chatViewModel: ChatViewModel,
    settingsViewModel: SettingsViewModel,
    historyViewModel: HistoryViewModel,
    scannerViewModel: ScannerViewModel
) {
    val navController = rememberNavController()
    val colors = MaterialTheme.colorScheme

    val backStack by navController.currentBackStackEntryAsState()
    val current = backStack?.destination?.route
    val showBottomBar = current == null || current in routes.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = colors.surfaceContainer,
                    tonalElevation = 0.dp
                ) {
                    routes.forEach { route ->
                        val selected = current == route.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(route.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(route.icon, contentDescription = route.label) },
                            label = { Text(route.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = colors.primary,
                                selectedTextColor = colors.primary,
                                indicatorColor = colors.primaryContainer,
                                unselectedIconColor = colors.onSurfaceVariant,
                                unselectedTextColor = colors.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    ) { inner ->
        NavHost(
            navController = navController,
            startDestination = TopRoute.Chat.route,
            modifier = Modifier.padding(inner)
        ) {
            composable(TopRoute.Chat.route) {
                ChatScreen(viewModel = chatViewModel, settingsViewModel = settingsViewModel)
            }
            composable(TopRoute.Scan.route) {
                ScannerScreen(viewModel = scannerViewModel)
            }
            composable(TopRoute.History.route) {
                HistoryScreen(viewModel = historyViewModel)
            }
            composable(TopRoute.Settings.route) {
                SettingsScreen(viewModel = settingsViewModel, onNavigateToProviders = {
                    navController.navigate("providers")
                })
            }
            composable("providers") {
                ProviderSettingsScreen(viewModel = settingsViewModel, onBack = { navController.popBackStack() })
            }
        }
    }
}
