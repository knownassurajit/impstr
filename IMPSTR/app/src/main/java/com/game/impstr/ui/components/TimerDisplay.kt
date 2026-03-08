package com.game.impstr.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.Locale

@Composable
fun TimerDisplay(
    seconds: Long,
    modifier: Modifier = Modifier,
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.headlineMedium,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary,
) {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    val timeString = String.format(Locale.getDefault(), "%02d:%02d", minutes, remainingSeconds)

    Text(
        text = timeString,
        style = style,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = modifier.padding(8.dp),
    )
}
