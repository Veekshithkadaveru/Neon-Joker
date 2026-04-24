package app.krafted.neonjoker.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.krafted.neonjoker.ui.theme.CyberBg
import app.krafted.neonjoker.ui.theme.CyberOutline
import app.krafted.neonjoker.ui.theme.tierNeonColor

private val TrackShape = RoundedCornerShape(12.dp)

@Composable
fun TierProgress(
    highestTierOnBoard: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = TrackShape,
        color = CyberBg.copy(alpha = 0.55f),
        border = BorderStroke(1.dp, CyberOutline.copy(alpha = 0.65f)),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (tier in 1..7) {
                val unlocked = tier <= highestTierOnBoard
                val accent = tierNeonColor(tier)
                val fill = if (unlocked) accent.copy(alpha = 0.38f) else Color.Transparent
                val borderColor = if (unlocked) accent else CyberOutline.copy(alpha = 0.55f)
                val labelColor = if (unlocked) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 3.dp)
                        .aspectRatio(1f),
                    contentAlignment = Alignment.Center
                ) {
                    if (unlocked) {
                        Spacer(
                            modifier = Modifier
                                .matchParentSize()
                                .neonGlowCircle(
                                    color = accent,
                                    blurRadius = 12.dp,
                                    spreadAlpha = 0.45f
                                )
                        )
                    }
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clip(CircleShape)
                            .background(fill)
                            .border(
                                width = if (unlocked) 2.dp else 1.dp,
                                color = borderColor,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tier.toString(),
                            style = MaterialTheme.typography.labelMedium,
                            color = labelColor
                        )
                    }
                }
            }
        }
    }
}
