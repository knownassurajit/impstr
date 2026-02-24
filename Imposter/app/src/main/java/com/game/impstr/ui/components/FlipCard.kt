package com.game.impstr.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun FlipCard(
    isFlipped: Boolean,
    onFlip: () -> Unit,
    modifier: Modifier = Modifier,
    front: @Composable () -> Unit,
    back: @Composable () -> Unit,
    flipDurationMs: Int = 400,
    enabled: Boolean = true,
) {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec =
            tween(
                durationMillis = flipDurationMs,
                easing = FastOutSlowInEasing,
            ),
        label = "cardFlip",
    )

    Box(
        modifier =
            modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null, // Disable ripple to avoid bounds artifact
                    enabled = enabled,
                    onClick = onFlip,
                )
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 12 * density // Increased distance for better 3D effect
                },
        contentAlignment = Alignment.Center,
    ) {
        if (rotation <= 90f) {
            // Front of Card
            Box(Modifier.fillMaxSize()) {
                front()
            }
        } else {
            // Back of Card
            Box(
                Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = 180f },
            ) {
                back()
            }
        }
    }
}
