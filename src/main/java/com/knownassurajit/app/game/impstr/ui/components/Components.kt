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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.knownassurajit.app.game.impstr.ui.theme.Alpha
import com.knownassurajit.app.game.impstr.ui.theme.Corners
import com.knownassurajit.app.game.impstr.ui.theme.Dimens

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
        modifier = modifier.height(Dimens.ButtonHeight),
        shape = MaterialTheme.shapes.medium,
        color = containerColor,
        contentColor = contentColor,
        tonalElevation = Dimens.ElevationBase,
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(onClick = onMainClick)
                        .padding(start = Dimens.CardPadding, end = Dimens.SpacingSm),
                contentAlignment = Alignment.CenterStart,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    mainContent()
                }
            }

            Box(
                modifier =
                    Modifier
                        .width(Dimens.BorderWidth)
                        .height(Dimens.AvatarSmall)
                        .background(contentColor.copy(alpha = Alpha.Divider + 0.08f)),
            )

            Box(
                modifier =
                    Modifier
                        .width(Dimens.ButtonHeight)
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
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = Alpha.Scrim + 0.18f))
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
                        .width(Dimens.SideSheetWidth)
                        .clickable(enabled = false) {},
                shape = Corners.SideSheet,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                tonalElevation = Dimens.ElevationHigh,
            ) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(Dimens.SheetPadding),
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
        val border = BorderStroke(Dimens.BorderWidth, borderColor)

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

@Composable
fun ImpstrPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier =
            modifier
                .fillMaxWidth()
                .height(Dimens.ButtonHeight)
                .defaultMinSize(minHeight = Dimens.TouchTargetMin),
        shape = MaterialTheme.shapes.medium,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        if (icon != null) {
            Spacer(modifier = Modifier.width(Dimens.SpacingSm))
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(Dimens.IconSizeSm),
            )
        }
    }
}
