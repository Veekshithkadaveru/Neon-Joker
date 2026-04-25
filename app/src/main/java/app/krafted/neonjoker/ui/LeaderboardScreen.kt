package app.krafted.neonjoker.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import app.krafted.neonjoker.R
import app.krafted.neonjoker.data.ScoreRecord
import app.krafted.neonjoker.ui.components.SecondaryGhostButton
import app.krafted.neonjoker.ui.components.cyberpunkGameGradientBrush
import app.krafted.neonjoker.ui.components.neonGlow
import app.krafted.neonjoker.ui.theme.CyberBg
import app.krafted.neonjoker.ui.theme.CyberOutline
import app.krafted.neonjoker.ui.theme.CyberSurface
import app.krafted.neonjoker.ui.theme.NeonCyan
import app.krafted.neonjoker.ui.theme.NeonGold
import app.krafted.neonjoker.ui.theme.NeonPink
import app.krafted.neonjoker.ui.theme.NeonPurple
import app.krafted.neonjoker.viewmodel.LeaderboardViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val SilverMedal = Color(0xFFC8C8D4)
private val BronzeMedal = Color(0xFFCD7F32)
private val LeaderboardPanelShape = RoundedCornerShape(24.dp)
private val RowShape = RoundedCornerShape(18.dp)
private val RankShape = RoundedCornerShape(14.dp)

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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(cyberpunkGameGradientBrush())
            .clipToBounds()
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_main),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = 0.12f },
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(NeonPurple.copy(alpha = 0.28f), Color.Transparent),
                            center = Offset(size.width * 0.1f, size.height * 0.08f),
                            radius = size.maxDimension * 0.72f,
                        )
                    )
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(NeonCyan.copy(alpha = 0.18f), Color.Transparent),
                            center = Offset(size.width * 0.95f, size.height * 0.85f),
                            radius = size.maxDimension * 0.62f,
                        )
                    )
                }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp, vertical = 34.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LeaderboardHeader(scores = scores)

            LeaderboardPanel(
                scores = scores,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )

            SecondaryGhostButton(text = "← BACK", onClick = onBack)
        }
    }
}

@Composable
private fun LeaderboardHeader(scores: List<ScoreRecord>) {
    val bestMoves = scores.minOfOrNull { it.moves }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.sym_7),
            contentDescription = null,
            modifier = Modifier
                .size(76.dp)
                .neonGlow(
                    color = NeonGold.copy(alpha = 0.58f),
                    cornerRadius = 38.dp,
                    blurRadius = 26.dp,
                    spreadAlpha = 0.58f,
                ),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(10.dp))

        Box(contentAlignment = Alignment.Center) {
            Text(
                text = "LEADERBOARD",
                style = TextStyle(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp,
                    color = NeonCyan.copy(alpha = 0.75f),
                ),
                modifier = Modifier.graphicsLayer { alpha = 0.75f }
            )
            Text(
                text = "LEADERBOARD",
                style = TextStyle(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp,
                    brush = Brush.linearGradient(listOf(Color.White, NeonCyan, NeonPurple)),
                    shadow = Shadow(color = NeonCyan.copy(alpha = 0.8f), blurRadius = 22f),
                ),
                textAlign = TextAlign.Center
            )
        }

        Text(
            text = "LOWEST MOVES ENTER THE NEON HALL",
            style = TextStyle(
                fontSize = 9.sp,
                letterSpacing = 3.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White.copy(alpha = 0.38f),
                textAlign = TextAlign.Center,
            ),
            modifier = Modifier.padding(top = 6.dp)
        )

        if (bestMoves != null) {
            BestRunCard(bestMoves = bestMoves, runCount = scores.size)
        }
    }
}

@Composable
private fun BestRunCard(bestMoves: Int, runCount: Int) {
    Surface(
        modifier = Modifier
            .padding(top = 18.dp)
            .fillMaxWidth()
            .neonGlow(
                color = NeonGold.copy(alpha = 0.22f),
                cornerRadius = 18.dp,
                blurRadius = 18.dp,
                spreadAlpha = 0.28f,
            ),
        shape = RoundedCornerShape(18.dp),
        color = NeonGold.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, NeonGold.copy(alpha = 0.28f)),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            NeonGold.copy(alpha = 0.12f),
                            NeonPink.copy(alpha = 0.06f),
                            Color.Transparent,
                        )
                    )
                )
                .padding(horizontal = 18.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "BEST RUN",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonGold.copy(alpha = 0.72f),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                )
                Text(
                    text = "$bestMoves MOVES",
                    style = TextStyle(
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Black,
                        color = NeonGold,
                        shadow = Shadow(color = NeonGold.copy(alpha = 0.6f), blurRadius = 18f),
                    )
                )
            }
            Text(
                text = "$runCount RUNS",
                style = TextStyle(
                    fontSize = 11.sp,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.48f),
                )
            )
        }
    }
}

@Composable
private fun LeaderboardPanel(
    scores: List<ScoreRecord>,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .neonGlow(
                color = NeonPurple.copy(alpha = 0.16f),
                cornerRadius = 24.dp,
                blurRadius = 20.dp,
                spreadAlpha = 0.22f,
            ),
        shape = LeaderboardPanelShape,
        color = CyberBg.copy(alpha = 0.72f),
        border = BorderStroke(1.dp, CyberOutline.copy(alpha = 0.72f)),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RANK",
                    color = NeonCyan.copy(alpha = 0.55f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                )
                Text(
                    text = "PILOT / MOVES",
                    color = Color.White.copy(alpha = 0.35f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                )
            }

            if (scores.isEmpty()) {
                EmptyLeaderboardState(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    itemsIndexed(
                        items = scores,
                        key = { _, record -> record.id }
                    ) { index, record ->
                        LeaderboardRow(index = index, record = record)
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyLeaderboardState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.sym_1),
                contentDescription = null,
                modifier = Modifier
                    .size(68.dp)
                    .graphicsLayer { alpha = 0.62f }
                    .neonGlow(
                        color = NeonCyan.copy(alpha = 0.3f),
                        cornerRadius = 34.dp,
                        blurRadius = 20.dp,
                        spreadAlpha = 0.4f,
                    ),
                contentScale = ContentScale.Fit
            )
            Text(
                text = "NO RUNS RECORDED",
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Play a round to light up the board.",
                color = Color.White.copy(alpha = 0.38f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun LeaderboardRow(index: Int, record: ScoreRecord) {
    val rankColor = when (index) {
        0 -> NeonGold
        1 -> SilverMedal
        2 -> BronzeMedal
        else -> NeonCyan.copy(alpha = 0.55f)
    }
    val isPodium = index < 3

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isPodium) {
                    Modifier.neonGlow(
                        color = rankColor.copy(alpha = 0.18f),
                        cornerRadius = 18.dp,
                        blurRadius = 14.dp,
                        spreadAlpha = 0.25f,
                    )
                } else {
                    Modifier
                }
            ),
        shape = RowShape,
        color = CyberSurface.copy(alpha = if (isPodium) 0.54f else 0.36f),
        border = BorderStroke(1.dp, rankColor.copy(alpha = if (isPodium) 0.5f else 0.18f)),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(rankColor.copy(alpha = if (isPodium) 0.16f else 0.06f), Color.Transparent)
                    )
                )
                .padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RankShape)
                    .background(rankColor.copy(alpha = if (isPodium) 0.18f else 0.08f))
                    .border(1.dp, rankColor.copy(alpha = if (isPodium) 0.8f else 0.35f), RankShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#${index + 1}",
                    style = TextStyle(
                        color = rankColor,
                        fontSize = if (isPodium) 15.sp else 13.sp,
                        fontWeight = FontWeight.Black,
                        shadow = if (isPodium) Shadow(rankColor.copy(alpha = 0.65f), blurRadius = 14f) else null,
                    ),
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = record.playerName.ifBlank { "Anonymous" }.uppercase(),
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = formatScoreDate(record.createdAtMillis),
                    color = Color.White.copy(alpha = 0.34f),
                    fontSize = 10.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = record.moves.toString(),
                    style = TextStyle(
                        color = if (isPodium) rankColor else Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        shadow = if (isPodium) Shadow(rankColor.copy(alpha = 0.5f), blurRadius = 16f) else null,
                    ),
                )
                Text(
                    text = "MOVES",
                    color = Color.White.copy(alpha = 0.36f),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                )
            }
        }
    }
}

private val ScoreDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")

private fun formatScoreDate(millis: Long): String =
    Instant.ofEpochMilli(millis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(ScoreDateFormatter)
