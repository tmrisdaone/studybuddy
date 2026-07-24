package com.tmrisdaone.studybuddy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tmrisdaone.studybuddy.domain.ChatMessage
import com.tmrisdaone.studybuddy.ui.theme.TurboGradients

@Composable
fun MessageBubble(message: ChatMessage) {
    val isUser = message.role == "user"
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isUser) {
            AssistantAvatar(modifier = Modifier.padding(end = 8.dp))
        }
        Box(
            modifier = Modifier
                .widthIn(max = 320.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomStart = if (isUser) 20.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 20.dp
                    )
                )
                .background(
                    if (isUser) TurboGradients.accent
                    else Brush.verticalGradient(listOf(colors.surfaceVariant, colors.surfaceVariant))
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = message.content,
                color = if (isUser) colors.onPrimary else colors.onSurface,
                fontSize = 15.sp,
                lineHeight = 21.sp
            )
        }
    }
}

@Composable
private fun AssistantAvatar(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(28.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(TurboGradients.accent),
        contentAlignment = Alignment.Center
    ) {
        Text("S", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}
