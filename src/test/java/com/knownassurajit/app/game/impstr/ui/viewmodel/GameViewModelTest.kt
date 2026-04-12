package com.knownassurajit.app.game.impstr.ui.viewmodel

import android.content.Context
import android.content.SharedPreferences
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.`when`
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

import androidx.lifecycle.SavedStateHandle
import com.knownassurajit.app.game.impstr.domain.usecase.ShufflePlayersUseCase
import org.mockito.ArgumentMatchers.anyList

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

@RunWith(MockitoJUnitRunner.Silent::class)
class GameViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var sharedPreferences: SharedPreferences

    @Mock
    private lateinit var editor: SharedPreferences.Editor

    @Mock
    private lateinit var shufflePlayersUseCase: ShufflePlayersUseCase

    private lateinit var viewModel: GameViewModel
    private val savedStateHandle = SavedStateHandle()

    @Before
    fun setup() {
        `when`(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPreferences)
        `when`(sharedPreferences.edit()).thenReturn(editor)
        `when`(editor.putInt(anyString(), anyInt())).thenReturn(editor)
        `when`(editor.putString(anyString(), anyString())).thenReturn(editor)
        
        // Default config
        `when`(sharedPreferences.getInt("player_count", 4)).thenReturn(4)
        `when`(sharedPreferences.getInt("imposter_count", 1)).thenReturn(1)
        `when`(sharedPreferences.getString("category", "Random Words")).thenReturn("Random Words")
        
        // Default mock behavior for shuffle
        `when`(shufflePlayersUseCase(anyList(), anyInt())).thenAnswer { invocation ->
            val players = invocation.getArgument<List<PlayerState>>(0)
            val count = invocation.getArgument<Int>(1)
            // Naive mock: just make the first 'count' players imposters
            players.mapIndexed { index, player ->
                player.copy(isImposter = index < count)
            }
        }

        viewModel = GameViewModel(context, savedStateHandle, shufflePlayersUseCase)
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
        assertEquals(3, viewModel.uiState.value.players.size) // minimum is 3

        // Set to 5 players
        viewModel.updatePlayerCount(5)
        assertEquals(5, viewModel.uiState.value.players.size)
        
        // Check imposter limit logic
        // 5 players -> max 2 imposters
        viewModel.updateImposterCount(2)
        assertEquals(2, viewModel.uiState.value.imposterCount)
        
        // Try to set 3 imposters with 5 players (should succeed since max is 4)
        viewModel.updateImposterCount(3)
        assertEquals(3, viewModel.uiState.value.imposterCount)
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
        
        viewModel.startDiscussion()
        viewModel.startVoting()
        
        // Pick a crewmate so game doesn't end immediately
        val playerToEliminate = viewModel.uiState.value.players.first { !it.isImposter }.id
        viewModel.castVote(listOf(playerToEliminate))
        
        val state = viewModel.uiState.value
        assertEquals(GamePhase.VOTING_RESULTS, state.phase) // Phase is voting results
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
        viewModel.startDiscussion()
        viewModel.startVoting()
        viewModel.castVote(listOf(imposter.id))
        
        // Check winner
        assertEquals("Crewmates", viewModel.uiState.value.winner)
        // Phase should be RESULT immediately
        assertEquals(GamePhase.RESULT, viewModel.uiState.value.phase)
    }

    @Test
    fun `win condition - imposters win`() {
        // Setup game with 4 players, 1 imposter
        viewModel.updatePlayerCount(4)
        viewModel.updateImposterCount(1)
        viewModel.startGame()

        // Round 1: Vote out a crewmate
        viewModel.startDiscussion()
        viewModel.startVoting()
        val crewmate1 = viewModel.uiState.value.players.first { !it.isImposter }
        viewModel.castVote(listOf(crewmate1.id))
        
        // Check not over
        assertNull(viewModel.uiState.value.winner)
        viewModel.startNextVotingRound()
        
        // Round 2: Vote out another crewmate
        viewModel.startVoting() // Next round transitions discussion -> voting
        val crewmate2 = viewModel.uiState.value.players.first { !it.isImposter && !it.isEliminated }
        viewModel.castVote(listOf(crewmate2.id))
        
        val activePlayers = viewModel.uiState.value.players.filter { !it.isEliminated }
        // Should be 1 imposter, 1 crewmate (minimum 2 players remaining)
        assertEquals(2, activePlayers.size)
        // check win
        assertEquals("Imposters", viewModel.uiState.value.winner)
        assertEquals(GamePhase.RESULT, viewModel.uiState.value.phase)
    }

    @Test
    fun `stealth mode assigns distinct words for imposters`() {
        // Enable Stealth Mode
        viewModel.setStealthMode(true)
        assertTrue(viewModel.uiState.value.isStealthMode)
        
        // Start game
        viewModel.startGame()
        
        val state = viewModel.uiState.value
        assertEquals(GamePhase.ROLE_REVEAL, state.phase)
        
        // In Stealth Mode, imposterWord should be present and distinct from secretWord
        assertNotNull(state.imposterWord)
        assertTrue(state.imposterWord.isNotEmpty())
        assertNotEquals(state.secretWord, state.imposterWord)
        
        // Verify all imposters see a word (revealed role word is tested in UI but logic check here)
        val imposters = state.players.filter { it.isImposter }
        assertTrue(imposters.isNotEmpty())
    }
}
