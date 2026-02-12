package com.example.imposter.ui.screens

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
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.imposter.ui.theme.CardGrey
import com.example.imposter.ui.theme.CardPurple
import com.example.imposter.ui.theme.CardRed
import com.example.imposter.ui.theme.CardTeal
import com.example.imposter.ui.theme.CardYellow
import com.example.imposter.ui.theme.ImposterTheme

data class Player(
    val name: String,
    val color: Color,
    val status: String
)

@Composable
fun VotingScreen(
    viewModel: com.example.imposter.ui.viewmodel.GameViewModel,
    onVoteConfirmed: () -> Unit
) {
    val uiState = androidx.compose.runtime.collectAsState(viewModel.uiState).value

    // Auto-navigate to result when phase changes
    androidx.compose.runtime.LaunchedEffect(uiState.phase) {
        if (uiState.phase == com.example.imposter.ui.viewmodel.GamePhase.RESULT) {
            onVoteConfirmed()
        }
    }

    val players = uiState.players.map { 
        Player(
            name = it.name, 
            color = when(it.name) {
                "Sarah (You)" -> CardYellow
                "Marcus" -> CardPurple
                "Elena" -> CardRed
                else -> CardTeal
            },
            status = "Alive" 
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Text(
                    text = "VOTING PHASE",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Who is the\nImposter?",
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    lineHeight = MaterialTheme.typography.displaySmall.lineHeight.times(0.9f)
                )
            }
            
            // Timer Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(32.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Timer,
                    contentDescription = null,
                    tint = Color(0xFFEF4444), // Red 500
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "0:45",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Actions Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { /* Confirm */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onBackground,
                    contentColor = MaterialTheme.colorScheme.background
                ),
                shape = RoundedCornerShape(32.dp),
                modifier = Modifier.weight(1.5f)
            ) {
                Text("Confirm Vote")
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(16.dp))
            }
            
            OutlinedButton(
                onClick = { /* Chat */ },
                shape = RoundedCornerShape(32.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("Chat (3)")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Players Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(players) { player ->
                VoteCard(player, onClick = { viewModel.castVote(player.name) })
            }
            
            // Skip Vote Card (Spans 2 columns)
            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                SkipVoteCard()
            }
        }
    }
}

@Composable
fun VoteCard(player: Player, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(player.color)
            .clickable { onClick() }
            .padding(16.dp)
            .height(180.dp), // Fixed height for grid consistency
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Selection Indicator (Placeholder)
        Box(
            modifier = Modifier
                .align(Alignment.End)
                .size(24.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.1f)), // Placeholder check
            contentAlignment = Alignment.Center
        ) {
           // Icon(Icons.Rounded.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Avatar Placeholder
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
               Text(player.name.first().toString(), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = player.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black.copy(alpha = 0.8f) // Text usually dark on colored cards
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
fun SkipVoteCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(CardGrey)
            .clickable { }
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.Block, contentDescription = null, tint = Color.Black.copy(alpha = 0.7f))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = "Skip Vote",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Black.copy(alpha = 0.8f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun VotingScreenPreview() {
    ImposterTheme {
        VotingScreen()
    }
}
