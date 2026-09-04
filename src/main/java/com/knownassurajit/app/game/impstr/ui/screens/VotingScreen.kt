package com.knownassurajit.app.game.impstr.ui.screens

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.knownassurajit.app.game.impstr.R
import com.knownassurajit.app.game.impstr.ui.ImpstrTestTags
import com.knownassurajit.app.game.impstr.ui.components.ImposterCard
import com.knownassurajit.app.game.impstr.ui.components.KeepScreenOn
import com.knownassurajit.app.game.impstr.ui.theme.*
import com.knownassurajit.app.game.impstr.ui.viewmodel.GamePhase
import com.knownassurajit.app.game.impstr.ui.viewmodel.GameViewModel

data class Player(
    val id: String,
    val name: String,
    val color: Color,
    val status: String,
    val isImposter: Boolean,
    val isEliminated: Boolean,
)

@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VotingScreen(
    viewModel: com.knownassurajit.app.game.impstr.ui.viewmodel.GameViewModel,
    onVoteConfirmed: () -> Unit,
    onGameEnd: () -> Unit,
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    var selectedPlayerIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    val view = androidx.compose.ui.platform.LocalView.current
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

    val players =
        uiState.players.mapIndexed { index, p ->
            Player(
                id = p.id,
                name = p.name,
                color = PlayerColors[index % PlayerColors.size],
                status = if (p.isEliminated) stringResource(R.string.status_eliminated) else stringResource(R.string.status_alive),
                isImposter = p.isImposter,
                isEliminated = p.isEliminated,
            )
        }

    // Limit selection to the number of active imposters remaining
    val activeImpostersCount = uiState.players.count { !it.isEliminated && it.isImposter }
    val maxSelections = kotlin.math.max(1, activeImpostersCount)

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
                    text = stringResource(R.string.voting_phase),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(Dimens.SpacingXs))
                Text(
                    text = if (maxSelections > 1) stringResource(R.string.voting_select_many, maxSelections) else stringResource(R.string.voting_who),
                    style = MaterialTheme.typography.displayMedium, // Poppins
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                )
            }

            // Players Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingLg),
                verticalArrangement = Arrangement.spacedBy(Dimens.SpacingLg),
                contentPadding = PaddingValues(horizontal = Dimens.SheetPadding, vertical = Dimens.SpacingSm),
                modifier = Modifier.weight(1f),
            ) {
                items(players) { player ->
                    val isSelected = selectedPlayerIds.contains(player.id)

                    VoteCard(
                        player = player,
                        isSelected = isSelected,
                        onClick = {
                            view.performHapticFeedback(android.view.HapticFeedbackConstants.CLOCK_TICK)
                            selectedPlayerIds =
                                if (isSelected) {
                                    selectedPlayerIds - player.id
                                } else {
                                    if (selectedPlayerIds.size < maxSelections) {
                                        selectedPlayerIds + player.id
                                    } else {
                                        // If we reached max selection, replace the first selected one with new one (or just do nothing? user requirement says "minimum one to totally number of imposters")
                                        // Let's make it a toggle behavior: if full, remove oldest? No, probably safer to just not add, or clear and add.
                                        // But standard multi-select usually blocks or replaces.
                                        // Given it's a game, maybe just block?
                                        // actually, let's just replace the oldest one if max is 1 (radio button style),
                                        // but if max > 1, maybe just don't allow more?
                                        // The user said "total numbers of players can be voted will be minimum one to total number of imposters configured".
                                        // Let's stick to "if < max, add".
                                        if (maxSelections == 1) {
                                            setOf(player.id)
                                        } else {
                                            selectedPlayerIds // Do nothing if full
                                        }
                                    }
                                }
                        },
                    )
                }
            }

            // Bottom Action Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                tonalElevation = Dimens.ElevationMax,
                shape = Corners.BottomBar,
            ) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(Dimens.SheetPadding),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingLg),
                ) {
                    OutlinedButton(
                        onClick = {
                            val currentState = viewModel.uiState.value
                            if (currentState.phase == GamePhase.HOST_VOTING) {
                                viewModel.castVote(listOf("SKIP"))
                                onVoteConfirmed()
                            }
                        },
                        modifier =
                            Modifier
                                .weight(1f)
                                .height(Dimens.ButtonHeight)
                                .testTag(ImpstrTestTags.SkipVote),
                        shape = MaterialTheme.shapes.medium,
                        border = androidx.compose.foundation.BorderStroke(Dimens.BorderWidth, MaterialTheme.colorScheme.outline),
                    ) {
                        Text(stringResource(R.string.skip_vote), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            if (selectedPlayerIds.isNotEmpty()) {
                                val currentState = viewModel.uiState.value
                                if (currentState.phase == GamePhase.HOST_VOTING) {
                                    view.performHapticFeedback(android.view.HapticFeedbackConstants.CONFIRM)
                                    viewModel.castVote(selectedPlayerIds.toList())
                                    onVoteConfirmed()
                                }
                            }
                        },
                        enabled = selectedPlayerIds.isNotEmpty(),
                        modifier =
                            Modifier
                                .weight(1f)
                                .height(Dimens.ButtonHeight)
                                .testTag(ImpstrTestTags.SubmitVote),
                        shape = MaterialTheme.shapes.medium,
                    ) {
                        Text(stringResource(R.string.submit_vote), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun VoteCard(
    player: Player,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    // If eliminated -> Filled Card (Disabled/Grayed or Status Color)
    // If Alive -> Outlined Card (Active)

    val isEliminated = player.isEliminated
    val isEnabled = !isEliminated

    // Animate Border/Container Color
    val containerColor by animateColorAsState(
        targetValue =
            if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                if (isEliminated) {
                    MaterialTheme.colorScheme.surfaceVariant
                } else {
                    MaterialTheme.colorScheme.surface
                }
            },
        label = "cardContainer",
    )

    val borderColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
        label = "cardBorder",
    )

    ImposterCard(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(Dimens.VoteCardHeight),
        isOutlined = !isSelected && !isEliminated, // Outlined only if active and not selected
        containerColor = containerColor,
        borderColor = borderColor,
        onClick = if (isEnabled) onClick else null,
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(Dimens.CardPadding)
                    .alpha(if (isEliminated) 0.6f else 1f),
        ) {
            // Selection Checkmark
            if (isSelected) {
                Box(
                    modifier =
                        Modifier
                            .align(Alignment.TopEnd)
                            .size(Dimens.AvatarSmall)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Rounded.Check,
                        contentDescription = stringResource(R.string.submit_vote),
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(Dimens.IconSizeSm),
                    )
                }
            }

            // Status Badge for Eliminated
            if (isEliminated) {
                Surface(
                    modifier = Modifier.align(Alignment.TopStart),
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = Corners.Badge,
                ) {
                    Text(
                        stringResource(R.string.status_eliminated),
                        modifier = Modifier.padding(horizontal = Dimens.SpacingSm, vertical = Dimens.OpticalInset),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            // Player Info
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize(),
            ) {
                // Avatar
                Box(
                    modifier =
                        Modifier
                            .size(Dimens.AvatarLarge)
                            .clip(CircleShape)
                            .background(player.color),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        player.name.firstOrNull()?.toString() ?: "?",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineMedium,
                        color = GameColors.OnVibrant,
                    )
                }
                Spacer(modifier = Modifier.height(Dimens.SpacingLg))
                Text(
                    text = player.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
