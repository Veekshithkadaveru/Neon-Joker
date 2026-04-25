package app.krafted.neonjoker.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import app.krafted.neonjoker.data.ScoreRecord
import app.krafted.neonjoker.ui.components.NeonMenuButton
import app.krafted.neonjoker.ui.components.cyberpunkGameGradientBrush
import app.krafted.neonjoker.ui.components.neonGlow
import app.krafted.neonjoker.ui.theme.CyberOnDarkMuted
import app.krafted.neonjoker.ui.theme.CyberSurfaceVariant
import app.krafted.neonjoker.ui.theme.NeonCyan
import app.krafted.neonjoker.ui.theme.NeonGold
import app.krafted.neonjoker.ui.theme.NeonPink
import app.krafted.neonjoker.ui.theme.tierNeonColor
import app.krafted.neonjoker.viewmodel.LeaderboardViewModel
import kotlinx.coroutines.delay

private val RowShape = CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp)

@Composable
fun LeaderboardRoute(
    onBack: () -> Unit,
    viewModel: LeaderboardViewModel = hiltViewModel()
) {
    val scores by viewModel.topScores.collectAsState()
    LeaderboardScreen(scores = scores, onBack = onBack)
}

@Composable
fun LeaderboardScreen(
    scores: List<ScoreRecord>,
    onBack: () -> Unit
) {
    val titleShown = remember { MutableTransitionState(false) }
    val listShown = remember { MutableTransitionState(false) }
    LaunchedEffect(Unit) {
        titleShown.targetState = true
        delay(90)
        listShown.targetState = true
    }
    val enter = fadeIn(tween(380, easing = FastOutSlowInEasing)) +
        slideInVertically(tween(380, easing = FastOutSlowInEasing)) { it / 8 }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(cyberpunkGameGradientBrush())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(visibleState = titleShown, enter = enter) {
                Text(
                    text = "LEADERBOARD",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        shadow = Shadow(
                            color = NeonGold.copy(alpha = 0.75f),
                            offset = Offset.Zero,
                            blurRadius = 18f
                        )
                    ),
                    color = NeonGold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
            AnimatedVisibility(
                visibleState = listShown,
                enter = enter,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (scores.isEmpty()) {
                        Text(
                            text = "No scores yet.\nPlay a round!",
                            style = MaterialTheme.typography.titleMedium,
                            color = CyberOnDarkMuted,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(32.dp)
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            itemsIndexed(
                                items = scores,
                                key = { _, record -> record.id }
                            ) { index, record ->
                                val rank = index + 1
                                val accent = when (rank) {
                                    1 -> NeonGold
                                    2 -> NeonCyan
                                    3 -> NeonPink
                                    else -> tierNeonColor(((rank - 1) % 7) + 1)
                                }
                                LeaderboardRow(
                                    rank = rank,
                                    score = record.score,
                                    accent = accent
                                )
                            }
                        }
                    }
                }
            }
            NeonMenuButton(
                text = "BACK",
                onClick = onBack,
                enabled = true,
                accent = NeonGold,
                fillFraction = 0.88f,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@Composable
private fun LeaderboardRow(
    rank: Int,
    score: Int,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RowShape,
        color = CyberSurfaceVariant.copy(alpha = 0.88f),
        modifier = modifier
            .fillMaxWidth()
            .neonGlow(
                accent,
                cornerRadius = 8.dp,
                blurRadius = 12.dp,
                spreadAlpha = 0.34f
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "#$rank",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = accent
            )
            Text(
                text = score.toString(),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
