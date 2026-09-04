package com.knownassurajit.app.game.impstr.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.knownassurajit.app.game.impstr.ui.screens.DiscussionScreen
import com.knownassurajit.app.game.impstr.ui.screens.HomeScreen
import com.knownassurajit.app.game.impstr.ui.screens.ResultScreen
import com.knownassurajit.app.game.impstr.ui.screens.RoleRevealScreen
import com.knownassurajit.app.game.impstr.ui.screens.VotingResultsScreen
import com.knownassurajit.app.game.impstr.ui.screens.VotingScreen
import com.knownassurajit.app.game.impstr.ui.viewmodel.GameViewModel

/** Shared game graph for JVM and device Compose tests (debug source set). */
@Composable
fun ImpstrGameFlow(viewModel: GameViewModel) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = Modifier.fillMaxSize(),
    ) {
        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onStartGame = {
                    viewModel.startGame()
                    navController.navigate("reveal")
                },
                personalizationViewModel = null,
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
                onGameEnd = { navController.popBackStack("home", inclusive = false) },
            )
        }
        composable("voting") {
            VotingScreen(
                viewModel = viewModel,
                onVoteConfirmed = { navController.navigate("voting_results") },
                onGameEnd = { navController.popBackStack("home", inclusive = false) },
            )
        }
        composable("voting_results") {
            VotingResultsScreen(
                viewModel = viewModel,
                onVoteAgain = {
                    navController.navigate("discussion") {
                        popUpTo("discussion") { inclusive = true }
                    }
                },
                onEndGame = { navController.navigate("result") },
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
