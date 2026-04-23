package app.krafted.neonjoker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "score_records")
data class ScoreRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val score: Int,
    val createdAtMillis: Long = System.currentTimeMillis()
)
