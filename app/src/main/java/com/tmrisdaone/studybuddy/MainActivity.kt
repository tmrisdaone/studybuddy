package com.tmrisdaone.studybuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.hilt.navigation.compose.hiltViewModel
import com.tmrisdaone.studybuddy.ui.navigation.StudyBuddyNavHost
import com.tmrisdaone.studybuddy.ui.theme.StudyBuddyTheme
import com.tmrisdaone.studybuddy.ui.viewmodels.ChatViewModel
import com.tmrisdaone.studybuddy.ui.viewmodels.SettingsViewModel
import com.tmrisdaone.studybuddy.ui.viewmodels.HistoryViewModel
import com.tmrisdaone.studybuddy.ui.viewmodels.ScannerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudyBuddyTheme {
                val chatViewModel = hiltViewModel<ChatViewModel>()
                val settingsViewModel = hiltViewModel<SettingsViewModel>()
                val historyViewModel = hiltViewModel<HistoryViewModel>()
                val scannerViewModel = hiltViewModel<ScannerViewModel>()
                
                StudyBuddyNavHost(
                    chatViewModel = chatViewModel,
                    settingsViewModel = settingsViewModel,
                    historyViewModel = historyViewModel,
                    scannerViewModel = scannerViewModel
                )
            }
        }
    }
}
