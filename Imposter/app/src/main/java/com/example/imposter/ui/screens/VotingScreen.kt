package com.example.imposter.ui.screens

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.imposter.ui.components.ImposterCard
import com.example.imposter.ui.components.KeepScreenOn
import com.example.imposter.ui.theme.*
import com.example.imposter.ui.viewmodel.GamePhase
import com.example.imposter.ui.viewmodel.GameViewModel

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
    viewModel: com.example.imposter.ui.viewmodel.GameViewModel,
    onVoteConfirmed: () -> Unit,
    onGameEnd: () -> Unit,
) {
    val uiState = viewModel.uiState.collectAsState().value
    var selectedPlayerIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    val view = androidx.compose.ui.platform.LocalView.current
    KeepScreenOn()

    var showExitDialog by remember { mutableStateOf(false) }

    androidx.activity.compose.BackHandler {
        showExitDialog = true
    }

    if (showExitDialog) {
        com.example.imposter.ui.components.ExitConfirmationDialog(
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
                status = if (p.isEliminated) "Eliminated" else "Alive",
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
                        .padding(24.dp),
            ) {
                Text(
                    text = "VOTING PHASE",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary, // Pop color
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (maxSelections > 1) "Select $maxSelections\nto Eliminate" else "Who is the\nImposter?",
                    style = MaterialTheme.typography.displayMedium, // Poppins
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    lineHeight = MaterialTheme.typography.displayMedium.fontSize.times(1.1f),
                )
            }

            // Players Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
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
                color = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 8.dp,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            ) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    // Skip Vote Button
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
                                .height(56.dp),
                        shape = MaterialTheme.shapes.medium,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    ) {
                        Text("Skip Vote", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }

                    // Submit Vote Button
                    FilledTonalButton(
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
                                .height(56.dp),
                        shape = MaterialTheme.shapes.medium,
                        colors =
                            ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                    ) {
                        Text("Submit Vote", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
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
                .height(180.dp),
        isOutlined = !isSelected && !isEliminated, // Outlined only if active and not selected
        containerColor = containerColor,
        borderColor = borderColor,
        onClick = if (isEnabled) onClick else null,
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .alpha(if (isEliminated) 0.6f else 1f),
        ) {
            // Selection Checkmark
            if (isSelected) {
                Box(
                    modifier =
                        Modifier
                            .align(Alignment.TopEnd)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Rounded.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            // Status Badge for Eliminated
            if (isEliminated) {
                Surface(
                    modifier = Modifier.align(Alignment.TopStart),
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(4.dp),
                ) {
                    Text(
                        "ELIMINATED",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
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
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(player.color),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        player.name.firstOrNull()?.toString() ?: "?",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.Black.copy(alpha = 0.7f),
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
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
