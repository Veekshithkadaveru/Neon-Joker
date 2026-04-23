package app.krafted.neonjoker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.krafted.neonjoker.data.GameDao
import app.krafted.neonjoker.data.GameSave
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GameUiState(
    val grid: List<Int> = List(16) { 0 },
    val score: Int = 0,
    val bestScore: Int = 0,
    val canContinue: Boolean = false
)

@HiltViewModel
class GameViewModel @Inject constructor(
    private val gameDao: GameDao
) : ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            gameDao.observeGameSave().collect { save ->
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
    }

    fun startNewGame() {
        val bestScore = _uiState.value.bestScore
        _uiState.value = GameUiState(bestScore = bestScore, canContinue = true)
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

    private fun parseGridState(raw: String): List<Int> {
        val values = raw.split(",").mapNotNull { it.toIntOrNull() }
        return if (values.size == 16) values else List(16) { 0 }
    }
}
