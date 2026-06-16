package com.tmrisdaone.studybuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tmrisdaone.studybuddy.data.local.StudyBuddyDatabase
import com.tmrisdaone.studybuddy.data.repo.StudyBuddyRepository
import com.tmrisdaone.studybuddy.ui.navigation.StudyBuddyNavHost
import com.tmrisdaone.studybuddy.ui.theme.StudyBuddyTheme
import com.tmrisdaone.studybuddy.ui.viewmodels.ChatViewModel
import com.tmrisdaone.studybuddy.ui.viewmodels.SettingsViewModel
import com.tmrisdaone.studybuddy.ui.viewmodels.HistoryViewModel
import com.tmrisdaone.studybuddy.ui.viewmodels.ScannerViewModel

// Note: Using manual ViewModel factories since Hilt doesn't work in Termux/proot
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudyBuddyTheme {
                val db = StudyBuddyDatabase.get(this@MainActivity)
                val repo = StudyBuddyRepository(db, this@MainActivity)
                
                val chatViewModel = ChatViewModel(repo)
                val settingsViewModel = SettingsViewModel(repo)
                val historyViewModel = HistoryViewModel(repo)
                val scannerViewModel = ScannerViewModel(repo)
                
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