package com.knownassurajit.app.game.impstr.ui.screens

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.knownassurajit.app.game.impstr.R
import com.knownassurajit.app.game.impstr.ui.ImpstrTestTags
import com.knownassurajit.app.game.impstr.ui.theme.Anim
import com.knownassurajit.app.game.impstr.ui.theme.Dimens
import com.knownassurajit.app.game.impstr.ui.theme.GameColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RoleRevealScreen(
    viewModel: com.knownassurajit.app.game.impstr.ui.viewmodel.GameViewModel,
    onNext: () -> Unit,
    onBack: () -> Unit,
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

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
    val imposterWord = uiState.imposterWord
    val isStealth = uiState.isStealthMode
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
        com.knownassurajit.app.game.impstr.ui.components.HelpDialog(onDismiss = { showHelpDialog = false })
    }

    if (showExitDialog) {
        com.knownassurajit.app.game.impstr.ui.components.ExitConfirmationDialog(
            onDismiss = { showExitDialog = false },
            onGoToLobby = {
                showExitDialog = false
                viewModel.resetGame()
                // Assuming onBack navigates back to Home/Lobby
                onBack()
            },
        )
    }

    // In stealth mode, imposters see a normal card (no red gradient hint)
    val showRedGradient = isFlipped && isImposter && !isStealth
    val gradientStart = if (showRedGradient) GameColors.CardGradientRedStart else GameColors.CardGradientBlueStart
    val gradientEnd = if (showRedGradient) GameColors.CardGradientRedEnd else GameColors.CardGradientBlueEnd

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
                    .padding(Dimens.ScreenHorizontal),
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
                        contentDescription = stringResource(R.string.cd_back),
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
                        contentDescription = stringResource(R.string.cd_help),
                        tint = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }

            // Progress Indicator
            Spacer(modifier = Modifier.height(Dimens.SpacingLg))

            val animatedProgress by animateFloatAsState(
                targetValue = progress,
                animationSpec = tween(durationMillis = Anim.DurationMedium, easing = Anim.EmphasizedEasing),
                label = "progress",
            )

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(Dimens.ProgressHeight)
                        .clip(MaterialTheme.shapes.extraSmall),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round,
            )

            Spacer(modifier = Modifier.height(Dimens.SpacingXxl))

            // Instruction Text
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(Dimens.RevealHeroHeight),
                // Fixed height to prevent layout shift
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isFlipped) stringResource(R.string.reveal_memorize) else stringResource(R.string.reveal_pass_to),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.height(Dimens.SpacingSm))
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
                        modifier = Modifier.padding(top = Dimens.SpacingSm),
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimens.SpacingXxl))

            // 3D Flip Card
            com.knownassurajit.app.game.impstr.ui.components.FlipCard(
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
                        .padding(horizontal = Dimens.SpacingLg)
                        .testTag(ImpstrTestTags.RoleCard),
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
                                    .padding(Dimens.SheetPadding),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier =
                                        Modifier
                                            .size(Dimens.AvatarLarge)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.05f)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        Icons.Rounded.Lock,
                                        contentDescription = null,
                                        tint = Color.White.copy(alpha = 0.7f),
                                        modifier = Modifier.size(Dimens.AvatarMedium),
                                    )
                                }
                                Spacer(modifier = Modifier.height(Dimens.SpacingXl))
                                Text(
                                    stringResource(R.string.reveal_tap),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White.copy(alpha = 0.6f),
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
                                    .padding(Dimens.SheetPadding),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                // Determine card content based on stealth mode
                                val cardLabel: String
                                val cardWord: String
                                val cardWordColor: Color

                                if (isImposter && !isStealth) {
                                    cardLabel = stringResource(R.string.reveal_you_are)
                                    cardWord = stringResource(R.string.reveal_imposter)
                                    cardWordColor = GameColors.ImposterRed
                                } else if (isImposter && isStealth) {
                                    cardLabel = stringResource(R.string.reveal_secret_is)
                                    cardWord = imposterWord
                                    cardWordColor = Color.White
                                } else {
                                    cardLabel = stringResource(R.string.reveal_secret_is)
                                    cardWord = secretWord
                                    cardWordColor = Color.White
                                }

                                Text(
                                    cardLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.5f),
                                    fontWeight = FontWeight.Bold,
                                )
                                Spacer(modifier = Modifier.height(Dimens.SpacingLg))
                                Text(
                                    cardWord,
                                    style = MaterialTheme.typography.displayMedium,
                                    color = cardWordColor,
                                    fontWeight = FontWeight.Black,
                                    textAlign = TextAlign.Center,
                                )
                                Spacer(modifier = Modifier.height(Dimens.SpacingXxl))
                                Surface(
                                    shape = MaterialTheme.shapes.small,
                                    color = Color.White.copy(alpha = 0.1f),
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = Dimens.SpacingLg, vertical = Dimens.SpacingSm),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Icon(
                                            Icons.Rounded.Lock,
                                            contentDescription = null,
                                            tint = Color.White.copy(alpha = 0.6f),
                                            modifier = Modifier.size(Dimens.IconSizeXs),
                                        )
                                        Spacer(modifier = Modifier.width(Dimens.SpacingSm))
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

            Spacer(modifier = Modifier.height(Dimens.SpacingXxl))

            // Action Button
            val revealActionLabel =
                if (isFlipped) {
                    if (isLastPlayer) stringResource(R.string.action_start_discussion) else stringResource(R.string.action_next_player)
                } else {
                    stringResource(R.string.action_reveal_role)
                }
            FilledTonalButton(
                onClick = {
                    if (isFlipped) {
                        if (isLastPlayer) {
                            onNext()
                        } else {
                            isFlipped = false
                            isTransitioning = true
                            scope.launch {
                                delay(Anim.DurationCardFlip.toLong())
                                viewModel.nextPlayerReveal()
                                isTransitioning = false
                            }
                        }
                    } else {
                        isFlipped = true
                    }
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(Dimens.ButtonHeight)
                        .testTag(ImpstrTestTags.RevealPrimary),
                shape = MaterialTheme.shapes.medium,
                colors =
                    androidx.compose.material3.ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
            ) {
                Text(
                    text = revealActionLabel,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.width(Dimens.SpacingSm))
                Icon(Icons.Rounded.Check, contentDescription = null)
            }
        }
    }
}
