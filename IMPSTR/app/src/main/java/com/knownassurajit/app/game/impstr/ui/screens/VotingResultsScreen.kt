package com.knownassurajit.app.game.impstr.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knownassurajit.app.game.impstr.ui.components.KeepScreenOn
import com.knownassurajit.app.game.impstr.ui.viewmodel.GameViewModel
import com.knownassurajit.app.game.impstr.ui.viewmodel.PlayerState

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun VotingResultsScreen(
    viewModel: GameViewModel,
    onVoteAgain: () -> Unit,
    onEndGame: () -> Unit,
    onBackToLobby: () -> Unit,
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val eliminatedPlayers = uiState.eliminatedInCurrentRound
    val view = androidx.compose.ui.platform.LocalView.current
    KeepScreenOn()
    val isGameOver = uiState.winner != null

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
                onBackToLobby()
            },
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            // Header
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
            ) {
                Text(
                    text = "VOTING RESULTS",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (eliminatedPlayers.isEmpty()) {
                    Text(
                        text = "Nobody got\nkicked out",
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                        lineHeight = MaterialTheme.typography.displaySmall.lineHeight.times(0.9f),
                    )
                } else {
                    Text(
                        text = "${eliminatedPlayers.size} Player(s)\nEjected",
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                        lineHeight = MaterialTheme.typography.displaySmall.lineHeight.times(0.9f),
                    )
                }
            }

            // Main Content Area
            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Timer Circle (Matches DiscussionScreen)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(280.dp),
                ) {
                    ElevatedCard(
                        modifier = Modifier.size(240.dp),
                        shape = androidx.compose.foundation.shape.CircleShape,
                        colors =
                            CardDefaults.elevatedCardColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                            ),
                        elevation =
                            CardDefaults.elevatedCardElevation(
                                defaultElevation = 4.dp,
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

                Spacer(modifier = Modifier.height(32.dp))

                if (eliminatedPlayers.isNotEmpty()) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        eliminatedPlayers.forEach { player ->
                            EliminatedPlayerCard(player)
                        }
                    }
                } else {
                    Text(
                        text = "Skipped vote",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }

            // Bottom Action Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp,
            ) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    if (!isGameOver) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            FilledTonalButton(
                                onClick = {
                                    view.performHapticFeedback(android.view.HapticFeedbackConstants.CLOCK_TICK)
                                    viewModel.startNextVotingRound()
                                    onVoteAgain()
                                },
                                modifier =
                                    Modifier
                                        .weight(1f)
                                        .height(56.dp),
                                shape = MaterialTheme.shapes.medium,
                                colors =
                                    androidx.compose.material3.ButtonDefaults.filledTonalButtonColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    ),
                            ) {
                                Text("Next Round", fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    view.performHapticFeedback(android.view.HapticFeedbackConstants.CONFIRM)
                                    viewModel.endGame()
                                    onEndGame()
                                },
                                modifier =
                                    Modifier
                                        .weight(1f)
                                        .height(56.dp),
                                shape = MaterialTheme.shapes.medium,
                            ) {
                                Text("End Game", fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        FilledTonalButton(
                            onClick = {
                                view.performHapticFeedback(android.view.HapticFeedbackConstants.CONFIRM)
                                viewModel.proceedFromResults()
                                onEndGame()
                            },
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                            shape = MaterialTheme.shapes.medium,
                        ) {
                            Text("See Game Results", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EliminatedPlayerCard(player: PlayerState) {
    val isImposter = player.isImposter
    val backgroundColor = if (isImposter) com.knownassurajit.app.game.impstr.ui.theme.GameColors.CrewmateGreen else MaterialTheme.colorScheme.errorContainer
    val contentColor = if (isImposter) Color.White else MaterialTheme.colorScheme.onErrorContainer

    ElevatedCard(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(100.dp),
        shape = MaterialTheme.shapes.large,
        colors =
            CardDefaults.elevatedCardColors(
                containerColor = backgroundColor,
            ),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = player.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = contentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = if (isImposter) "Imposter identified" else "Was not an Imposter",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = contentColor.copy(alpha = 0.9f),
                )
            }
        }
    }
}
