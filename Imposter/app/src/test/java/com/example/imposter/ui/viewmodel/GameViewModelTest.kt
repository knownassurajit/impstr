package com.example.imposter.ui.viewmodel

import com.example.imposter.data.GameDao
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.Rule

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

@RunWith(MockitoJUnitRunner::class)
class GameViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var gameDao: GameDao

    private lateinit var viewModel: GameViewModel

    @Before
    fun setup() {
        viewModel = GameViewModel(gameDao)
    }

    @Test
    fun `initial state is correct`() {
        val state = viewModel.uiState.value
        assertEquals(4, state.players.size) // Default 4
        assertEquals(1, state.imposterCount)
        assertEquals(GamePhase.SETUP, state.phase)
    }

    @Test
    fun `updatePlayerCount respects minimums and imposter limits`() {
        // Try to set too few players
        viewModel.updatePlayerCount(2)
        assertEquals(4, viewModel.uiState.value.players.size)

        // Set to 5 players
        viewModel.updatePlayerCount(5)
        assertEquals(5, viewModel.uiState.value.players.size)
        
        // Check imposter limit logic
        // 5 players -> max 2 imposters
        viewModel.updateImposterCount(2)
        assertEquals(2, viewModel.uiState.value.imposterCount)
        
        // Try to set 3 imposters with 5 players (should fail)
        viewModel.updateImposterCount(3)
        assertEquals(2, viewModel.uiState.value.imposterCount)
    }

    @Test
    fun `startGame assigns roles correctly`() {
        viewModel.updatePlayerCount(5)
        viewModel.updateImposterCount(2)
        viewModel.startGame()

        val state = viewModel.uiState.value
        assertEquals(GamePhase.ROLE_REVEAL, state.phase)
        val imposters = state.players.filter { it.isImposter }
        assertEquals(2, imposters.size)
    }

    @Test
    fun `voting eliminates players`() {
        viewModel.startGame()
        // Skip through reveal
        while (viewModel.nextPlayerReveal()) {}
        
        viewModel.startDiscussion()
        viewModel.startVoting()
        
        val playerToEliminate = viewModel.uiState.value.players[0].name
        viewModel.castVote(listOf(playerToEliminate))
        
        val state = viewModel.uiState.value
        assertEquals(GamePhase.VOTING_RESULTS, state.phase)
        assertTrue(state.players[0].isEliminated)
        assertEquals(1, state.eliminatedInCurrentRound.size)
    }

    @Test
    fun `win condition - crewmates win`() {
        // Setup game with 3 players, 1 imposter
        viewModel.updatePlayerCount(3)
        viewModel.updateImposterCount(1)
        viewModel.startGame()
        
        // Find imposter
        val imposter = viewModel.uiState.value.players.first { it.isImposter }
        
        // Vote out imposter
        viewModel.castVote(listOf(imposter.name))
        
        // Check winner
        assertEquals("Crewmates", viewModel.uiState.value.winner)
        // Phase should be VOTING_RESULTS to show the elimination screen first
        assertEquals(GamePhase.VOTING_RESULTS, viewModel.uiState.value.phase)
    }

    @Test
    fun `win condition - imposters win`() {
        // Setup game with 4 players, 2 imposter (need to adjust logic/test to allow this setup if possible, or 4 players 1 imposter)
        // 4 players, 1 imposter. Need to eliminate 2 crewmates to win? 
        // Imposter wins if Imposters >= Crewmates
        // 4 players (1 imp, 3 crew). 
        // Round 1: eliminate 1 crew -> 3 active (1 imp, 2 crew). No win.
        // Round 2: eliminate 1 crew -> 2 active (1 imp, 1 crew). Imposter wins.
        
        viewModel.updatePlayerCount(4)
        viewModel.updateImposterCount(1)
        viewModel.startGame()

        // Round 1: Vote out a crewmate
        val crewmate1 = viewModel.uiState.value.players.first { !it.isImposter }
        viewModel.castVote(listOf(crewmate1.name))
        
        // Check not over
        assertNull(viewModel.uiState.value.winner)
        viewModel.startNewRound()
        
        // Round 2: Vote out another crewmate
        val crewmate2 = viewModel.uiState.value.players.first { !it.isImposter && !it.isEliminated }
        viewModel.castVote(listOf(crewmate2.name))
        
        val activePlayers = viewModel.uiState.value.players.filter { !it.isEliminated }
        // Should be 1 imposter, 1 crewmate
        assertEquals(2, activePlayers.size)
        // check win
        assertEquals("Imposters", viewModel.uiState.value.winner)
    }
}
