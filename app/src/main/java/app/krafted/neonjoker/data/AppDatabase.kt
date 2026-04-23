package app.krafted.neonjoker.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [GameSave::class, ScoreRecord::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
}
