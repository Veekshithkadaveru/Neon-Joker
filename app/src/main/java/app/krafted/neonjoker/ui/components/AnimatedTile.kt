package app.krafted.neonjoker.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import app.krafted.neonjoker.R
import app.krafted.neonjoker.ui.theme.tierNeonColor
import app.krafted.neonjoker.viewmodel.TileData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private val CellShape = CutCornerShape(10.dp)

@Composable
fun AnimatedTile(
    tile: TileData,
    cellSizePx: Float,
    cellSizeDp: Dp,
    moveGeneration: Long,
) {
    val targetCol = tile.cellIndex % 4
    val targetRow = tile.cellIndex / 4
    val prevCol = tile.previousIndex % 4
    val prevRow = tile.previousIndex / 4

    val offsetX = remember(tile.id) { Animatable(prevCol * cellSizePx) }
    val offsetY = remember(tile.id) { Animatable(prevRow * cellSizePx) }

    LaunchedEffect(tile.id, moveGeneration) {
        launch {
            offsetX.snapTo(prevCol * cellSizePx)
            offsetX.animateTo(
                targetValue = targetCol * cellSizePx,
                animationSpec = tween(150, easing = FastOutSlowInEasing)
            )
        }
        launch {
            offsetY.snapTo(prevRow * cellSizePx)
            offsetY.animateTo(
                targetValue = targetRow * cellSizePx,
                animationSpec = tween(150, easing = FastOutSlowInEasing)
            )
        }
    }

    val scale = remember(tile.id) { Animatable(if (tile.isNew) 0f else 1f) }

    LaunchedEffect(tile.id, moveGeneration) {
        if (tile.isNew) {
            scale.snapTo(0f)
            delay(160)
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(dampingRatio = 0.5f, stiffness = 500f)
            )
        } else if (tile.isMerged) {
            scale.snapTo(1f)
            delay(120)
            scale.animateTo(
                targetValue = 1.15f,
                animationSpec = tween(80)
            )
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(dampingRatio = 0.5f, stiffness = 600f)
            )
        }
    }

    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
            .size(cellSizeDp)
            .padding(4.dp)
            .scale(scale.value)
    ) {
        TileVisual(tier = tile.tier)
    }
}

private fun tierDrawable(tier: Int): Int = when (tier) {
    1 -> R.drawable.sym_1
    2 -> R.drawable.sym_2
    3 -> R.drawable.sym_3
    4 -> R.drawable.sym_4
    5 -> R.drawable.sym_5
    6 -> R.drawable.sym_6
    7 -> R.drawable.sym_7
    else -> R.drawable.sym_1
}

@Composable
fun TileVisual(tier: Int, modifier: Modifier = Modifier) {
    val bg = tierNeonColor(tier)
    val glowIntensity = 0.35f + (tier * 0.06f).coerceAtMost(0.25f)

    Surface(
        modifier = modifier
            .fillMaxSize()
            .neonGlow(
                color = bg,
                cornerRadius = 8.dp,
                blurRadius = 18.dp,
                spreadAlpha = glowIntensity
            ),
        shape = CellShape,
        color = Color.Transparent,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.35f)),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(bg, bg.copy(alpha = 0.72f))
                    ),
                    CellShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = tierDrawable(tier)),
                contentDescription = "Tier $tier",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}
