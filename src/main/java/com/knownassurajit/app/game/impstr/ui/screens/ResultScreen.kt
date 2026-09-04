package com.knownassurajit.app.game.impstr.ui.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.knownassurajit.app.game.impstr.R
import com.knownassurajit.app.game.impstr.ui.ImpstrTestTags
import com.knownassurajit.app.game.impstr.ui.components.ImpstrPrimaryButton
import com.knownassurajit.app.game.impstr.ui.theme.Corners
import com.knownassurajit.app.game.impstr.ui.theme.Dimens
import com.knownassurajit.app.game.impstr.ui.theme.GameColors
import kotlinx.coroutines.delay

@Composable
fun ResultScreen(
    viewModel: com.knownassurajit.app.game.impstr.ui.viewmodel.GameViewModel,
    onPlayAgain: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val imposters = uiState.imposterNames
    val winner = uiState.winner
    val isCrewmatesWin = winner == "Crewmates"

    val cardColor = if (isCrewmatesWin) GameColors.CrewmateGreen else GameColors.ImposterRed
    val bgGradient =
        if (isCrewmatesWin) {
            Brush.verticalGradient(
                colors =
                    listOf(
                        GameColors.WinGradientGreenStart.copy(alpha = 0.3f),
                        MaterialTheme.colorScheme.background,
                    ),
            )
        } else {
            Brush.verticalGradient(
                colors =
                    listOf(
                        GameColors.WinGradientRedStart.copy(alpha = 0.3f),
                        MaterialTheme.colorScheme.background,
                    ),
            )
        }

    // Animation state
    var isVisible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec =
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium,
            ),
        label = "card_scale",
    )

    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    // Back Handler
    var showExitDialog by remember { mutableStateOf(false) }
    androidx.activity.compose.BackHandler {
        showExitDialog = true
    }

    if (showExitDialog) {
        com.knownassurajit.app.game.impstr.ui.components.ExitConfirmationDialog(
            onDismiss = { showExitDialog = false },
            onGoToLobby = {
                showExitDialog = false
                onPlayAgain() // Resets and goes home
            },
            showResumeButton = false,
            confirmText = "Close Game",
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(bgGradient),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                // Scrollable content
                Column(
                    modifier =
                        Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = Dimens.ScreenHorizontal)
                            .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                ) {
                    Spacer(modifier = Modifier.height(Dimens.ScreenVertical))

                // Top-Middle Duration
                val totalMinutes = uiState.totalGameTime / 60
                val totalSeconds = uiState.totalGameTime % 60
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                ) {
                    Text(
                        text = "Duration: ${totalMinutes}m ${totalSeconds}s • Rounds: ${uiState.currentRoundNumber}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = Dimens.SpacingLg, vertical = Dimens.SpacingSm),
                    )
                }

                Spacer(modifier = Modifier.height(Dimens.SpacingXxl))

                // Result Card
                Card(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .scale(scale),
                    shape = MaterialTheme.shapes.extraLarge,
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                        ),
                    elevation =
                        CardDefaults.cardElevation(
                            defaultElevation = Dimens.ElevationMax,
                        ),
                ) {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(Dimens.SpacingXxl),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        // Icon
                        Box(
                            modifier =
                                Modifier
                                    .size(Dimens.AvatarLarge)
                                    .clip(CircleShape)
                                    .background(cardColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "😈",
                                style = MaterialTheme.typography.displayLarge,
                            )
                        }

                        Spacer(modifier = Modifier.height(Dimens.SpacingXl))

                        Text(
                            text = if (imposters.size > 1) stringResource(R.string.the_imposters_were) else stringResource(R.string.the_imposter_was),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                        )

                        Spacer(modifier = Modifier.height(Dimens.SpacingLg))

                        imposters.forEach { imposter ->
                            Text(
                                text = imposter,
                                style = MaterialTheme.typography.displaySmall,
                                color = cardColor,
                                fontWeight = FontWeight.Black,
                                textAlign = TextAlign.Center,
                            )
                            if (imposters.indexOf(imposter) != imposters.lastIndex) {
                                Spacer(modifier = Modifier.height(Dimens.SpacingSm))
                            }
                        }

                        Spacer(modifier = Modifier.height(Dimens.SpacingXl))

                        Spacer(modifier = Modifier.height(Dimens.SpacingXl))

                        // Winner Badge
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            color = cardColor.copy(alpha = 0.15f),
                        ) {
                            Text(
                                text = if (isCrewmatesWin) stringResource(R.string.crewmates_win) else stringResource(R.string.imposter_wins),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = cardColor,
                                modifier = Modifier.padding(horizontal = Dimens.SpacingXl, vertical = Dimens.SpacingMd),
                            )
                        }

                        Spacer(modifier = Modifier.height(Dimens.SpacingXl))

                        // ALWAYS Show Secret Word & Category
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column(
                                modifier = Modifier.padding(Dimens.CardPadding),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(
                                    text = stringResource(R.string.secret_word_was),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                    Spacer(modifier = Modifier.height(Dimens.SpacingXs))
                                Text(
                                    text = uiState.secretWord,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )

                                // In stealth mode, also reveal the imposter's decoy word
                                if (uiState.isStealthMode && uiState.imposterWord.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(Dimens.SpacingMd))
                                    Text(
                                        text = stringResource(R.string.imposter_word_was),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = GameColors.ImposterRedDim,
                                    )
                                    Spacer(modifier = Modifier.height(Dimens.SpacingXs))
                                    Text(
                                        text = uiState.imposterWord,
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = GameColors.ImposterRed,
                                    )
                                }

                                Spacer(modifier = Modifier.height(Dimens.SpacingSm))
                                Text(
                                    text = stringResource(R.string.category_prefix, uiState.category),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }

                if (uiState.eliminationHistory.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(Dimens.SpacingXl))

                    // Separate Card for Elimination History
                    Card(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .scale(scale),
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.ElevationSlight),
                    ) {
                        Column(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(Dimens.SpacingXl),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = stringResource(R.string.elimination_history),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Spacer(modifier = Modifier.height(Dimens.SpacingLg))
                            uiState.eliminationHistory.forEach { record ->
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                shape = MaterialTheme.shapes.small,
                                    modifier = Modifier.fillMaxWidth().padding(vertical = Dimens.SpacingXs),
                                ) {
                                    Text(
                                        text = "${record.eliminationOrder}. ${record.player.name} (Round ${record.roundNumber})",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(Dimens.CardPaddingTight),
                                        textAlign = TextAlign.Center,
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.SpacingXxl))
                }

                // Pinned Bottom Bar — Play Again
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                    tonalElevation = Dimens.ElevationHigh,
                    shape = Corners.BottomBar,
                ) {
                    ImpstrPrimaryButton(
                        text = stringResource(R.string.play_again),
                        onClick = onPlayAgain,
                        modifier =
                            Modifier
                                .padding(Dimens.BottomBarPadding)
                                .testTag(ImpstrTestTags.PlayAgain),
                    )
                }
            }
        }
    }
}
