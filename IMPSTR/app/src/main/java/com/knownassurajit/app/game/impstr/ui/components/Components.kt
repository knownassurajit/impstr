package com.knownassurajit.app.game.impstr.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * Split Button Component
 * A button with a main action area and a segmented secondary action/edit area ("Pet Tool").
 */
@Composable
fun SplitButton(
    modifier: Modifier = Modifier,
    mainContent: @Composable RowScope.() -> Unit,
    onMainClick: () -> Unit,
    onSecondaryClick: () -> Unit,
    secondaryIcon: ImageVector,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    Surface(
        modifier = modifier.height(56.dp),
        shape = MaterialTheme.shapes.medium, // Expressive shape
        color = containerColor,
        contentColor = contentColor,
        tonalElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Main Action Area
            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(onClick = onMainClick)
                        .padding(start = 16.dp, end = 8.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    mainContent()
                }
            }

            // Separator
            Box(
                modifier =
                    Modifier
                        .width(1.dp)
                        .height(32.dp)
                        .background(contentColor.copy(alpha = 0.2f)),
            )

            // Secondary Action Area (Pet Tool)
            Box(
                modifier =
                    Modifier
                        .width(56.dp)
                        .fillMaxHeight()
                        .clickable(onClick = onSecondaryClick),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = secondaryIcon,
                    contentDescription = null,
                    tint = contentColor.copy(alpha = 0.8f),
                )
            }
        }
    }
}

/**
 * Modal Side Sheet
 * A full-height sheet that slides in from the right.
 */
@Composable
fun ModalSideSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
        exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
        label = "SideSheetAnimation",
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)) // Scrim
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDismiss,
                    ),
            contentAlignment = Alignment.CenterEnd, // Align to right
        ) {
            // Sheet Content
            Surface(
                modifier =
                    Modifier
                        .fillMaxHeight()
                        .width(320.dp) // Fixed width for side sheet
                        .clickable(enabled = false) {},
                // Prevent clicks passing through
                shape = RoundedCornerShape(topStart = 28.dp, bottomStart = 28.dp), // SideSheet specific
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
            ) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                ) {
                    content()
                }
            }
        }
    }
}

/**
 * Imposter Card
 * Wrapper for Cards that supports "Outlined" vs "Filled" styles easily.
 */
@Composable
fun ImposterCard(
    modifier: Modifier = Modifier,
    isOutlined: Boolean = true,
    onClick: (() -> Unit)? = null,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = MaterialTheme.shapes.large // Expressive shape

    if (isOutlined) {
        val colors = CardDefaults.outlinedCardColors(containerColor = containerColor)
        val border = BorderStroke(1.dp, borderColor)

        if (onClick != null) {
            OutlinedCard(
                onClick = onClick,
                modifier = modifier,
                shape = shape,
                colors = colors,
                border = border,
                content = content,
            )
        } else {
            OutlinedCard(
                modifier = modifier,
                shape = shape,
                colors = colors,
                border = border,
                content = content,
            )
        }
    } else {
        val colors = CardDefaults.cardColors(containerColor = containerColor)

        if (onClick != null) {
            Card(
                onClick = onClick,
                modifier = modifier,
                shape = shape,
                colors = colors,
                content = content,
            )
        } else {
            Card(
                modifier = modifier,
                shape = shape,
                colors = colors,
                content = content,
            )
        }
    }
}
