package app.krafted.neonjoker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.krafted.neonjoker.data.GameDao
import app.krafted.neonjoker.data.GameSave
import app.krafted.neonjoker.data.ScoreRecord
import app.krafted.neonjoker.game.Direction
import app.krafted.neonjoker.game.Grid
import app.krafted.neonjoker.game.GridEngine
import app.krafted.neonjoker.game.TileSpawner
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private data class UndoSnapshot(val grid: Grid, val score: Int)

data class GameUiState(
    val grid: List<Int> = List(16) { 0 },
    val score: Int = 0,
    val bestScore: Int = 0,
    val canContinue: Boolean = false,
    val isGameOver: Boolean = false,
    val isWon: Boolean = false,
    val canUndo: Boolean = false
)

@HiltViewModel
class GameViewModel @Inject constructor(
    private val gameDao: GameDao
) : ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val spawner = TileSpawner()
    private var undoSnapshot: UndoSnapshot? = null

    init {
        viewModelScope.launch {
            val save = gameDao.observeGameSave().firstOrNull()
            if (save != null) {
                _uiState.update {
                    it.copy(
                        grid = parseGridState(save.gridState),
                        score = save.score,
                        bestScore = maxOf(it.bestScore, save.bestScore),
                        canContinue = true
                    )
                }
            }
        }
    }

    fun onSwipe(direction: Direction) {
        val state = _uiState.value
        if (state.isGameOver || state.isWon) return

        val currentGrid = Grid(state.grid.toIntArray())
        val result = GridEngine.slide(currentGrid, direction)
        if (!result.moved) return

        undoSnapshot = UndoSnapshot(currentGrid, state.score)

        val afterSpawn = spawner.spawn(result.grid)
        val newScore = state.score + result.scoreDelta
        val newBest = maxOf(state.bestScore, newScore)
        val won = afterSpawn.values.any { it >= Grid.MAX_TIER }
        val gameOver = !won && isGameOver(afterSpawn)

        _uiState.value = state.copy(
            grid = afterSpawn.values.toList(),
            score = newScore,
            bestScore = newBest,
            isWon = won,
            isGameOver = gameOver,
            canUndo = true,
            canContinue = !gameOver
        )

        persistCurrentState()
        if (gameOver) recordScore(newScore)
    }

    fun undo() {
        val snapshot = undoSnapshot ?: return
        undoSnapshot = null
        _uiState.update {
            it.copy(
                grid = snapshot.grid.values.toList(),
                score = snapshot.score,
                canUndo = false,
                isGameOver = false,
                isWon = false
            )
        }
        persistCurrentState()
    }

    fun startNewGame() {
        undoSnapshot = null
        val bestScore = _uiState.value.bestScore
        var grid = Grid.empty()
        grid = spawner.spawn(grid)
        grid = spawner.spawn(grid)
        _uiState.value = GameUiState(
            grid = grid.values.toList(),
            bestScore = bestScore,
            canContinue = true
        )
        persistCurrentState()
    }

    fun persistCurrentState() {
        viewModelScope.launch {
            val state = _uiState.value
            gameDao.upsertGameSave(
                GameSave(
                    gridState = state.grid.joinToString(separator = ","),
                    score = state.score,
                    bestScore = state.bestScore
                )
            )
        }
    }

    private fun recordScore(score: Int) {
        viewModelScope.launch {
            gameDao.insertScoreRecord(ScoreRecord(score = score))
        }
    }

    private fun isGameOver(grid: Grid): Boolean {
        if (spawner.hasEmptyCell(grid)) return false
        return Direction.entries.none { GridEngine.slide(grid, it).moved }
    }

    private fun parseGridState(raw: String): List<Int> {
        val values = raw.split(",").mapNotNull { it.toIntOrNull() }
        return if (values.size == 16) values else List(16) { 0 }
    }
}
