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

data class TileData(
    val id: Long,
    val cellIndex: Int,
    val previousIndex: Int,
    val tier: Int,
    val isNew: Boolean = false,
    val isMerged: Boolean = false,
)

private data class UndoSnapshot(
    val grid: Grid,
    val score: Int,
    val tiles: Map<Long, TileData>,
)

data class GameUiState(
    val grid: List<Int> = List(16) { 0 },
    val tiles: List<TileData> = emptyList(),
    val score: Int = 0,
    val bestScore: Int = 0,
    val canContinue: Boolean = false,
    val isGameOver: Boolean = false,
    val isWon: Boolean = false,
    val canUndo: Boolean = false,
    val moveGeneration: Long = 0,
)

@HiltViewModel
class GameViewModel @Inject constructor(
    private val gameDao: GameDao
) : ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val spawner = TileSpawner()
    private var undoSnapshot: UndoSnapshot? = null
    private var nextTileId: Long = 1L
    private var currentTiles: MutableMap<Long, TileData> = mutableMapOf()

    init {
        viewModelScope.launch {
            val save = gameDao.observeGameSave().firstOrNull()
            if (save != null) {
                val gridValues = parseGridState(save.gridState)
                val restoredTiles = buildTilesFromGrid(gridValues)
                val restoredGrid = Grid(gridValues.toIntArray())
                val won = isWon(restoredGrid)
                val gameOver = !won && isGameOver(restoredGrid)
                _uiState.update {
                    it.copy(
                        grid = gridValues,
                        tiles = restoredTiles,
                        score = save.score,
                        bestScore = maxOf(it.bestScore, save.bestScore),
                        canContinue = !won && !gameOver,
                        isWon = won,
                        isGameOver = gameOver,
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

        undoSnapshot = UndoSnapshot(currentGrid, state.score, currentTiles.toMap())

        val tileByCell = currentTiles.values.associateBy { it.cellIndex }
        val newTiles = mutableListOf<TileData>()

        for (move in result.moves) {
            if (move.merged) {
                val id = nextTileId++
                newTiles.add(
                    TileData(
                        id = id,
                        cellIndex = move.toIndex,
                        previousIndex = move.mergedFrom!!.first,
                        tier = move.tier,
                        isMerged = true,
                    )
                )
            } else {
                val sourceTile = tileByCell[move.fromIndex]
                val id = sourceTile?.id ?: nextTileId++
                newTiles.add(
                    TileData(
                        id = id,
                        cellIndex = move.toIndex,
                        previousIndex = move.fromIndex,
                        tier = move.tier,
                    )
                )
            }
        }

        val spawnResult = spawner.spawnWithIndex(result.grid)
        if (spawnResult.spawnedIndex >= 0) {
            newTiles.add(
                TileData(
                    id = nextTileId++,
                    cellIndex = spawnResult.spawnedIndex,
                    previousIndex = spawnResult.spawnedIndex,
                    tier = spawnResult.spawnedTier,
                    isNew = true,
                )
            )
        }

        currentTiles.clear()
        for (tile in newTiles) {
            currentTiles[tile.id] = tile
        }

        val afterSpawn = spawnResult.grid
        val newScore = state.score + result.scoreDelta
        val newBest = maxOf(state.bestScore, newScore)
        val won = isWon(afterSpawn)
        val gameOver = !won && isGameOver(afterSpawn)

        _uiState.value = state.copy(
            grid = afterSpawn.values.toList(),
            tiles = newTiles.toList(),
            score = newScore,
            bestScore = newBest,
            isWon = won,
            isGameOver = gameOver,
            canUndo = true,
            canContinue = !won && !gameOver,
            moveGeneration = state.moveGeneration + 1,
        )

        persistCurrentState()
        if (gameOver) recordScore(newScore)
    }

    fun undo() {
        val snapshot = undoSnapshot ?: return
        undoSnapshot = null

        currentTiles.clear()
        currentTiles.putAll(snapshot.tiles)
        val restoredTiles = snapshot.tiles.values.map {
            it.copy(isNew = false, isMerged = false, previousIndex = it.cellIndex)
        }

        _uiState.update {
            it.copy(
                grid = snapshot.grid.values.toList(),
                tiles = restoredTiles,
                score = snapshot.score,
                canUndo = false,
                isGameOver = false,
                isWon = false,
                moveGeneration = it.moveGeneration + 1,
            )
        }
        persistCurrentState()
    }

    fun startNewGame() {
        undoSnapshot = null
        val bestScore = _uiState.value.bestScore
        var grid = Grid.empty()

        val spawn1 = spawner.spawnWithIndex(grid)
        grid = spawn1.grid
        val spawn2 = spawner.spawnWithIndex(grid)
        grid = spawn2.grid

        currentTiles.clear()
        val tiles = mutableListOf<TileData>()
        if (spawn1.spawnedIndex >= 0) {
            val t = TileData(nextTileId++, spawn1.spawnedIndex, spawn1.spawnedIndex, spawn1.spawnedTier, isNew = true)
            tiles.add(t)
            currentTiles[t.id] = t
        }
        if (spawn2.spawnedIndex >= 0) {
            val t = TileData(nextTileId++, spawn2.spawnedIndex, spawn2.spawnedIndex, spawn2.spawnedTier, isNew = true)
            tiles.add(t)
            currentTiles[t.id] = t
        }

        _uiState.value = GameUiState(
            grid = grid.values.toList(),
            tiles = tiles,
            bestScore = bestScore,
            canContinue = true,
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

    private fun isWon(grid: Grid): Boolean {
        return grid.values.any { it >= Grid.MAX_TIER }
    }

    private fun parseGridState(raw: String): List<Int> {
        val values = raw.split(",").mapNotNull { it.toIntOrNull() }
        return if (values.size == 16) values else List(16) { 0 }
    }

    private fun buildTilesFromGrid(gridValues: List<Int>): List<TileData> {
        currentTiles.clear()
        val tiles = mutableListOf<TileData>()
        for (i in gridValues.indices) {
            if (gridValues[i] > 0) {
                val t = TileData(nextTileId++, i, i, gridValues[i])
                tiles.add(t)
                currentTiles[t.id] = t
            }
        }
        return tiles
    }
}
