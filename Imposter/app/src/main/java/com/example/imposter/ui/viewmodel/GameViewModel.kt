package com.example.imposter.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imposter.data.GameDao
import com.example.imposter.data.GameResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class GamePhase {
    SETUP,
    ROLE_REVEAL,
    DISCUSSION,
    VOTING,
    RESULT
}

data class PlayerState(
    val name: String,
    val isImposter: Boolean = false,
    val isReady: Boolean = false,
    val hasVoted: Boolean = false,
    val isEliminated: Boolean = false
)

data class GameState(
    val phase: GamePhase = GamePhase.SETUP,
    val players: List<PlayerState> = emptyList(),
    val category: String = "Animals",
    val secretWord: String = "Lion",
    val imposterName: String = "",
    val timeLeft: Long = 0,
    val totalTime: Long = 0,
    val winner: String? = null
)

@HiltViewModel
class GameViewModel @Inject constructor(
    private val gameDao: GameDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameState())
    val uiState: StateFlow<GameState> = _uiState.asStateFlow()

    // Mock initial players
    init {
        _uiState.update { currentState ->
            currentState.copy(
                players = listOf(
                    PlayerState("Sarah (You)", isReady = true),
                    PlayerState("Marcus", isReady = true),
                    PlayerState("Elena", isReady = false),
                    PlayerState("David", isReady = true)
                )
            )
        }
    }

    fun startGame() {
        // Simple logic: Pick a random imposter
        val players = _uiState.value.players.toMutableList()
        val imposterIndex = players.indices.random()
        val updatedPlayers = players.mapIndexed { index, player ->
            player.copy(isImposter = index == imposterIndex)
        }
        val imposterName = updatedPlayers[imposterIndex].name

        _uiState.update { currentState ->
            currentState.copy(
                phase = GamePhase.ROLE_REVEAL,
                players = updatedPlayers,
                imposterName = imposterName,
                timeLeft = 180 // 3 minutes discussion
            )
        }
    }

    fun startDiscussion() {
        _uiState.update { it.copy(phase = GamePhase.DISCUSSION) }
        startTimer()
    }

    private fun startTimer() {
        viewModelScope.launch {
            while (_uiState.value.timeLeft > 0 && _uiState.value.phase == GamePhase.DISCUSSION) {
                delay(1000)
                _uiState.update { it.copy(timeLeft = it.timeLeft - 1) }
            }
            if (_uiState.value.phase == GamePhase.DISCUSSION) {
                startVoting()
            }
        }
    }

    fun startVoting() {
        _uiState.update { it.copy(phase = GamePhase.VOTING) }
    }

    fun castVote(votedForName: String) {
        // Simplified: Direct transition to results
        // In real game: Wait for all votes, then calculate result
        val isImposterCaught = votedForName == _uiState.value.imposterName
        val winner = if (isImposterCaught) "Crewmates" else "Imposter"

        _uiState.update { it.copy(phase = GamePhase.RESULT, winner = winner) }
        saveGameResult()
    }

    fun resetGame() {
        _uiState.update { 
            GameState(
                players = it.players.map { p -> p.copy(isImposter = false, hasVoted = false, isEliminated = false) }
            )
        }
    }

    private fun saveGameResult() {
        viewModelScope.launch {
            val state = _uiState.value
            val result = GameResult(
                winner = state.winner ?: "Unknown",
                imposterName = state.imposterName,
                word = state.secretWord,
                durationSeconds = state.totalTime - state.timeLeft,
                timestamp = System.currentTimeMillis()
            )
            gameDao.insertGame(result)
        }
    }
}
