package com.example.imposter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.imposter.ui.screens.DiscussionScreen
import com.example.imposter.ui.screens.GameSetupScreen
import com.example.imposter.ui.screens.HomeScreen
import com.example.imposter.ui.screens.ResultScreen
import com.example.imposter.ui.screens.RoleRevealScreen
import com.example.imposter.ui.screens.VotingScreen
import com.example.imposter.ui.theme.ImposterTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ImposterTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val viewModel: GameViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
                    val uiState = viewModel.uiState
                    
                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") { 
                            HomeScreen(
                                viewModel = viewModel,
                                onStartGame = {
                                    viewModel.startGame()
                                    navController.navigate("reveal")
                                }
                            ) 
                        }
                        composable("setup") { GameSetupScreen() }
                        composable("reveal") { 
                            RoleRevealScreen(
                                viewModel = viewModel,
                                onNext = {
                                    viewModel.startDiscussion()
                                    navController.navigate("discussion")
                                }
                            ) 
                        }
                        composable("discussion") { 
                            DiscussionScreen(
                                viewModel = viewModel,
                                onVotingStart = {
                                    viewModel.startVoting()
                                    navController.navigate("voting")
                                }
                            ) 
                        }
                        composable("voting") { 
                            VotingScreen(
                                viewModel = viewModel,
                                onVoteConfirmed = {
                                    navController.navigate("result")
                                }
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
                                }
                            ) 
                        }
                    }
                }
            }
        }
    }
}
