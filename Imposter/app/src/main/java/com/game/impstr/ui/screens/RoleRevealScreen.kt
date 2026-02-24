package com.game.impstr.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RoleRevealScreen(
    viewModel: com.game.impstr.ui.viewmodel.GameViewModel,
    onNext: () -> Unit,
    onBack: () -> Unit,
) {
    val uiState = viewModel.uiState.collectAsState().value

    // We maintain local state for animation triggering to ensure smooth UI updates
    var isFlipped by remember { mutableStateOf(false) }
    var isTransitioning by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    // val view = androidx.compose.ui.platform.LocalView.current // Unused if haptics removed

    val currentIndex = uiState.currentRevealPlayerIndex
    val currentPlayer = uiState.players.getOrNull(currentIndex) ?: return

    // Look ahead for smooth transition
    val nextPlayerName = uiState.players.getOrNull(currentIndex + 1)?.name ?: ""
    val currentPlayerName = if (isTransitioning) nextPlayerName else currentPlayer.name

    val isImposter = currentPlayer.isImposter
    val secretWord = uiState.secretWord
    val category = uiState.category

    val isLastPlayer = currentIndex == uiState.players.size - 1
    val progress = (currentIndex + 1).toFloat() / uiState.players.size.toFloat()

    var showHelpDialog by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }

    // Back Handler
    androidx.activity.compose.BackHandler {
        showExitDialog = true
    }

    if (showHelpDialog) {
        com.game.impstr.ui.components.HelpDialog(onDismiss = { showHelpDialog = false })
    }

    if (showExitDialog) {
        com.game.impstr.ui.components.ExitConfirmationDialog(
            onDismiss = { showExitDialog = false },
            onGoToLobby = {
                showExitDialog = false
                viewModel.resetGame()
                // Assuming onBack navigates back to Home/Lobby
                onBack()
            },
        )
    }

    val gradientStart = if (isFlipped && isImposter) Color(0xFF450a0a) else Color(0xFF1e1b4b)
    val gradientEnd = if (isFlipped && isImposter) Color(0xFF000000) else Color(0xFF0f172a)

    val bgBrush =
        Brush.verticalGradient(
            colors = listOf(gradientStart, gradientEnd),
        )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = { showExitDialog = true }) {
                    Icon(
                        Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground,
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "PLAYER ${currentIndex + 1}/${uiState.players.size}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                    )
                }
                IconButton(onClick = { showHelpDialog = true }) {
                    Icon(
                        Icons.Rounded.Info,
                        contentDescription = "Help",
                        tint = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }

            // Progress Indicator
            Spacer(modifier = Modifier.height(16.dp))

            val animatedProgress by animateFloatAsState(
                targetValue = progress,
                animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
                label = "progress",
            )

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(8.dp) // Slightly taller for expressive
                        .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round,
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Instruction Text
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                // Fixed height to prevent layout shift
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isFlipped) "Memorize your secret" else "Pass the phone to",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // Reserve space for name even if moved
                    if (!isFlipped) {
                        Text(
                            text = currentPlayerName,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    } else {
                        // Invisible text to maintain height
                        Text(
                            text = currentPlayerName,
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.Transparent,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                        )
                    }
                    Text(
                        text = "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 3D Flip Card
            com.game.impstr.ui.components.FlipCard(
                isFlipped = isFlipped,
                onFlip = {
                    if (!isFlipped) {
                        isFlipped = true
                    }
                },
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                front = {
                    Card(
                        modifier = Modifier.fillMaxSize(),
                        shape = MaterialTheme.shapes.extraLarge,
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    ) {
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxSize()
                                    .background(bgBrush)
                                    .padding(24.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier =
                                        Modifier
                                            .size(96.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.05f)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        Icons.Rounded.Lock,
                                        contentDescription = null,
                                        tint = Color.White.copy(alpha = 0.7f),
                                        modifier = Modifier.size(48.dp),
                                    )
                                }
                                Spacer(modifier = Modifier.height(24.dp))
                                Text(
                                    "TAP TO REVEAL",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White.copy(alpha = 0.6f),
                                    letterSpacing = 2.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                },
                back = {
                    Card(
                        modifier =
                            Modifier
                                .fillMaxSize(),
                        shape = MaterialTheme.shapes.extraLarge,
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    ) {
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxSize()
                                    .background(bgBrush)
                                    .padding(24.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    if (isImposter) "YOU ARE THE" else "THE SECRET WORD IS",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.5f),
                                    letterSpacing = 2.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    if (isImposter) "IMPOSTER" else secretWord,
                                    style = MaterialTheme.typography.displayMedium,
                                    color = if (isImposter) Color(0xFFef4444) else Color.White,
                                    fontWeight = FontWeight.Black,
                                    textAlign = TextAlign.Center,
                                )
                                Spacer(modifier = Modifier.height(32.dp))
                                Surface(
                                    shape = MaterialTheme.shapes.small,
                                    color = Color.White.copy(alpha = 0.1f),
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Icon(
                                            Icons.Rounded.Lock,
                                            contentDescription = null,
                                            tint = Color.White.copy(alpha = 0.6f),
                                            modifier = Modifier.size(16.dp),
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            category.uppercase(),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.White.copy(alpha = 0.6f),
                                            fontWeight = FontWeight.Bold,
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Action Button
            FilledTonalButton(
                onClick = {
                    if (isFlipped) {
                        if (isLastPlayer) {
                            onNext()
                        } else {
                            // Flip back first
                            isFlipped = false
                            isTransitioning = true // Update text immediately
                            scope.launch {
                                // Wait for flip to complete (400ms) then change player
                                delay(400)
                                viewModel.nextPlayerReveal()
                                isTransitioning = false
                            }
                        }
                    } else {
                        // Trigger flip if button clicked instead of card
                        isFlipped = true
                    }
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors =
                    androidx.compose.material3.ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
            ) {
                Text(
                    text =
                        if (isFlipped) {
                            if (isLastPlayer) "Start Discussion" else "Next Player"
                        } else {
                            "Reveal Role"
                        },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Rounded.Check, contentDescription = null)
            }
        }
    }
}
