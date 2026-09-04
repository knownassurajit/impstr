package com.knownassurajit.impstr_game.app

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.knownassurajit.impstr_game.app.ui.screens.DiscussionScreen
import com.knownassurajit.impstr_game.app.ui.screens.HomeScreen
import com.knownassurajit.impstr_game.app.ui.screens.ResultScreen
import com.knownassurajit.impstr_game.app.ui.screens.RoleRevealScreen
import com.knownassurajit.impstr_game.app.ui.screens.VotingScreen
import com.knownassurajit.impstr_game.app.ui.theme.IMPSTRTheme
import com.knownassurajit.impstr_game.app.ui.viewmodel.GameViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Last time the user touched the screen, in ms since boot. Used by long-running
 * screens to decide whether to keep the display awake.
 */
val LocalInteractionTime =
    compositionLocalOf<MutableState<Long>> {
        error("No Interaction Time provided")
    }

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Draw behind status & navigation bars. The Theme keeps appearance
        // flags in sync; per-screen content uses Modifier.safeDrawingPadding()
        // (applied on the NavHost) so no UI overlaps the system bars.
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContent {
            val interactionTime = remember { mutableStateOf(System.currentTimeMillis()) }

            val viewModel: GameViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            IMPSTRTheme(isStealthMode = uiState.isStealthMode) {
                CompositionLocalProvider(LocalInteractionTime provides interactionTime) {
                    Surface(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .pointerInput(Unit) {
                                    awaitPointerEventScope {
                                        while (true) {
                                            awaitPointerEvent(PointerEventPass.Initial)
                                            interactionTime.value = System.currentTimeMillis()
                                        }
                                    }
                                },
                        color = MaterialTheme.colorScheme.background,
                    ) {
                        val navController = rememberNavController()

                        NavHost(
                            navController = navController,
                            startDestination = "home",
                            modifier = Modifier.safeDrawingPadding(),
                            enterTransition = {
                                slideIntoContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec = tween(300),
                                ) + fadeIn(animationSpec = tween(300))
                            },
                            exitTransition = {
                                slideOutOfContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec = tween(300),
                                ) + fadeOut(animationSpec = tween(300))
                            },
                            popEnterTransition = {
                                slideIntoContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Right,
                                    animationSpec = tween(300),
                                ) + fadeIn(animationSpec = tween(300))
                            },
                            popExitTransition = {
                                slideOutOfContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Right,
                                    animationSpec = tween(300),
                                ) + fadeOut(animationSpec = tween(300))
                            },
                        ) {
                            composable("home") {
                                val context = LocalContext.current

                                var showExitDialog by remember { mutableStateOf(false) }

                                androidx.activity.compose.BackHandler {
                                    showExitDialog = true
                                }

                                if (showExitDialog) {
                                    AlertDialog(
                                        onDismissRequest = { showExitDialog = false },
                                        title = { Text("Exit Game?") },
                                        text = { Text("Are you sure you want to close the app?") },
                                        confirmButton = {
                                            IconButton(
                                                onClick = {
                                                    showExitDialog = false
                                                    (context as? android.app.Activity)?.finish()
                                                },
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Rounded.Check,
                                                    contentDescription = "Yes",
                                                    tint = MaterialTheme.colorScheme.primary,
                                                )
                                            }
                                        },
                                        dismissButton = {
                                            IconButton(
                                                onClick = { showExitDialog = false },
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Rounded.Close,
                                                    contentDescription = "No",
                                                    tint = MaterialTheme.colorScheme.error,
                                                )
                                            }
                                        },
                                    )
                                }

                                HomeScreen(
                                    viewModel = viewModel,
                                    onStartGame = {
                                        viewModel.startGame()
                                        navController.navigate("reveal")
                                    },
                                )
                            }

                            composable("reveal") {
                                RoleRevealScreen(
                                    viewModel = viewModel,
                                    onNext = {
                                        viewModel.startDiscussion()
                                        navController.navigate("discussion")
                                    },
                                    onBack = {
                                        viewModel.resetGame()
                                        navController.popBackStack()
                                    },
                                )
                            }
                            composable("discussion") {
                                DiscussionScreen(
                                    viewModel = viewModel,
                                    onVotingStart = {
                                        viewModel.startVoting()
                                        navController.navigate("voting")
                                    },
                                    onGameEnd = {
                                        navController.popBackStack("home", inclusive = false)
                                    },
                                )
                            }
                            composable("voting") {
                                VotingScreen(
                                    viewModel = viewModel,
                                    onVoteConfirmed = {
                                        navController.navigate("voting_results")
                                    },
                                    onGameEnd = {
                                        navController.popBackStack("home", inclusive = false)
                                    },
                                )
                            }
                            composable("voting_results") {
                                com.knownassurajit.impstr_game.app.ui.screens.VotingResultsScreen(
                                    viewModel = viewModel,
                                    onVoteAgain = {
                                        navController.navigate("discussion") {
                                            popUpTo("discussion") { inclusive = true }
                                        }
                                    },
                                    onEndGame = {
                                        navController.navigate("result")
                                    },
                                    onBackToLobby = {
                                        navController.navigate("home") {
                                            popUpTo("home") { inclusive = true }
                                        }
                                    },
                                )
                            }

                            composable("result") {
                                ResultScreen(
                                    viewModel = viewModel,
                                    onPlayAgain = {
                                        viewModel.resetGame()
                                        navController.navigate("home") {
                                            popUpTo("home") { inclusive = true }
                                        }
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HelpDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(Icons.Filled.Info, contentDescription = "Info")
        },
        title = {
            Text(
                text = "How to Play",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Column(
                modifier =
                    Modifier
                        .verticalScroll(rememberScrollState())
                        .fillMaxWidth(),
            ) {
                RuleHeader("1. The Setup")
                RuleText(
                    "Everyone receives a secret word. However, one player is the Imposter and sees a different word (or nothing at all).",
                )

                Spacer(modifier = Modifier.height(12.dp))

                RuleHeader("2. Describe & Blend In")
                RuleText(
                    "Go around the circle. Describe your word using one sentence.",
                )
                RuleSubText(
                    "• Civilians: Don't be too obvious, or the Imposter will guess the word.\n" +
                        "• Imposter: Listen carefully and lie convincingly to blend in.",
                )

                Spacer(modifier = Modifier.height(12.dp))

                RuleHeader("3. The Verdict")
                RuleText(
                    "After discussion, vote for who you think the Imposter is. If the majority catches the Imposter, the Civilians win!",
                )

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                CreditsFooter()
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Let's Play")
            }
        },
    )
}

@Composable
fun RuleHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
fun RuleText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(top = 4.dp),
    )
}

@Composable
fun RuleSubText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 4.dp, start = 8.dp),
    )
}

@Composable
fun CreditsFooter() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Made with ",
                style = MaterialTheme.typography.labelSmall,
            )
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = "Love",
                tint = Color(0xFF3BE6FF),
                modifier =
                    Modifier
                        .size(16.dp)
                        .padding(horizontal = 2.dp),
            )
            Text(
                text = " by Surajit Das",
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Version 1.0.0",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline,
        )
    }
}
