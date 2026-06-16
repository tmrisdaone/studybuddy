package com.tmrisdaone.studybuddy.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.MaterialTheme
import com.tmrisdaone.studybuddy.domain.ChatMessage

@Composable
fun MessageBubble(message: ChatMessage) {
    val isUser = message.role == "user"
    val colors = MaterialTheme.colorScheme
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) 
                    colors.primary 
                else 
                    colors.surfaceContainerHighest
            ),
            shape = RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp,
                bottomStart = if (isUser) 4.dp else 20.dp,
                bottomEnd = if (isUser) 20.dp else 4.dp
            ),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = message.content,
                color = if (isUser) 
                    colors.onPrimary 
                else 
                    colors.onSurface,
                fontSize = 15.sp,
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Start
            )
        }
    }
}