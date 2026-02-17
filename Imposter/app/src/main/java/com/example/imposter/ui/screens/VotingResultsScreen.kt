package com.example.imposter.ui.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.imposter.ui.theme.CardPurple
import com.example.imposter.ui.theme.CardRed
import com.example.imposter.ui.theme.CardTeal
import com.example.imposter.ui.theme.CardYellow
import com.example.imposter.ui.viewmodel.GameViewModel
import com.example.imposter.ui.viewmodel.PlayerState

@Composable
fun VotingResultsScreen(
    viewModel: GameViewModel,
    onVoteAgain: () -> Unit,
    onEndGame: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsState().value
    val eliminatedPlayers = uiState.eliminatedInCurrentRound
    val view = androidx.compose.ui.platform.LocalView.current
    val isGameOver = uiState.winner != null

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
                Text(
                    text = "VOTING RESULTS",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (eliminatedPlayers.isEmpty()) {
                    Text(
                        text = "Nobody got\nkicked out",
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                        lineHeight = MaterialTheme.typography.displaySmall.lineHeight.times(0.9f)
                    )
                } else {
                    Text(
                        text = "${eliminatedPlayers.size} Player(s)\nEjected",
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                        lineHeight = MaterialTheme.typography.displaySmall.lineHeight.times(0.9f)
                    )
                }
            }

            // Eliminated Players Grid
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                 if (eliminatedPlayers.isNotEmpty()) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(1),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(eliminatedPlayers) { player ->
                            EliminatedPlayerCard(player)
                        }
                    }
                } else {
                     Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                       Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Skipped vote",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "The game continues...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha=0.7f)
                            )
                       }
                    }
                }
            }

            // Bottom Action Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (!isGameOver) {
                         Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            FilledTonalButton(
                                onClick = {
                                    view.performHapticFeedback(android.view.HapticFeedbackConstants.CLOCK_TICK)
                                    viewModel.startNextVotingRound()
                                    onVoteAgain()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = androidx.compose.material3.ButtonDefaults.filledTonalButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            ) {
                                Text("Vote Again", fontWeight = FontWeight.Bold)
                            }
                            
                            OutlinedButton(
                                onClick = {
                                     view.performHapticFeedback(android.view.HapticFeedbackConstants.CONFIRM)
                                     viewModel.endGame()
                                     onEndGame()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp)
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp)
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
    val backgroundColor = if (isImposter) Color(0xFF4CAF50) else MaterialTheme.colorScheme.errorContainer
    val contentColor = if (isImposter) Color.White else MaterialTheme.colorScheme.onErrorContainer
    
     ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = backgroundColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
             Column(
                verticalArrangement = Arrangement.Center
            ) {
                 Text(
                    text = player.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                 Text(
                    text = if (isImposter) "Imposter identified" else "Was not an Imposter",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = contentColor.copy(alpha = 0.9f)
                )
            }
        }
    }
}
