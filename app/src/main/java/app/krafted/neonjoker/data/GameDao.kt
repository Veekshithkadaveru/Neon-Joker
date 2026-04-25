package app.krafted.neonjoker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM game_save WHERE id = 1")
    fun observeGameSave(): Flow<GameSave?>

    @Upsert
    suspend fun upsertGameSave(gameSave: GameSave)

    @Query("DELETE FROM game_save WHERE id = 1")
    suspend fun deleteGameSave()

    @Insert
    suspend fun insertScoreRecord(scoreRecord: ScoreRecord)

    @Query(
        """
        SELECT * FROM score_records
        ORDER BY moves ASC, score DESC, createdAtMillis ASC
        LIMIT :limit
        """
    )
    fun observeTopScores(limit: Int): Flow<List<ScoreRecord>>
}
