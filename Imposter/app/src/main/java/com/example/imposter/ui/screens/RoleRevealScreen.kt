package com.example.imposter.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.imposter.ui.theme.ImposterTheme
import com.example.imposter.ui.theme.NeonMint

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RoleRevealScreen(
    viewModel: com.example.imposter.ui.viewmodel.GameViewModel,
    onNext: () -> Unit
) {
    val uiState = androidx.compose.runtime.collectAsState(viewModel.uiState).value
    var isRevealed by remember { mutableStateOf(false) }
    
    // For demo purposes, we assume the user is "Sarah (You)"
    val currentPlayerName = "Sarah (You)"
    val isImposter = uiState.imposterName == currentPlayerName
    val secretWord = uiState.secretWord
    val category = uiState.category

    val gradientStart = if (isRevealed && isImposter) Color(0xFF450a0a) else Color(0xFF2e1065)
    val gradientEnd = if (isRevealed && isImposter) Color(0xFF000000) else Color(0xFF0f172a)
    
    val bgBrush = Brush.brushedGradient(
        colors = listOf(gradientStart, gradientEnd)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0c0c0e)) // Dark background
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Rounded.ArrowBack, 
                contentDescription = "Back", 
                tint = Color.White,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0xFF27272a))
                    .padding(8.dp)
            )
            Text(
                "ROUND 1", 
                style = MaterialTheme.typography.titleMedium, 
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Icon(
                Icons.Rounded.Settings, 
                contentDescription = "Settings", 
                tint = Color.White,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0xFF27272a))
                    .padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Instruction Text
        Text(
            text = if (isRevealed) "Memorize your secret word" else "Pass the phone to",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        if (!isRevealed) {
            Text(
                text = currentPlayerName,
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFFd8b4fe), // Light Purple
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = "Keep the screen hidden from others!",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Card
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(bgBrush)
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(32.dp))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(targetState = isRevealed) { revealed ->
                if (!revealed) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.05f))
                                .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Rounded.Lock,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(48.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            "SECRET LOCKED",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.6f),
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            if (isImposter) "YOU ARE THE" else "THE SECRET WORD IS",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.4f),
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            if (isImposter) "IMPOSTER" else secretWord,
                            style = MaterialTheme.typography.displayMedium,
                            color = if (isImposter) Color(0xFFef4444) else Color.White,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.05f))
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Rounded.Category,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.6f),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    category.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Action Button
        Button(
            onClick = { 
                if (isRevealed) {
                    onNext()
                } else {
                    isRevealed = true 
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = NeonMint,
                contentColor = Color(0xFF18181b)
            )
        ) {
            Text(
                text = if (isRevealed) "Got it! Start Game" else "Tap to Reveal",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Rounded.Visibility, contentDescription = null)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview
@Composable
fun RoleRevealScreenPreview() {
    ImposterTheme {
        RoleRevealScreen()
    }
}
