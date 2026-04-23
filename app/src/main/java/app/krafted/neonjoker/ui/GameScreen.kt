package app.krafted.neonjoker.ui

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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import app.krafted.neonjoker.viewmodel.GameViewModel

@Composable
fun GameRoute(
    onBack: () -> Unit,
    onLeaderboard: () -> Unit,
    viewModel: GameViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    GameScreen(
        score = uiState.score,
        onBack = onBack,
        onLeaderboard = onLeaderboard
    )
}

@Composable
fun GameScreen(
    score: Int,
    onBack: () -> Unit,
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
            text = "Game Board",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Score: $score",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 12.dp, bottom = 24.dp)
        )
        Button(onClick = onLeaderboard) {
            Text(text = "View Leaderboard")
        }
        Button(onClick = onBack, modifier = Modifier.padding(top = 12.dp)) {
            Text(text = "Back Home")
        }
    }
}
