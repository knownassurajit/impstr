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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.knownassurajit.app.game.impstr.R
import com.knownassurajit.app.game.impstr.ui.ImpstrTestTags
import com.knownassurajit.app.game.impstr.ui.components.KeepScreenOn
import com.knownassurajit.app.game.impstr.ui.theme.Dimens
import com.knownassurajit.app.game.impstr.ui.theme.GameColors
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
                        .padding(Dimens.SheetPadding),
            ) {
                Text(
                    text = stringResource(R.string.voting_results),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(Dimens.SpacingXs))
                if (eliminatedPlayers.isEmpty()) {
                    Text(
                        text = stringResource(R.string.nobody_kicked),
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                        lineHeight = MaterialTheme.typography.displaySmall.lineHeight.times(0.9f),
                    )
                } else {
                    Text(
                        text = stringResource(R.string.players_ejected, eliminatedPlayers.size),
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
                        .padding(horizontal = Dimens.SheetPadding)
                        .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Spacer(modifier = Modifier.height(Dimens.SpacingLg))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(Dimens.TimerCircleOuter),
                ) {
                    ElevatedCard(
                        modifier = Modifier.size(Dimens.TimerCircleInner),
                        shape = androidx.compose.foundation.shape.CircleShape,
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
                                    text = stringResource(R.string.total_time),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.SpacingXxl))

                if (eliminatedPlayers.isNotEmpty()) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMd),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        eliminatedPlayers.forEach { player ->
                            EliminatedPlayerCard(player)
                        }
                    }
                } else {
                    Text(
                        text = stringResource(R.string.skipped_vote),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Spacer(modifier = Modifier.height(Dimens.SpacingXxl))
            }

            // Bottom Action Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                tonalElevation = Dimens.ElevationSlight,
            ) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(Dimens.SheetPadding),
                    verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMd),
                ) {
                    if (!isGameOver) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingMd),
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
                                        .height(Dimens.ButtonHeight)
                                        .testTag(ImpstrTestTags.NextRound),
                                shape = MaterialTheme.shapes.medium,
                            ) {
                                Text(stringResource(R.string.next_round), fontWeight = FontWeight.Bold)
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
                                        .height(Dimens.ButtonHeight)
                                        .testTag(ImpstrTestTags.EndGame),
                                shape = MaterialTheme.shapes.medium,
                            ) {
                                Text(stringResource(R.string.end_game), fontWeight = FontWeight.Bold)
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
                                    .height(Dimens.ButtonHeight)
                                    .testTag(ImpstrTestTags.SeeResults),
                            shape = MaterialTheme.shapes.medium,
                        ) {
                            Text(stringResource(R.string.see_game_results), fontWeight = FontWeight.Bold)
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
    val backgroundColor = if (isImposter) GameColors.CrewmateGreen else MaterialTheme.colorScheme.errorContainer
    val contentColor = if (isImposter) Color.White else MaterialTheme.colorScheme.onErrorContainer

    ElevatedCard(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(Dimens.TouchTargetMin * 2),
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
                    .padding(Dimens.CardPadding),
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
                    text = if (isImposter) stringResource(R.string.imposter_identified) else stringResource(R.string.was_not_imposter),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = contentColor.copy(alpha = 0.9f),
                )
            }
        }
    }
}
