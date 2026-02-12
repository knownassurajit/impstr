package com.example.imposter.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM game_results ORDER BY timestamp DESC")
    fun getAllGames(): Flow<List<GameResult>>

    @Insert
    suspend fun insertGame(game: GameResult)
}
