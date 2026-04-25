package app.krafted.neonjoker.ui

import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import app.krafted.neonjoker.R
import app.krafted.neonjoker.ui.components.PrimaryGradientButton
import app.krafted.neonjoker.ui.components.SecondaryGhostButton
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import app.krafted.neonjoker.game.Direction
import app.krafted.neonjoker.ui.components.AnimatedTile
import app.krafted.neonjoker.ui.components.TierProgress
import app.krafted.neonjoker.ui.components.cyberpunkGameGradientBrush
import app.krafted.neonjoker.ui.components.neonGlow
import app.krafted.neonjoker.ui.theme.CyberBg
import app.krafted.neonjoker.ui.theme.CyberOutline
import app.krafted.neonjoker.ui.theme.CyberSurface
import app.krafted.neonjoker.ui.theme.NeonCyan
import app.krafted.neonjoker.ui.theme.NeonGold
import app.krafted.neonjoker.ui.theme.NeonRed
import app.krafted.neonjoker.viewmodel.GameViewModel
import app.krafted.neonjoker.viewmodel.TileData
import kotlinx.coroutines.delay
import kotlin.math.abs

private val HudShape = CutCornerShape(topStart = 14.dp, bottomEnd = 14.dp)
private val GridFrameShape = CutCornerShape(16.dp)
private val CellShape = CutCornerShape(10.dp)

private const val SWIPE_THRESHOLD_PX = 48f

@Composable
fun GameRoute(
    onHome: () -> Unit,
    onLeaderboard: () -> Unit,
    viewModel: GameViewModel = hiltViewModel(
        viewModelStoreOwner = LocalContext.current as ComponentActivity
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    GameScreen(
        grid = uiState.grid,
        tiles = uiState.tiles,
        moveGeneration = uiState.moveGeneration,
        score = uiState.score,
        moves = uiState.moves,
        lowestMoves = uiState.lowestMoves,
        canUndo = uiState.canUndo,
        isWon = uiState.isWon,
        isGameOver = uiState.isGameOver,
        onSwipe = viewModel::onSwipe,
        onUndo = viewModel::undo,
        onHome = onHome,
        onLeaderboard = onLeaderboard,
        onOverlayAction = { action, name ->
            if (action == "HOME") {
                viewModel.recordScoreWithName(name)
            }
            when (action) {
                "HOME" -> onHome()
                "NEW_GAME" -> viewModel.startNewGame()
                "LEADERBOARD" -> onLeaderboard()
            }
        }
    )
}

@Composable
fun GameScreen(
    grid: List<Int>,
    tiles: List<TileData>,
    moveGeneration: Long,
    score: Int,
    moves: Int,
    lowestMoves: Int,
    canUndo: Boolean,
    isWon: Boolean,
    isGameOver: Boolean,
    onSwipe: (Direction) -> Unit,
    onUndo: () -> Unit,
    onHome: () -> Unit,
    onLeaderboard: () -> Unit,
    onOverlayAction: (String, String) -> Unit
) {
    val highestTier = grid.maxOrNull() ?: 0

    val animatedScore by animateIntAsState(
        targetValue = score,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
        label = "score"
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
                .padding(top = 48.dp, bottom = 16.dp, start = 20.dp, end = 20.dp)
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
                    label = "Moves",
                    value = moves.toString(),
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
                        onClick = onHome,
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

            GameGrid(
                tiles = tiles,
                moveGeneration = moveGeneration,
                onSwipe = onSwipe,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )

            TierProgress(
                highestTierOnBoard = highestTier,
                modifier = Modifier.padding(top = 24.dp, bottom = 10.dp)
            )

            Spacer(modifier = Modifier.weight(1f, fill = true))
        }

        GameOverlay(
            isWon = isWon,
            isGameOver = isGameOver,
            score = score,
            onOverlayAction = onOverlayAction,
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1f)
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
        color = CyberBg.copy(alpha = 0.85f),
        border = BorderStroke(1.5.dp, accent.copy(alpha = 0.75f)),
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
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun GameGrid(
    tiles: List<TileData>,
    moveGeneration: Long,
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
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            val cellSizeDp = maxWidth / 4
            val cellSizePx = with(LocalDensity.current) { cellSizeDp.toPx() }

            for (r in 0 until 4) {
                for (c in 0 until 4) {
                    EmptyCell(
                        modifier = Modifier
                            .offset(x = cellSizeDp * c, y = cellSizeDp * r)
                            .size(cellSizeDp)
                            .padding(4.dp)
                    )
                }
            }

            val sortedTiles = remember(tiles) {
                tiles.sortedBy { if (it.isMerged) 1 else 0 }
            }
            for (tile in sortedTiles) {
                key(tile.id) {
                    AnimatedTile(
                        tile = tile,
                        cellSizePx = cellSizePx,
                        cellSizeDp = cellSizeDp,
                        moveGeneration = moveGeneration,
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyCell(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .border(
                width = 1.dp,
                color = CyberOutline.copy(alpha = 0.5f),
                shape = CellShape
            )
            .background(CyberSurface.copy(alpha = 0.10f), CellShape)
    )
}

@Composable
private fun GameOverlay(
    isWon: Boolean,
    isGameOver: Boolean,
    score: Int,
    onOverlayAction: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isWon || isGameOver,
        enter = fadeIn(tween(350)),
        exit = fadeOut(tween(300)),
        modifier = modifier
    ) {
        val absorber = remember { MutableInteractionSource() }
        var playerName by remember { mutableStateOf("") }
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xE603020E))
                .clickable(
                    interactionSource = absorber,
                    indication = null,
                    enabled = true,
                    onClick = {}
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 36.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                if (isWon) {
                    val transition = rememberInfiniteTransition(label = "winPulse")
                    val scale by transition.animateFloat(
                        initialValue = 1f,
                        targetValue = 1.04f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1500, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "winScale"
                    )
                    val glowAlpha by transition.animateFloat(
                        initialValue = 0.45f,
                        targetValue = 0.85f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1500, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "winGlow"
                    )
                    Image(
                        painter = painterResource(id = R.drawable.sym_7),
                        contentDescription = null,
                        modifier = Modifier
                            .size(120.dp)
                            .graphicsLayer { scaleX = scale; scaleY = scale }
                            .neonGlow(
                                color = NeonGold.copy(alpha = glowAlpha),
                                cornerRadius = 60.dp,
                                blurRadius = 36.dp,
                                spreadAlpha = glowAlpha
                            )
                    )
                    Text(
                        text = "YOU WIN! 👑",
                        style = TextStyle(
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = NeonGold,
                            shadow = Shadow(color = NeonGold, blurRadius = 30f)
                        ),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "SCORE",
                        style = TextStyle(
                            fontSize = 10.sp,
                            letterSpacing = 3.sp,
                            color = Color.White.copy(alpha = 0.4f),
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = score.toString(),
                        style = TextStyle(
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Black,
                            color = NeonGold,
                            shadow = Shadow(color = NeonGold, blurRadius = 30f)
                        )
                    )
                    OutlinedTextField(
                        value = playerName,
                        onValueChange = { playerName = it },
                        placeholder = { Text("Enter your name", color = Color.White.copy(alpha=0.5f)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedIndicatorColor = NeonGold,
                            unfocusedIndicatorColor = NeonGold.copy(alpha = 0.5f),
                            cursorColor = NeonGold
                        ),
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )
                    PrimaryGradientButton(
                        text = "⚡ NEW GAME",
                        onClick = { onOverlayAction("NEW_GAME", playerName) }
                    )
                    SecondaryGhostButton(
                        text = "🏆 SAVE AND EXIT",
                        onClick = { onOverlayAction("HOME", playerName) }
                    )
                } else {
                    Text(
                        text = "GAME OVER",
                        style = TextStyle(
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = NeonRed,
                            shadow = Shadow(color = NeonRed, blurRadius = 30f)
                        ),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "FINAL SCORE",
                        style = TextStyle(
                            fontSize = 10.sp,
                            letterSpacing = 3.sp,
                            color = Color.White.copy(alpha = 0.4f),
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = score.toString(),
                        style = TextStyle(
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Black,
                            color = NeonGold,
                            shadow = Shadow(color = NeonGold, blurRadius = 30f)
                        )
                    )
                    OutlinedTextField(
                        value = playerName,
                        onValueChange = { playerName = it },
                        placeholder = { Text("Enter your name", color = Color.White.copy(alpha=0.5f)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedIndicatorColor = NeonRed,
                            unfocusedIndicatorColor = NeonRed.copy(alpha = 0.5f),
                            cursorColor = NeonRed
                        ),
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )
                    PrimaryGradientButton(
                        text = "⟳ NEW GAME",
                        onClick = { onOverlayAction("NEW_GAME", playerName) }
                    )
                    SecondaryGhostButton(
                        text = "← SAVE AND EXIT",
                        onClick = { onOverlayAction("HOME", playerName) }
                    )
                }
            }
        }
    }
}
