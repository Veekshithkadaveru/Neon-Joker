package app.krafted.neonjoker.ui

import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import app.krafted.neonjoker.game.Direction
import app.krafted.neonjoker.ui.components.TierProgress
import app.krafted.neonjoker.ui.components.cyberpunkGameGradientBrush
import app.krafted.neonjoker.ui.components.neonGlow
import app.krafted.neonjoker.ui.theme.CyberBg
import app.krafted.neonjoker.ui.theme.CyberOnDarkMuted
import app.krafted.neonjoker.ui.theme.CyberOutline
import app.krafted.neonjoker.ui.theme.CyberSurface
import app.krafted.neonjoker.ui.theme.NeonCyan
import app.krafted.neonjoker.ui.theme.NeonGold
import app.krafted.neonjoker.ui.theme.NeonRed
import app.krafted.neonjoker.ui.theme.tierNeonColor
import app.krafted.neonjoker.viewmodel.GameViewModel
import kotlinx.coroutines.delay
import kotlin.math.abs

private val HudShape = CutCornerShape(topStart = 14.dp, bottomEnd = 14.dp)
private val GridFrameShape = CutCornerShape(16.dp)
private val CellShape = CutCornerShape(10.dp)
private val OverlayCardShape = CutCornerShape(topStart = 24.dp, bottomEnd = 24.dp)

private const val SWIPE_THRESHOLD_PX = 48f

@Composable
fun GameRoute(
    onBack: () -> Unit,
    onLeaderboard: () -> Unit,
    viewModel: GameViewModel = hiltViewModel(
        viewModelStoreOwner = LocalContext.current as ComponentActivity
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    GameScreen(
        grid = uiState.grid,
        score = uiState.score,
        bestScore = uiState.bestScore,
        canUndo = uiState.canUndo,
        isWon = uiState.isWon,
        isGameOver = uiState.isGameOver,
        onSwipe = viewModel::onSwipe,
        onUndo = viewModel::undo,
        onNewGame = viewModel::startNewGame,
        onBack = onBack,
        onLeaderboard = onLeaderboard
    )
}

@Composable
fun GameScreen(
    grid: List<Int>,
    score: Int,
    bestScore: Int,
    canUndo: Boolean,
    isWon: Boolean,
    isGameOver: Boolean,
    onSwipe: (Direction) -> Unit,
    onUndo: () -> Unit,
    onNewGame: () -> Unit,
    onBack: () -> Unit,
    onLeaderboard: () -> Unit
) {
    val highestTier = grid.maxOrNull() ?: 0

    val animatedScore by animateIntAsState(
        targetValue = score,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
        label = "score"
    )
    val animatedBest by animateIntAsState(
        targetValue = bestScore,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
        label = "best"
    )

    var scoreDelta by remember { mutableIntStateOf(0) }
    var showDelta by remember { mutableStateOf(false) }
    var lastScore by remember { mutableIntStateOf(score) }

    LaunchedEffect(score) {
        val delta = score - lastScore
        lastScore = score
        if (delta > 0) {
            scoreDelta = delta
            showDelta = true
            delay(1000L)
            showDelta = false
        } else {
            showDelta = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(cyberpunkGameGradientBrush())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    HudStatCard(
                        label = "Score",
                        value = animatedScore.toString(),
                        accent = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth()
                    )
                    ScoreDeltaBadge(
                        visible = showDelta,
                        delta = scoreDelta,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(end = 6.dp)
                    )
                }
                HudStatCard(
                    label = "Best",
                    value = animatedBest.toString(),
                    accent = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NeonOutlinedButton(
                    text = "Undo",
                    onClick = onUndo,
                    enabled = canUndo,
                    accent = NeonCyan
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NeonOutlinedButton(
                        text = "Home",
                        onClick = onBack,
                        enabled = true,
                        accent = MaterialTheme.colorScheme.tertiary
                    )
                    NeonOutlinedButton(
                        text = "Leaderboard",
                        onClick = onLeaderboard,
                        enabled = true,
                        accent = MaterialTheme.colorScheme.primary
                    )
                }
            }

            TierProgress(
                highestTierOnBoard = highestTier,
                modifier = Modifier.padding(top = 14.dp, bottom = 10.dp)
            )

            GameGrid(
                grid = grid,
                onSwipe = onSwipe,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )

            Spacer(modifier = Modifier.weight(1f, fill = true))
        }

        GameOverlay(
            isWon = isWon,
            isGameOver = isGameOver,
            score = score,
            onNewGame = onNewGame,
            onBack = onBack
        )
    }
}

private fun Modifier.swipeGesture(
    onSwipe: (Direction) -> Unit
): Modifier = pointerInput(onSwipe) {
    var totalX = 0f
    var totalY = 0f
    detectDragGestures(
        onDragStart = {
            totalX = 0f
            totalY = 0f
        },
        onDrag = { change, dragAmount ->
            change.consume()
            totalX += dragAmount.x
            totalY += dragAmount.y
        },
        onDragEnd = {
            if (abs(totalX) > SWIPE_THRESHOLD_PX || abs(totalY) > SWIPE_THRESHOLD_PX) {
                if (abs(totalX) > abs(totalY)) {
                    onSwipe(if (totalX > 0) Direction.RIGHT else Direction.LEFT)
                } else {
                    onSwipe(if (totalY > 0) Direction.DOWN else Direction.UP)
                }
            }
        }
    )
}

@Composable
private fun ScoreDeltaBadge(
    visible: Boolean,
    delta: Int,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(120)) + slideInVertically { -it / 2 },
        exit = fadeOut(tween(400)) + slideOutVertically { -it },
        modifier = modifier
    ) {
        Text(
            text = "+$delta",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = NeonGold
        )
    }
}

@Composable
private fun NeonOutlinedButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean,
    accent: Color
) {
    val borderAlpha = if (enabled) 0.85f else 0.28f
    val glowMod = if (enabled) {
        Modifier.neonGlow(
            color = accent.copy(alpha = 0.18f),
            cornerRadius = 6.dp,
            blurRadius = 8.dp,
            spreadAlpha = 0.18f
        )
    } else {
        Modifier
    }

    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = glowMod,
        border = BorderStroke(
            width = 1.dp,
            color = accent.copy(alpha = borderAlpha)
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = accent.copy(alpha = if (enabled) 1f else 0.45f)
        ),
        shape = CutCornerShape(8.dp),
        contentPadding = ButtonDefaults.TextButtonContentPadding
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun HudStatCard(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .neonGlow(
                color = accent.copy(alpha = 0.4f),
                cornerRadius = 6.dp,
                blurRadius = 12.dp,
                spreadAlpha = 0.35f
            ),
        shape = HudShape,
        color = CyberBg.copy(alpha = 0.55f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.55f)),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Column {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color.Transparent,
                                accent.copy(alpha = 0.85f),
                                accent.copy(alpha = 0.6f),
                                Color.Transparent
                            )
                        )
                    )
            )
            Column(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = label.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = accent.copy(alpha = 0.85f)
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun GameGrid(
    grid: List<Int>,
    onSwipe: (Direction) -> Unit,
    modifier: Modifier = Modifier
) {
    val frameGlow = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
    Surface(
        modifier = modifier
            .neonGlow(
                color = frameGlow,
                cornerRadius = 12.dp,
                blurRadius = 18.dp,
                spreadAlpha = 0.28f
            )
            .swipeGesture(onSwipe),
        shape = GridFrameShape,
        color = CyberBg.copy(alpha = 0.45f),
        border = BorderStroke(1.dp, CyberOutline.copy(alpha = 0.75f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            for (r in 0 until 4) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    for (c in 0 until 4) {
                        val v = grid[r * 4 + c]
                        GridCell(
                            tier = v,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GridCell(
    tier: Int,
    modifier: Modifier = Modifier
) {

    val scale = remember { Animatable(1f) }
    LaunchedEffect(tier) {
        if (tier > 0) {
            scale.snapTo(0.65f)
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = 0.45f,
                    stiffness = 420f
                )
            )
        }
    }

    if (tier == 0) {

        Box(
            modifier = modifier
                .border(
                    width = 1.dp,
                    color = CyberOutline.copy(alpha = 0.5f),
                    shape = CellShape
                )
                .background(CyberSurface.copy(alpha = 0.10f), CellShape)
        )
    } else {
        val bg = tierNeonColor(tier)
        val fg = if (tier >= 7) CyberBg else Color.White
        val glowIntensity = 0.35f + (tier * 0.06f).coerceAtMost(0.25f)

        Surface(
            modifier = modifier
                .scale(scale.value)
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
                            colors = listOf(
                                bg,
                                bg.copy(alpha = 0.72f)
                            )
                        ),
                        CellShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tier.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = fg
                )
            }
        }
    }
}

@Composable
private fun GameOverlay(
    isWon: Boolean,
    isGameOver: Boolean,
    score: Int,
    onNewGame: () -> Unit,
    onBack: () -> Unit
) {
    AnimatedVisibility(
        visible = isWon || isGameOver,
        enter = fadeIn(tween(500)),
        exit = fadeOut(tween(300))
    ) {
        val accent = if (isWon) NeonGold else NeonRed

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CyberBg.copy(alpha = 0.80f)),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .padding(horizontal = 36.dp)
                    .neonGlow(
                        color = accent,
                        cornerRadius = 18.dp,
                        blurRadius = 28.dp,
                        spreadAlpha = 0.35f
                    ),
                shape = OverlayCardShape,
                color = CyberSurface.copy(alpha = 0.94f),
                border = BorderStroke(2.dp, accent.copy(alpha = 0.65f))
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (isWon) "ULTIMATE JOKER" else "GAME OVER",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = accent,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = if (isWon) "You reached the highest tier!"
                        else "No moves remaining",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CyberOnDarkMuted,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Score: $score",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        NeonOutlinedButton(
                            text = "New Game",
                            onClick = onNewGame,
                            enabled = true,
                            accent = accent
                        )
                        NeonOutlinedButton(
                            text = "Home",
                            onClick = onBack,
                            enabled = true,
                            accent = NeonCyan
                        )
                    }
                }
            }
        }
    }
}
