package app.krafted.neonjoker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_save")
data class GameSave(
    @PrimaryKey val id: Int = 1,
    val gridState: String,
    val score: Int,
    val moves: Int,
    val lowestMoves: Int,
    val updatedAtMillis: Long = System.currentTimeMillis()
)
