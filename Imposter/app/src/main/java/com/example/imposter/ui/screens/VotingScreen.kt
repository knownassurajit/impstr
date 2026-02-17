package com.example.imposter.ui.screens

import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.example.imposter.ui.theme.CardPurple
import com.example.imposter.ui.theme.CardRed
import com.example.imposter.ui.theme.CardTeal
import com.example.imposter.ui.theme.CardYellow

data class Player(
    val name: String,
    val color: Color,
    val status: String,
    val isImposter: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VotingScreen(
    viewModel: com.example.imposter.ui.viewmodel.GameViewModel,
    onVoteConfirmed: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsState().value
    var selectedPlayers by remember { mutableStateOf<Set<String>>(emptySet()) }
    val view = androidx.compose.ui.platform.LocalView.current

    // Auto-navigate to result when phase changes
    LaunchedEffect(uiState.phase) {
        if (uiState.phase == com.example.imposter.ui.viewmodel.GamePhase.VOTING_RESULTS) {
             onVoteConfirmed()
        }
    }
    
    val players = uiState.players.mapIndexed { index, p -> 
        Player(
            name = p.name, 
            color = when(index % 4) {
                0 -> CardYellow
                1 -> CardPurple
                2 -> CardRed
                else -> CardTeal
            },

            status = if (p.isEliminated) "Eliminated" else "Alive",
            isImposter = p.isImposter
        )
    }
    // Calculate max selections based on remaining imposters
    // activeImpostersCount is not used as we enforce 1 selection for now.
    
    // Ensure at least 1 selection is allowed even if active count is weird, unless 0 players left.
    // Host logic: Host selects who gets eliminated.
    // If activeImpostersCount is e.g. 2, host can eliminate 2 people?
    // User requirement: "vote again with the remaining players if enough players are available. if host selects wrong player then the player will be eliminated"
    // This implies single elimination per vote usually? Or maxSelections = 1?
    // "majority of selection will reduce to one" -> usually 1 per round.
    // Let's set allowed selections to 1 for now to simplify, as "Vote Again" handles the loop.
    val activeImpostersCount = uiState.players.count { !it.isEliminated && it.isImposter }
    // Host selection logic: Allow selecting up to total number of imposters (min 1)
    val maxSelections = if (activeImpostersCount > 0) activeImpostersCount else 1

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "VOTING PHASE",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Who is the\nImposter?",
                            style = MaterialTheme.typography.displaySmall,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold,
                            lineHeight = MaterialTheme.typography.displaySmall.lineHeight.times(0.9f)
                        )
                    }
                    

                }
            }

            // Players Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 24.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(players) { player ->
                    val isSelected = selectedPlayers.contains(player.name)
                    
                    VoteCard(
                        player = player, 
                        isSelected = isSelected,
                        isEnabled = player.status != "Eliminated", // Disable if eliminated
                        onClick = { 
                            view.performHapticFeedback(android.view.HapticFeedbackConstants.CLOCK_TICK)
                            selectedPlayers = if (isSelected) {
                                selectedPlayers - player.name
                            } else {
                                // Enforce limit
                                if (selectedPlayers.size < maxSelections) {
                                    selectedPlayers + player.name
                                } else {
                                    // Make some feedback? Sarcastic toast?
                                    selectedPlayers
                                }
                            }
                        }
                    )
                }
            }

            // Bottom Action Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Skip Vote Button
                    OutlinedButton(
                        onClick = { viewModel.castVote(listOf("SKIP")) },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Skip Vote", fontWeight = FontWeight.Bold)
                    }

                    // Submit Vote Button
                    FilledTonalButton(
                        onClick = { 
                            if (selectedPlayers.isNotEmpty()) {
                                view.performHapticFeedback(android.view.HapticFeedbackConstants.CONFIRM)
                                viewModel.castVote(selectedPlayers.toList())
                            }
                        },
                        enabled = selectedPlayers.isNotEmpty(),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Submit Vote", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}


@Composable
fun VoteCard(player: Player, isSelected: Boolean, isEnabled: Boolean = true, onClick: () -> Unit) {
    val elevation by animateDpAsState(
        targetValue = if (isSelected) 6.dp else 2.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "elevation"
    )

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .clickable(enabled = isEnabled, onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (!isEnabled) {
                if (player.isImposter) Color(0xFF4CAF50) // Green for Imposter
                else Color(0xFFEF5350) // Red for Crewmate
            } else {
                player.color
            }
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = elevation
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .then(if (!isEnabled) Modifier.alpha(0.6f) else Modifier) // Reduced opacity for disabled
        ) {
            // Selection Checkmark - Only show if enabled/selectable
            if (isEnabled) {
                Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary
                        else Color.Black.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        Icons.Rounded.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

            // Player Info
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        player.name.firstOrNull()?.toString() ?: "?",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.Black.copy(alpha = 0.8f)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = player.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black.copy(alpha = 0.9f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            // Eliminated Cross Overlay - Removed
        }
    }
}
