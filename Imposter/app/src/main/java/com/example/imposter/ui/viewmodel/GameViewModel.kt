package com.example.imposter.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imposter.data.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max

enum class GamePhase {
    SETUP,
    ROLE_REVEAL,
    DISCUSSION,
    VOTING,
    VOTING_RESULTS,
    REVEAL,
    RESULT
}

data class PlayerState(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val isImposter: Boolean = false,
    val isReady: Boolean = false,
    val hasVoted: Boolean = false,
    val isEliminated: Boolean = false
)

data class GameState(
    val phase: GamePhase = GamePhase.SETUP,
    val players: List<PlayerState> = emptyList(),
    val category: String = "Random Words",
    val secretWord: String = "",
    val imposterNames: List<String> = emptyList(),
    val imposterCount: Int = 1,
    val elapsedTime: Long = 0, // Time elapsed in current phase (seconds)
    // Removed currentRound since we are single round now
    val roundStartTime: Long = 0, // Timestamp when current phase/round started
    val totalGameTime: Long = 0, // Total time (seconds)
    val winner: String? = null,
    
    // Reveal State
    val currentRevealPlayerIndex: Int = 0,
    val isCardRevealed: Boolean = false,
    val isCardFlipping: Boolean = false,

    val lastEliminatedPlayer: PlayerState? = null,
    val eliminatedInCurrentRound: List<PlayerState> = emptyList()
)

@HiltViewModel
class GameViewModel @Inject constructor(
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: android.content.Context
) : ViewModel() {

    private val sharedPreferences = context.getSharedPreferences("impstr_settings", android.content.Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(GameState())
    val uiState: StateFlow<GameState> = _uiState.asStateFlow()
    
    private var timerJob: Job? = null

    // Initialize
    init {
        loadConfig()
    }

    private fun loadConfig() {
        val count = sharedPreferences.getInt("player_count", 4)
        val imposterCount = sharedPreferences.getInt("imposter_count", 1)
        val category = sharedPreferences.getString("category", "Random Words") ?: "Random Words"
        
        // Load player names if available
        val playerNames = mutableListOf<String>()
        for (i in 0 until count) {
            val name = sharedPreferences.getString("player_name_$i", "Player ${i + 1}") ?: "Player ${i + 1}"
            playerNames.add(name)
        }
        
        _uiState.update { 
            it.copy(
                category = category,
                imposterCount = imposterCount
            ) 
        }
        // Update players
         val players = playerNames.map { name ->
            PlayerState(name = name, isReady = true)
        }
         _uiState.update { it.copy(players = players) }
         
         // Ensure counts are valid
         updatePlayerCount(count)
         updateImposterCount(imposterCount)
    }

    private fun saveConfig() {
        val editor = sharedPreferences.edit()
        val state = _uiState.value
        
        editor.putInt("player_count", state.players.size)
        editor.putInt("imposter_count", state.imposterCount)
        editor.putString("category", state.category)
        
        state.players.forEachIndexed { index, player ->
            editor.putString("player_name_$index", player.name)
        }
        editor.apply()
    }

    fun updatePlayerCount(count: Int) {
        val currentImposterCount = _uiState.value.imposterCount
         val maxImposters = (count - 1) / 2
         val safeImposterCount = currentImposterCount.coerceAtMost(max(1, maxImposters)) 
         
         if (count < 3) return
         
         if (safeImposterCount != currentImposterCount) {
             _uiState.update { it.copy(imposterCount = safeImposterCount) }
         }
        
        val currentPlayers = _uiState.value.players
        val newPlayers = if (count > currentPlayers.size) {
            currentPlayers + List(count - currentPlayers.size) { index ->
                PlayerState(name = "Player ${currentPlayers.size + index + 1}", isReady = true)
            }
        } else {
            currentPlayers.take(count)
        }
        
        _uiState.update { it.copy(players = newPlayers) }
        saveConfig()
    }

    fun updateImposterCount(count: Int) {
        val maxImposters = (_uiState.value.players.size - 1) / 2
        if (count < 1 || count > maxImposters) return
        _uiState.update { it.copy(imposterCount = count) }
        saveConfig()
    }

    fun updatePlayerName(index: Int, newName: String) {
        if (index in _uiState.value.players.indices) {
            val updatedPlayers = _uiState.value.players.toMutableList()
            updatedPlayers[index] = updatedPlayers[index].copy(name = newName)
            _uiState.update { it.copy(players = updatedPlayers) }
            saveConfig()
        }
    }
    
    fun reorderPlayers(fromIndex: Int, toIndex: Int) {
        val currentPlayers = _uiState.value.players.toMutableList()
        if (fromIndex in currentPlayers.indices && toIndex in currentPlayers.indices) {
            val player = currentPlayers.removeAt(fromIndex)
            currentPlayers.add(toIndex, player)
            _uiState.update { it.copy(players = currentPlayers) }
            saveConfig()
        }
    }

    fun updateCategory(category: String) {
        _uiState.update { it.copy(category = category) }
        saveConfig()
    }

    fun startGame() {
        val players = _uiState.value.players.filter { it.isReady }
        
        if (players.size < 3) return
        
        val imposterCount = _uiState.value.imposterCount
        val imposterIndices = players.indices.shuffled().take(imposterCount)
        val imposterNames = imposterIndices.map { players[it].name }
        
        val updatedPlayers = players.mapIndexed { index, player ->
            player.copy(isImposter = index in imposterIndices)
        }
        
        val word = WordRepository.getRandomWord(_uiState.value.category)

        _uiState.update { currentState ->
            currentState.copy(
                phase = GamePhase.ROLE_REVEAL,
                players = updatedPlayers,
                imposterNames = imposterNames,
                secretWord = word,
                elapsedTime = 0,
                roundStartTime = 0,
                totalGameTime = 0,
                currentRevealPlayerIndex = 0,
                isCardRevealed = false,
                isCardFlipping = false
            )
        }
    }
    
    fun setCardFlipping(flipping: Boolean) {
        _uiState.update { it.copy(isCardFlipping = flipping) }
    }
    
    fun setCardRevealed(revealed: Boolean) {
        _uiState.update { it.copy(isCardRevealed = revealed) }
    }
    
    fun nextPlayerReveal(): Boolean {
        val currentIndex = _uiState.value.currentRevealPlayerIndex
        val players = _uiState.value.players
        
        if (currentIndex < players.size - 1) {
            _uiState.update { 
                it.copy(
                    currentRevealPlayerIndex = currentIndex + 1,
                    isCardRevealed = false,
                    isCardFlipping = false
                ) 
            }
            return true
        }
        return false
    }

    fun startDiscussion() {
        _uiState.update { 
            it.copy(
                phase = GamePhase.DISCUSSION,
                roundStartTime = System.currentTimeMillis(),
                elapsedTime = 0
            )
        }
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.phase == GamePhase.DISCUSSION) {
                delay(1000)
                _uiState.update { it.copy(elapsedTime = it.elapsedTime + 1) }
            }
        }
    }

    fun startVoting() {
        timerJob?.cancel()
        _uiState.update { 
            it.copy(
                phase = GamePhase.VOTING,
                totalGameTime = it.totalGameTime + it.elapsedTime
            )
        }
    }

    fun castVote(votedForNames: List<String>) {
        if (votedForNames.contains("SKIP")) {
             _uiState.update { 
                it.copy(
                    phase = GamePhase.VOTING_RESULTS,
                    eliminatedInCurrentRound = emptyList()
                )
            }
            // Check win conditions even on skip? Usually skipping doesn't change win state unless time runs out (not implemented here)
            // But we should check if game should end for other reasons? 
            // For now, proceed.
            checkWinConditions(autoEnd = false)
            return
        }
        
        val eliminatedPlayers = mutableListOf<PlayerState>()
        val updatedPlayers = _uiState.value.players.map { player ->
            if (votedForNames.contains(player.name)) {
                val eliminated = player.copy(isEliminated = true)
                eliminatedPlayers.add(eliminated)
                eliminated
            } else {
                player
            }
        }
        
        _uiState.update { 
            it.copy(
                players = updatedPlayers,
                eliminatedInCurrentRound = eliminatedPlayers,
                phase = GamePhase.VOTING_RESULTS
            ) 
        }
        
        checkWinConditions(autoEnd = false)
    }

    fun startNextVotingRound() {
        _uiState.update { 
            it.copy(
                phase = GamePhase.VOTING,
                eliminatedInCurrentRound = emptyList()
            )
        }
    }
    
    fun proceedFromResults() {
        endGame()
    }

    private fun checkWinConditions(autoEnd: Boolean = true): Boolean {
        val activePlayers = _uiState.value.players.filter { !it.isEliminated }
        val activeImposters = activePlayers.filter { it.isImposter }
        val activeCrewmates = activePlayers.filter { !it.isImposter }
        
        val winner = when {
            activeImposters.isEmpty() -> "Crewmates"
            activeImposters.size >= activeCrewmates.size -> "Imposters"
            else -> null
        }
        
        if (winner != null) {
            _uiState.update { it.copy(winner = winner) }
            if (autoEnd) {
                 _uiState.update { it.copy(phase = GamePhase.RESULT) }
            }
            return true
        }
        
        return false
    }
    
    fun endGame() {
        val state = _uiState.value
        if (state.winner == null) {
            // Force end
             val activePlayers = state.players.filter { !it.isEliminated }
             val activeImposters = activePlayers.filter { it.isImposter }
             val winner = if (activeImposters.isEmpty()) "Crewmates" else "Imposters"
             _uiState.update { it.copy(winner = winner) }
        }
        
        _uiState.update { 
            it.copy(phase = GamePhase.RESULT)
        }
    }

    fun resetGame() {
        timerJob?.cancel()
        _uiState.update { 
            GameState(
                players = it.players.map { p -> p.copy(isImposter = false, hasVoted = false, isEliminated = false, isReady = true) },
                category = it.category,
                phase = GamePhase.SETUP,
                elapsedTime = 0,
                roundStartTime = 0,
                totalGameTime = 0
            )
        }
    }
}
