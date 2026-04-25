package app.krafted.neonjoker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import app.krafted.neonjoker.data.ScoreRecord
import app.krafted.neonjoker.ui.components.SecondaryGhostButton
import app.krafted.neonjoker.ui.theme.NeonGold
import app.krafted.neonjoker.viewmodel.LeaderboardViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val StageBg = Color(0xFF0D0B1E)
private val SilverMedal = Color(0xFFC8C8D4)
private val BronzeMedal = Color(0xFFCD7F32)
private val RowShape = RoundedCornerShape(10.dp)

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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StageBg)
            .padding(horizontal = 18.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "LEADERBOARD",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        )

        if (scores.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No scores yet.\nPlay to claim the top spot!",
                    color = Color.White.copy(alpha = 0.3f),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(7.dp),
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

        SecondaryGhostButton(text = "← BACK", onClick = onBack)
    }
}

@Composable
private fun LeaderboardRow(index: Int, record: ScoreRecord) {
    val (rankText, rankColor) = when (index) {
        0 -> "🥇" to NeonGold
        1 -> "🥈" to SilverMedal
        2 -> "🥉" to BronzeMedal
        else -> "#${index + 1}" to Color.White.copy(alpha = 0.3f)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RowShape)
            .background(Color.White.copy(alpha = 0.05f), RowShape)
            .border(1.dp, Color.White.copy(alpha = 0.07f), RowShape)
            .padding(horizontal = 14.dp, vertical = 11.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = rankText,
            color = rankColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(40.dp)
        )
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = record.playerName,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${record.moves} moves",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = formatScoreDate(record.createdAtMillis),
            color = Color.White.copy(alpha = 0.25f),
            fontSize = 10.sp
        )
    }
}

private val ScoreDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")

private fun formatScoreDate(millis: Long): String =
    Instant.ofEpochMilli(millis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(ScoreDateFormatter)
