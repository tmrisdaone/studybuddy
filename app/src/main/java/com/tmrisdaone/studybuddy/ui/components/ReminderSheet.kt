package com.tmrisdaone.studybuddy.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderOptionsSheet(
    sheetState: SheetState,
    showReminderSheet: MutableState<Boolean>,
    context: android.content.Context,
    scope: androidx.compose.runtime.CoroutineScope
) {
    val options = listOf(
        "1 Day Before" to 1L * 24 * 60 * 60 * 1000,
        "1 Hour Before" to 1L * 60 * 60 * 1000,
        "15 Minutes Before" to 15L * 60 * 1000,
        "No Reminder" to 0L
    )

    if (showReminderSheet.value) {
        ModalBottomSheet(
            onDismissRequest = { showReminderSheet.value = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Text(
                text = "Set Reminder",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
            )
            options.forEach { (label, offset) ->
                ListItem(
                    headlineContent = { Text(label) },
                    trailingContent = if (offset == 0L) {
                        Icon(Icons.Default.Check, contentDescription = null)
                    } else null,
                    modifier = Modifier.clickable {
                        showReminderSheet.value = false
                        scope.launch {
                            val alarmManager = context.getSystemService(android.app.AlarmManager::class.java)
                            val intent = android.content.Intent(context, com.tmrisdaone.studybuddy.receiver.ReminderReceiver::class.java).apply {
                                putExtra("title", "Study Reminder")
                                putExtra("message", "Time to study!")
                            }
                            val pendingIntent = androidx.core.app.PendingIntentCompat.getBroadcast(
                                context, 0, intent,
                                androidx.core.app.PendingIntentCompat.FLAG_IMMUTABLE
                            )
                            val triggerAt = System.currentTimeMillis() + offset
                            alarmManager?.setExact(android.app.AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
