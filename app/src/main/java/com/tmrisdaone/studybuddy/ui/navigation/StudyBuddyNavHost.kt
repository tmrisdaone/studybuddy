package com.tmrisdaone.studybuddy.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tmrisdaone.studybuddy.ui.screens.*
import com.tmrisdaone.studybuddy.ui.viewmodels.SettingsViewModel
import com.tmrisdaone.studybuddy.ui.viewmodels.ChatViewModel

@Composable
fun StudyBuddyNavHost(settingsViewModel: SettingsViewModel, chatViewModel: ChatViewModel) {
    val nav = rememberNavController()
    val msgs by chatViewModel.messages.collectAsState(emptyList())
    val sid by chatViewModel.sessionId.collectAsState(0L)
    val loading by chatViewModel.isLoading.collectAsState(false)

    NavHost(navController = nav, startDestination = "chat") {
        composable("chat") {
            ChatScreen(
                messages = msgs,
                sessionId = sid,
                isLoading = loading,
                onSend = chatViewModel::send,
                onGenerateQuiz = chatViewModel::generateQuiz,
                onGenerateFlashcards = chatViewModel::generateFlashcards,
                onSummarize = chatViewModel::summarize
            )
        }
        composable("settings") {
            SettingsScreen(
                groqKey = settingsViewModel.groqKey.value,
                onKeyChange = settingsViewModel::setGroqKey,
                sttModel = settingsViewModel.sttModel.value,
                onSttChange = settingsViewModel::setSttModel,
                llmModel = settingsViewModel.llmModel.value,
                onLlmChange = settingsViewModel::setLlmModel
            )
        }
        composable("history") {
            HistoryScreen(sessions = emptyList())
        }
        composable("scanner") {
            ScannerScreen(onDocumentScanned = { nav.popBackStack() })
        }
    }
}