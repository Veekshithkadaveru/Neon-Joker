package app.krafted.neonjoker.ui

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import app.krafted.neonjoker.viewmodel.GameViewModel

@Composable
fun HomeRoute(
    onNewGame: () -> Unit,
    onContinue: () -> Unit,
    onLeaderboard: () -> Unit,
    viewModel: GameViewModel = hiltViewModel(
        viewModelStoreOwner = LocalContext.current as ComponentActivity
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    HomeScreen(
        bestScore = uiState.bestScore,
        canContinue = uiState.canContinue,
        onNewGame = {
            viewModel.startNewGame()
            onNewGame()
        },
        onContinue = onContinue,
        onLeaderboard = onLeaderboard
    )
}

@Composable
fun HomeScreen(
    bestScore: Int,
    canContinue: Boolean,
    onNewGame: () -> Unit,
    onContinue: () -> Unit,
    onLeaderboard: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Neon Joker 2048",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Best Score: $bestScore",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 12.dp, bottom = 24.dp)
        )
        Button(onClick = onNewGame) {
            Text(text = "New Game")
        }
        Button(
            onClick = onContinue,
            enabled = canContinue,
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Text(text = "Continue")
        }
        Button(
            onClick = onLeaderboard,
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Text(text = "Leaderboard")
        }
    }
}
