package app.krafted.neonjoker.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [GameSave::class, ScoreRecord::class],
    version = 4,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
}
