package app.krafted.neonjoker.viewmodel

import app.krafted.neonjoker.data.GameDao
import app.krafted.neonjoker.data.GameSave
import app.krafted.neonjoker.data.ScoreRecord
import app.krafted.neonjoker.game.Direction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

private class FakeGameDao : GameDao {
    private val savesFlow = MutableStateFlow<GameSave?>(null)
    private val scores = mutableListOf<ScoreRecord>()

    override fun observeGameSave(): Flow<GameSave?> = savesFlow

    override suspend fun upsertGameSave(gameSave: GameSave) {
        savesFlow.value = gameSave
    }

    override suspend fun deleteGameSave() {
        savesFlow.value = null
    }

    override suspend fun insertScoreRecord(scoreRecord: ScoreRecord) {
        scores.add(scoreRecord)
    }

    override fun observeTopScores(limit: Int): Flow<List<ScoreRecord>> {
        return MutableStateFlow(scores.sortedByDescending { it.score }.take(limit))
    }

    fun getLatestSave(): GameSave? = savesFlow.value
    fun getScores(): List<ScoreRecord> = scores.toList()
}

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeDao: FakeGameDao
    private lateinit var viewModel: GameViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeDao = FakeGameDao()
        viewModel = GameViewModel(fakeDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun startNewGameSpawnsTwoTiles() = runTest {
        viewModel.startNewGame()
        advanceUntilIdle()
        val nonZero = viewModel.uiState.value.grid.count { it != 0 }
        assertEquals(2, nonZero)
    }

    @Test
    fun startNewGameResetsScorePreservesBestScore() = runTest {
        viewModel.startNewGame()
        advanceUntilIdle()
        for (dir in Direction.entries) {
            viewModel.onSwipe(dir)
            advanceUntilIdle()
        }
        val bestAfterPlay = viewModel.uiState.value.bestScore
        viewModel.startNewGame()
        advanceUntilIdle()
        assertEquals(0, viewModel.uiState.value.score)
        assertEquals(bestAfterPlay, viewModel.uiState.value.bestScore)
    }

    @Test
    fun initialStateHasNoUndo() = runTest {
        viewModel.startNewGame()
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.canUndo)
    }

    @Test
    fun onSwipeThatMovesEnablesUndo() = runTest {
        viewModel.startNewGame()
        advanceUntilIdle()
        val gridBefore = viewModel.uiState.value.grid.toList()
        for (dir in Direction.entries) {
            viewModel.onSwipe(dir)
            advanceUntilIdle()
            if (viewModel.uiState.value.grid != gridBefore) break
        }
        if (viewModel.uiState.value.grid != gridBefore) {
            assertTrue(viewModel.uiState.value.canUndo)
        }
    }

    @Test
    fun undoRestoresPreviousState() = runTest {
        viewModel.startNewGame()
        advanceUntilIdle()
        val gridBefore = viewModel.uiState.value.grid.toList()
        val scoreBefore = viewModel.uiState.value.score

        for (dir in Direction.entries) {
            viewModel.onSwipe(dir)
            advanceUntilIdle()
            if (viewModel.uiState.value.canUndo) break
        }

        if (viewModel.uiState.value.canUndo) {
            viewModel.undo()
            advanceUntilIdle()
            assertEquals(gridBefore, viewModel.uiState.value.grid)
            assertEquals(scoreBefore, viewModel.uiState.value.score)
            assertFalse(viewModel.uiState.value.canUndo)
        }
    }

    @Test
    fun doubleUndoIsNoOp() = runTest {
        viewModel.startNewGame()
        advanceUntilIdle()
        for (dir in Direction.entries) {
            viewModel.onSwipe(dir)
            advanceUntilIdle()
            if (viewModel.uiState.value.canUndo) break
        }
        if (viewModel.uiState.value.canUndo) {
            viewModel.undo()
            advanceUntilIdle()
            val stateAfterFirstUndo = viewModel.uiState.value
            viewModel.undo()
            advanceUntilIdle()
            assertEquals(stateAfterFirstUndo, viewModel.uiState.value)
        }
    }

    @Test
    fun initialStateNotGameOverOrWon() = runTest {
        viewModel.startNewGame()
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isGameOver)
        assertFalse(viewModel.uiState.value.isWon)
    }

    @Test
    fun stateIsPersistedOnSwipe() = runTest {
        viewModel.startNewGame()
        advanceUntilIdle()
        for (dir in Direction.entries) {
            viewModel.onSwipe(dir)
            advanceUntilIdle()
            if (viewModel.uiState.value.canUndo) break
        }
        if (viewModel.uiState.value.canUndo) {
            assertNotNull(fakeDao.getLatestSave())
        }
    }

    @Test
    fun startNewGamePersistsInitialState() = runTest {
        viewModel.startNewGame()
        advanceUntilIdle()
        val save = fakeDao.getLatestSave()
        assertNotNull(save)
        assertEquals(0, save!!.score)
    }

    @Test
    fun initRestoresFromSavedGame() = runTest {
        val savedGrid = "1,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0"
        fakeDao.upsertGameSave(GameSave(gridState = savedGrid, score = 42, bestScore = 100))
        val vm = GameViewModel(fakeDao)
        advanceUntilIdle()
        assertEquals(42, vm.uiState.value.score)
        assertEquals(100, vm.uiState.value.bestScore)
        assertTrue(vm.uiState.value.canContinue)
        assertEquals(1, vm.uiState.value.grid[0])
        assertEquals(2, vm.uiState.value.grid[5])
    }
}
