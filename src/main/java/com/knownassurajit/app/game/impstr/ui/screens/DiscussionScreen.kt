package com.knownassurajit.app.game.impstr.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knownassurajit.app.game.impstr.ui.components.KeepScreenOn
import com.knownassurajit.app.game.impstr.ui.theme.Dimens
import com.knownassurajit.app.game.impstr.ui.theme.Anim

@Composable
fun DiscussionScreen(
    viewModel: com.knownassurajit.app.game.impstr.ui.viewmodel.GameViewModel,
    onVotingStart: () -> Unit,
    onGameEnd: () -> Unit,
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    // Auto-navigate removed in favor of direct click

    // Keep screen on during Discussion
    KeepScreenOn()

    var showExitDialog by remember { mutableStateOf(false) }

    androidx.activity.compose.BackHandler {
        showExitDialog = true
    }

    if (showExitDialog) {
        com.knownassurajit.app.game.impstr.ui.components.ExitConfirmationDialog(
            onDismiss = { showExitDialog = false },
            onGoToLobby = {
                showExitDialog = false
                viewModel.resetGame()
                onGameEnd()
            },
        )
    }

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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "DISCUSSION PHASE",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                )
            }
            Spacer(modifier = Modifier.height(Dimens.SpacingSm))
            Text(
                text = "Talk with your friends to find the imposter.\nKeep it civil!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.weight(1f))

            // Timer Circle
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(Dimens.TimerCircleOuter),
            ) {
                // Timer Display Card
                ElevatedCard(
                    modifier = Modifier.size(Dimens.TimerCircleInner),
                    shape = CircleShape,
                    colors =
                        CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                        ),
                    elevation =
                        CardDefaults.elevatedCardElevation(
                            defaultElevation = Dimens.ElevationSlight,
                        ),
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            com.knownassurajit.app.game.impstr.ui.components.TimerDisplay(
                                seconds = uiState.totalGameTime,
                                style = MaterialTheme.typography.displayLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = "TOTAL TIME",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp,
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Proceed Button
            FilledTonalButton(
                onClick = {
                    val currentState = viewModel.uiState.value
                    if (currentState.phase == com.knownassurajit.app.game.impstr.ui.viewmodel.GamePhase.DISCUSSION) {
                        viewModel.startVoting()
                        onVotingStart()
                    }
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(Dimens.ButtonHeight),
                shape = MaterialTheme.shapes.medium,
                colors =
                    ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
            ) {
                Text(
                    text = "Proceed to Voting",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.height(Dimens.SpacingLg))
        }
    }
}
