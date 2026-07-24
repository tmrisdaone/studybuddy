package com.tmrisdaone.studybuddy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import com.tmrisdaone.studybuddy.ui.theme.TurboGradients

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageInput(
    messageText: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    isLoading: Boolean
) {
    var text by remember { mutableStateOf(messageText) }
    LaunchedEffect(messageText) {
        if (text != messageText) text = messageText
    }
    val colors = MaterialTheme.colorScheme
    val canSend = text.isNotBlank() && !isLoading

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = colors.surfaceVariant,
            tonalElevation = 0.dp,
            modifier = Modifier.weight(1f)
        ) {
            TextField(
                value = text,
                onValueChange = { text = it; onTextChange(it) },
                placeholder = { Text("Ask StudyBuddy...", color = colors.onSurfaceVariant) },
                singleLine = false,
                maxLines = 4,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send, autoCorrect = true),
                visualTransformation = VisualTransformation.None,
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    cursorColor = colors.primary,
                    focusedTextColor = colors.onSurface,
                    unfocusedTextColor = colors.onSurface
                )
            )
        }
        val background = if (canSend) TurboGradients.accent
        else Brush.linearGradient(listOf(colors.surfaceContainerHighest, colors.surfaceContainerHighest))
        Box(
            modifier = Modifier
                .size(46.dp)
                .background(background, CircleShape)
                .clickable(enabled = canSend) { onSend() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send",
                tint = if (canSend) Color.White else colors.onSurfaceVariant
            )
        }
    }
}
