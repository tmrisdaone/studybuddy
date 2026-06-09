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
val chatVm: ChatViewModel = viewModel(factory = ChatViewModel.factory((application as StudyBuddyApp).database, this@MainActivity))
StudyBuddyNavHost(settingsViewModel = settingsVm, chatViewModel = chatVm)
                }
            }
        }
    }
}
