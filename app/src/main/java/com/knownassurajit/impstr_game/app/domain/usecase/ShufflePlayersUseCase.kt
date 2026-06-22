package com.knownassurajit.impstr_game.app.domain.usecase

import com.knownassurajit.impstr_game.app.ui.viewmodel.PlayerState
import javax.inject.Inject

class ShufflePlayersUseCase
    @Inject
    constructor() {
        operator fun invoke(
            players: List<PlayerState>,
            imposterCount: Int,
        ): List<PlayerState> {
            if (players.isEmpty()) return emptyList()

            val shuffledIndices = players.indices.shuffled()
            val imposterIndices = shuffledIndices.take(imposterCount)

            return players.mapIndexed { index, player ->
                player.copy(isImposter = index in imposterIndices)
            }
        }
    }
