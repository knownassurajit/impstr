package com.example.imposter.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_results")
data class GameResult(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val winner: String, // "Crewmates" or "Imposter"
    val imposterName: String,
    val word: String,
    val durationSeconds: Long,
    val timestamp: Long
)
