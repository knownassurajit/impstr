package com.game.impstr.ui.viewmodel

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.game.impstr.data.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import javax.inject.Inject
import kotlin.math.max
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.compose.runtime.Stable

/**
 * Represents the different phases of the Imposter game.
 */
enum class GamePhase {
    SETUP,
    ROLE_REVEAL,
    DISCUSSION,
    HOST_VOTING,
    VOTING_RESULTS,
    RESULT,
}

/**
 * Records the details of a player's elimination during the game.
 *
 * @property roundNumber The round in which the player was eliminated.
 * @property eliminationOrder The global order of this elimination (e.g., 1st, 2nd).
 * @property player The state of the player at the time of elimination.
 */
@Parcelize
data class EliminationRecord(
    val roundNumber: Int,
    val eliminationOrder: Int,
    val player: PlayerState,
) : Parcelable

/**
 * Represents the current state of a player in the game.
 *
 * @property id Unique identifier for the player.
 * @property name The display name of the player.
 * @property isImposter True if the player is assigned the Imposter role.
 * @property isReady True if the player is ready to proceed.
 * @property hasVoted True if the player has submitted their vote in the current round.
 * @property isEliminated True if the player has been voted out.
 */
@Stable
@Parcelize
data class PlayerState(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val isImposter: Boolean = false,
    val isReady: Boolean = false,
    val hasVoted: Boolean = false,
    val isEliminated: Boolean = false,
) : Parcelable

/**
 * Holds the comprehensive state of the current game session.
 *
 * @property phase The current phase of the game.
 * @property players List of all players in the game.
 * @property category The topic category for the secret word.
 * @property secretWord The word that crewmates know, but imposters don't.
 * @property imposterNames Pre-calculated list of imposter names.
 * @property imposterCount The configured number of imposters.
 * @property elapsedTime The time elapsed in the current phase (in seconds).
 * @property currentRoundNumber The current voting round number.
 * @property roundStartTime Timestamp of when the current phase/round started.
 * @property totalGameTime The cumulative time since the game started (in seconds).
 * @property winner The winning team ("Crewmates" or "Imposters"), or null if ongoing.
 * @property currentRevealPlayerIndex Index of the player currently viewing their role.
 * @property isCardRevealed True if the current player's role card is flipped over.
 * @property isCardFlipping True if the card flip animation is currently in progress.
 * @property lastEliminatedPlayer The most recently eliminated player (if any).
 * @property eliminatedInCurrentRound Players eliminated in the most recent voting phase.
 * @property eliminationHistory Exhaustive list of all eliminations in the game.
 */
@Stable
@Parcelize
data class GameState(
    val phase: GamePhase = GamePhase.SETUP,
    val players: List<PlayerState> = emptyList(),
    val category: String = "Random Words",
    val secretWord: String = "",
    val imposterNames: List<String> = emptyList(),
    val imposterCount: Int = 1,
    val elapsedTime: Long = 0,
    val currentRoundNumber: Int = 1,
    val roundStartTime: Long = 0,
    val totalGameTime: Long = 0,
    val winner: String? = null,
    // Reveal State
    val currentRevealPlayerIndex: Int = 0,
    val isCardRevealed: Boolean = false,
    val isCardFlipping: Boolean = false,
    val lastEliminatedPlayer: PlayerState? = null,
    val eliminatedInCurrentRound: List<PlayerState> = emptyList(),
    val eliminationHistory: List<EliminationRecord> = emptyList(),
) : Parcelable

/**
 * ViewModel managing the core game logic, state transitions, and timers.
 */
@HiltViewModel
class GameViewModel
    @Inject
    constructor(
        @dagger.hilt.android.qualifiers.ApplicationContext private val context: android.content.Context,
        private val savedStateHandle: androidx.lifecycle.SavedStateHandle,
        private val shufflePlayersUseCase: com.game.impstr.domain.usecase.ShufflePlayersUseCase,
    ) : ViewModel() {
        private val prefs by lazy {
            try {
                val masterKey = MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()

                EncryptedSharedPreferences.create(
                    context,
                    "imposter_prefs_secure",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )
            } catch (e: Exception) {
                // Use regular preferences when encrypted storage is unavailable in tests.
                context.getSharedPreferences("imposter_prefs", android.content.Context.MODE_PRIVATE)
            }
        }

        private val _uiState = MutableStateFlow(GameState())
        val uiState = _uiState.asStateFlow()

        private var timerJob: Job? = null
        private var gameTimerJob: Job? = null // For total game duration

        private fun updateState(update: (GameState) -> GameState) {
            _uiState.update(update)
        }

        init {
            val savedState = savedStateHandle.get<GameState>("gameState")
            if (savedState != null) {
                _uiState.value = savedState
            } else {
                // Load from SharedPreferences
                loadConfig()
            }

            viewModelScope.launch {
                uiState.collect {
                    savedStateHandle["gameState"] = it
                }
            }
        }

        private fun loadConfig() {
            val category = prefs.getString("category", "Random Words") ?: "Random Words"
            val imposterCount = prefs.getInt("imposter_count", 1)
            val playerNamesString = prefs.getString("player_names", null)

            val players =
                if (playerNamesString != null) {
                    playerNamesString.split(";;;").filter { it.isNotEmpty() }.map { PlayerState(name = it) }
                } else {
                // Provide a playable default lobby for first launch.
                List(4) { PlayerState(name = "Player ${it + 1}") }
                }

            // Clamp persisted values to the current roster size.
            val maxImposters = max(1, players.size - 1)
            val safeImposterCount = imposterCount.coerceIn(1, maxImposters)

            updateState {
                it.copy(
                    category = category,
                    imposterCount = safeImposterCount,
                    players = players,
                )
            }
        }

        private fun saveConfig() {
            val state = _uiState.value
            val names = state.players.joinToString(";;;") { it.name }
            prefs.edit().apply {
                putString("category", state.category)
                putInt("imposter_count", state.imposterCount)
                putString("player_names", names)
                apply()
            }
        }

        /**
         * Updates the total number of players and ensures the imposter count remains valid.
         * Appends new players or truncates the current list accordingly.
         *
         * @param count The target number of total players.
         */
        fun updatePlayerCount(count: Int) {
            val safeCount = max(3, count)
            updateState { currentState ->
                val currentPlayers = currentState.players
                val newPlayers =
                    if (safeCount > currentPlayers.size) {
                        currentPlayers +
                            List(safeCount - currentPlayers.size) {
                                PlayerState(name = "Player ${currentPlayers.size + it + 1}")
                            }
                    } else {
                        currentPlayers.take(safeCount)
                    }

                // Keep imposter count valid after roster size changes.
                val maxImposters = max(1, newPlayers.size - 1)
                val currentImposterCount = currentState.imposterCount
                val newImposterCount = currentImposterCount.coerceIn(1, maxImposters)

                currentState.copy(
                    players = newPlayers,
                    imposterCount = newImposterCount,
                )
            }
            saveConfig()
        }

        fun updateImposterCount(count: Int) {
            val playerCount = _uiState.value.players.size
            val maxImposters = max(1, playerCount - 1)
            val safeCount = count.coerceIn(1, maxImposters)

            updateState { it.copy(imposterCount = safeCount) }
            saveConfig()
        }

        fun updateCategory(category: String) {
            updateState { it.copy(category = category) }
            saveConfig()
        }

        fun updatePlayerName(
            index: Int,
            name: String,
        ) {
            updateState { currentState ->
                val players = currentState.players.toMutableList()
                if (index in players.indices) {
                    players[index] = players[index].copy(name = name)
                }
                currentState.copy(players = players)
            }
            saveConfig()
        }

        /**
         * Initializes the game by shuffling roles, picking a secret word,
         * and transitioning to the [GamePhase.ROLE_REVEAL] phase.
         */
        fun startGame() {
            val players = _uiState.value.players

            if (players.size < 3) return

            val imposterCount = _uiState.value.imposterCount

            val updatedPlayers = shufflePlayersUseCase(players, imposterCount)
            val imposterNames = updatedPlayers.filter { it.isImposter }.map { it.name }

            val word = WordRepository.getRandomWord(_uiState.value.category)

            updateState { currentState ->
                if (currentState.phase != GamePhase.SETUP) return@updateState currentState
                currentState.copy(
                    phase = GamePhase.ROLE_REVEAL,
                    players = updatedPlayers,
                    imposterNames = imposterNames,
                    secretWord = word,
                    elapsedTime = 0,
                    currentRoundNumber = 1,
                    roundStartTime = 0,
                    totalGameTime = 0,
                    currentRevealPlayerIndex = 0,
                    isCardRevealed = false,
                    isCardFlipping = false,
                    eliminationHistory = emptyList(),
                )
            }
            startGameTimer()
        }

        private fun startGameTimer() {
            gameTimerJob?.cancel()
            gameTimerJob =
                viewModelScope.launch {
                    val startTime = System.currentTimeMillis()
                    while (true) {
                        val now = System.currentTimeMillis()
                        val totalElapsed = (now - startTime) / 1000
                        updateState { it.copy(totalGameTime = totalElapsed) }
                        delay(1000)
                    }
                }
        }

        fun setCardFlipping(flipping: Boolean) {
            updateState { it.copy(isCardFlipping = flipping) }
        }

        fun setCardRevealed(revealed: Boolean) {
            updateState { it.copy(isCardRevealed = revealed) }
        }

        fun nextPlayerReveal(): Boolean {
            val currentIndex = _uiState.value.currentRevealPlayerIndex
            val players = _uiState.value.players

            if (currentIndex < players.size - 1) {
                updateState {
                    it.copy(
                        currentRevealPlayerIndex = currentIndex + 1,
                        isCardRevealed = false,
                        isCardFlipping = false,
                    )
                }
                return true
            }
            return false
        }

        /**
         * Begins the discussion phase, resetting the round timer.
         */
        fun startDiscussion() {
            val now = System.currentTimeMillis()
            updateState { currentState ->
                if (currentState.phase != GamePhase.ROLE_REVEAL && currentState.phase != GamePhase.VOTING_RESULTS) {
                    return@updateState currentState
                }
                currentState.copy(
                    phase = GamePhase.DISCUSSION,
                    roundStartTime = now,
                    elapsedTime = 0,
                )
            }
            startPhaseTimer()
        }

        private fun startPhaseTimer() {
            timerJob?.cancel()
            timerJob =
                viewModelScope.launch {
                    while (_uiState.value.phase == GamePhase.DISCUSSION) {
                        val startTime = _uiState.value.roundStartTime
                        if (startTime > 0) {
                            val elapsed = (System.currentTimeMillis() - startTime) / 1000
                            if (elapsed != _uiState.value.elapsedTime) {
                                // Avoid redundant state writes that trigger unnecessary recomposition.
                                updateState { it.copy(elapsedTime = elapsed) }
                            }
                        }
                        delay(200)
                    }
                }
        }

        /**
         * Transitions the game from discussion to the host voting phase.
         * Halts the discussion phase timer, but keeps the total game timer running.
         */
        fun startVoting() {
            timerJob?.cancel()
            updateState { currentState ->
                if (currentState.phase != GamePhase.DISCUSSION) return@updateState currentState
                currentState.copy(
                    phase = GamePhase.HOST_VOTING,
                )
            }
        }

        /**
         * Casts the host's votes to eliminate players or skip the vote.
         * Transitions the game to [GamePhase.VOTING_RESULTS] and records eliminations.
         *
         * Rule notes:
         * - Skip votes never eliminate players and still advance to results.
         * - Elimination count per round is capped at the number of active imposters.
         *   This keeps vote impact proportional to the hidden threat currently in play.
         *
         * @param votedForIds A list of player IDs selected for elimination, or ["SKIP"].
         */
        fun castVote(votedForIds: List<String>) {
            val currentState = _uiState.value
            if (currentState.phase != GamePhase.HOST_VOTING) return

            val activePlayers = currentState.players.filter { !it.isEliminated }
            val activeImpostersCount = activePlayers.count { it.isImposter }

            if (votedForIds.contains("SKIP")) {
                updateState {
                    it.copy(
                        phase = GamePhase.VOTING_RESULTS,
                        eliminatedInCurrentRound = emptyList(),
                    )
                }
                checkWinConditions(autoEnd = true)
                return
            }

            val maxEliminations = max(1, activeImpostersCount)
            val validVotedForIds = votedForIds.take(maxEliminations)

            if (validVotedForIds.isEmpty()) return

            val eliminatedPlayers = mutableListOf<PlayerState>()
            var globalEliminationCounter = currentState.eliminationHistory.size
            val newEliminationRecords = mutableListOf<EliminationRecord>()

            val updatedPlayers =
                currentState.players.map { player ->
                    if (validVotedForIds.contains(player.id) && !player.isEliminated) {
                        globalEliminationCounter++
                        val eliminated = player.copy(isEliminated = true)
                        eliminatedPlayers.add(eliminated)
                        newEliminationRecords.add(
                            EliminationRecord(
                                roundNumber = currentState.currentRoundNumber,
                                eliminationOrder = globalEliminationCounter,
                                player = eliminated,
                            ),
                        )
                        eliminated
                    } else {
                        player
                    }
                }

            updateState {
                it.copy(
                    players = updatedPlayers,
                    eliminatedInCurrentRound = eliminatedPlayers,
                    eliminationHistory = it.eliminationHistory + newEliminationRecords,
                    phase = GamePhase.VOTING_RESULTS,
                )
            }

            checkWinConditions(autoEnd = true)
        }

        /**
         * Starts the next voting round after viewing results, transitioning back to Discussion.
         */
        fun startNextVotingRound() {
            updateState { currentState ->
                if (currentState.phase != GamePhase.VOTING_RESULTS) return@updateState currentState
                currentState.copy(
                    currentRoundNumber = currentState.currentRoundNumber + 1,
                    eliminatedInCurrentRound = emptyList(),
                )
            }
            startDiscussion()
        }

        fun proceedFromResults() {
            endGame()
        }

        /**
         * Evaluates win conditions using deterministic precedence so edge cases resolve consistently.
         *
         * Precedence:
         * 1. Crewmates win immediately when no active imposters remain.
         * 2. Imposters win if only two active players remain and at least one is an imposter.
         * 3. Otherwise, imposters win when they reach parity with crewmates.
         */
        private fun checkWinConditions(autoEnd: Boolean = true): Boolean {
            val activePlayers = _uiState.value.players.filter { !it.isEliminated }
            val activeImposters = activePlayers.filter { it.isImposter }
            val activeCrewmates = activePlayers.filter { !it.isImposter }

            val winner =
                when {
                    activeImposters.isEmpty() -> "Crewmates"
                    activePlayers.size <= 2 && activeImposters.isNotEmpty() -> "Imposters"
                    activeImposters.size >= activeCrewmates.size -> "Imposters"
                    else -> null
                }

            if (winner != null) {
                updateState { it.copy(winner = winner, phase = GamePhase.RESULT) }
                gameTimerJob?.cancel()
                return true
            }

            return false
        }

        /**
         * Immediately stops all timers and enforces the end of the game,
         * determining the winner if one isn't already set.
         */
        fun endGame() {
            val state = _uiState.value
            if (state.phase == GamePhase.RESULT || state.phase == GamePhase.SETUP) return

            gameTimerJob?.cancel()
            timerJob?.cancel()
            if (state.winner == null) {
                // Forced endings still map to a valid winner for result-screen consistency.
                val activePlayers = state.players.filter { !it.isEliminated }
                val activeImposters = activePlayers.filter { it.isImposter }
                val winner = if (activeImposters.isEmpty()) "Crewmates" else "Imposters"
                updateState { it.copy(winner = winner, phase = GamePhase.RESULT) }
            } else {
                updateState {
                    it.copy(phase = GamePhase.RESULT)
                }
            }
        }

        fun shuffleLobbyPlayers() {
            val currentPlayers = _uiState.value.players
            val shuffled = currentPlayers.shuffled()
            updateState { it.copy(players = shuffled) }
            saveConfig()
        }

        fun reorderPlayers(
            fromIndex: Int,
            toIndex: Int,
        ) {
            val players = _uiState.value.players.toMutableList()
            val item = players.removeAt(fromIndex)
            players.add(toIndex, item)
            updateState { it.copy(players = players) }
            saveConfig()
        }

        /**
         * Completely resets the game state back to [GamePhase.SETUP],
         * clearing roles, votes, eliminations, and elapsed times, while
         * maintaining the players and configuration.
         */
        fun resetGame() {
            timerJob?.cancel()
            gameTimerJob?.cancel()
            updateState { currentState ->
                // Keep lobby identities while removing transient round-specific flags.
                val resetPlayers =
                    currentState.players.map { p ->
                        p.copy(
                            isImposter = false,
                            hasVoted = false,
                            isEliminated = false,
                            isReady = true,
                        )
                    }

                GameState(
                    players = resetPlayers,
                    category = currentState.category,
                    imposterCount = currentState.imposterCount,
                    phase = GamePhase.SETUP,
                    elapsedTime = 0,
                    currentRoundNumber = 1,
                    roundStartTime = 0,
                    totalGameTime = 0,
                    eliminationHistory = emptyList(),
                )
            }
        }
    }
