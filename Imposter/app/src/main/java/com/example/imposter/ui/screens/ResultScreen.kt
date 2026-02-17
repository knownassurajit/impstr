package com.example.imposter.ui.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun ResultScreen(
    viewModel: com.example.imposter.ui.viewmodel.GameViewModel,
    onPlayAgain: () -> Unit
) {
    // Cache the state on entry to prevent flashing when game resets during navigation
    val uiState = remember { viewModel.uiState.value }

    val imposters = uiState.imposterNames
    val winner = uiState.winner
    val isCrewmatesWin = winner == "Crewmates"
    
    val cardColor = if (isCrewmatesWin) Color(0xFF22c55e) else Color(0xFFef4444)
    val bgGradient = if (isCrewmatesWin) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF064e3b).copy(alpha = 0.3f),
                MaterialTheme.colorScheme.background
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF7f1d1d).copy(alpha = 0.3f),
                MaterialTheme.colorScheme.background
            )
        )
    }
    
    // Animation state
    var isVisible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "card_scale"
    )
    
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bgGradient)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.weight(0.5f))
                
                // Result Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(scale),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 8.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Icon
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .background(cardColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "😈",
                                style = MaterialTheme.typography.displayLarge,
                                fontSize = 56.sp
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Text(
                            text = if (imposters.size > 1) "The Imposters were" else "The Imposter was",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))

                        imposters.forEach { imposter ->
                            Text(
                                text = imposter,
                                style = MaterialTheme.typography.displaySmall,
                                color = cardColor,
                                fontWeight = FontWeight.Black,
                                textAlign = TextAlign.Center
                            )
                            if (imposters.indexOf(imposter) != imposters.lastIndex) {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Winner Badge
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = cardColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = if (isCrewmatesWin) "🎉 Crewmates Win!" else "😈 Imposter Wins!",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = cardColor,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Total Game Time
                        val totalMinutes = uiState.totalGameTime / 60
                        val totalSeconds = uiState.totalGameTime % 60
                        Text(
                            text = "Game Duration: $totalMinutes min $totalSeconds sec",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))

                // Play Again Button
                FilledTonalButton(
                    onClick = onPlayAgain,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = androidx.compose.material3.ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        "Play Again",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
