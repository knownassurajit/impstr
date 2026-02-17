package com.example.imposter

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

// CHANGE: Use @Composable fun instead of annotation class
@Composable
fun GameView(
    modifier: Modifier = Modifier, // Standard to provide a default
    color: Color,
    content: @Composable () -> Unit // Mark content as Composable
) {
    // Your UI implementation goes here.
    // For example:
    androidx.compose.foundation.layout.Box(
        modifier = modifier.background(color)
    ) {
        content()
    }
}

data class GameViewData(
    val modifier: Modifier = Modifier,
    val color: Color,
    val content: @Composable () -> Unit
)