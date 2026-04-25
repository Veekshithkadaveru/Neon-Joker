package app.krafted.neonjoker.ui

import androidx.activity.ComponentActivity
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import app.krafted.neonjoker.R
import app.krafted.neonjoker.ui.components.neonGlow
import app.krafted.neonjoker.ui.theme.NeonCyan
import app.krafted.neonjoker.ui.theme.NeonGold
import app.krafted.neonjoker.ui.theme.NeonGreen
import app.krafted.neonjoker.ui.theme.NeonPink
import app.krafted.neonjoker.ui.theme.NeonPurple
import app.krafted.neonjoker.ui.theme.NeonRed
import app.krafted.neonjoker.viewmodel.GameViewModel
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

private val StageBg = Color(0xFF0D0B1E)
private val PrimaryGradStart = Color(0xFF7C4DFF)
private val PrimaryGradEnd = Color(0xFF00B8D4)
private val TitleMidViolet = Color(0xFFB39DDB)

private val tierImages = intArrayOf(
    R.drawable.sym_1, R.drawable.sym_2, R.drawable.sym_3,
    R.drawable.sym_4, R.drawable.sym_5, R.drawable.sym_6, R.drawable.sym_7,
)
private val tierGlows = listOf(
    NeonCyan, NeonPink, NeonPurple, NeonRed, NeonGold, NeonGreen, Color.White
)

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
    onLeaderboard: () -> Unit,
) {
    val animatedBest by animateIntAsState(
        targetValue = bestScore,
        animationSpec = tween(1500, easing = FastOutSlowInEasing),
        label = "bestScore"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(StageBg)
            .clipToBounds()
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_main),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = 0.18f },
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(NeonPurple.copy(alpha = 0.25f), Color.Transparent),
                            center = Offset(size.width * 0.5f, 0f),
                            radius = size.maxDimension * 0.7f,
                        )
                    )
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(NeonCyan.copy(alpha = 0.12f), Color.Transparent),
                            center = Offset(size.width * 0.8f, size.height),
                            radius = size.maxDimension * 0.6f,
                        )
                    )
                }
        )

        HomeParticles(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            HeroZone()
            Spacer(modifier = Modifier.height(8.dp))
            Wordmark()
            Spacer(modifier = Modifier.height(10.dp))
            GemStrip()
            Spacer(modifier = Modifier.height(14.dp))
            BestScoreCard(score = animatedBest)
            Spacer(modifier = Modifier.height(14.dp))

            PrimaryGradientButton(
                text = "⚡ NEW GAME",
                onClick = onNewGame,
            )
            if (canContinue) {
                Spacer(modifier = Modifier.height(10.dp))
                SecondaryGhostButton(
                    text = "▶ CONTINUE",
                    onClick = onContinue,
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            SecondaryGhostButton(
                text = "🏆 LEADERBOARD",
                onClick = onLeaderboard,
            )
        }
    }
}

@Composable
private fun HeroZone() {
    val halo1 by rememberInfiniteTransition(label = "halo1t").animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing), RepeatMode.Restart),
        label = "halo1"
    )
    val halo2 by rememberInfiniteTransition(label = "halo2t").animateFloat(
        initialValue = 0f, targetValue = -360f,
        animationSpec = infiniteRepeatable(tween(9000, easing = LinearEasing), RepeatMode.Restart),
        label = "halo2"
    )
    val ring by rememberInfiniteTransition(label = "ringt").animateFloat(
        initialValue = 1f, targetValue = 1.07f,
        animationSpec = infiniteRepeatable(tween(3000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "ring"
    )
    val ringAlpha by rememberInfiniteTransition(label = "ringAt").animateFloat(
        initialValue = 0.5f, targetValue = 0.12f,
        animationSpec = infiniteRepeatable(tween(3000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "ringA"
    )
    val float by rememberInfiniteTransition(label = "floatt").animateFloat(
        initialValue = 0f, targetValue = -16f,
        animationSpec = infiniteRepeatable(tween(3500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "float"
    )
    val floatRot by rememberInfiniteTransition(label = "fRott").animateFloat(
        initialValue = -2f, targetValue = 2.5f,
        animationSpec = infiniteRepeatable(tween(3500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "fRot"
    )

    Box(
        modifier = Modifier.size(230.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(230.dp)
                .rotate(halo1)
                .blur(12.dp)
                .background(
                    brush = Brush.sweepGradient(
                        0f to NeonPurple.copy(alpha = 0.27f),
                        0.25f to NeonCyan.copy(alpha = 0.27f),
                        0.5f to NeonPink.copy(alpha = 0.27f),
                        0.75f to NeonGold.copy(alpha = 0.27f),
                        1f to NeonPurple.copy(alpha = 0.27f),
                    ),
                    shape = CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(190.dp)
                .rotate(halo2)
                .blur(8.dp)
                .background(
                    brush = Brush.sweepGradient(
                        0f to NeonCyan.copy(alpha = 0.2f),
                        0.33f to NeonPurple.copy(alpha = 0.2f),
                        0.66f to NeonGold.copy(alpha = 0.2f),
                        1f to NeonCyan.copy(alpha = 0.2f),
                    ),
                    shape = CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .scale(ring)
                .border(1.5.dp, Color.White.copy(alpha = ringAlpha), CircleShape)
        )
        Image(
            painter = painterResource(id = R.drawable.sym_7),
            contentDescription = "Joker",
            modifier = Modifier
                .size(160.dp)
                .graphicsLayer {
                    translationY = float
                    rotationZ = floatRot
                }
                .neonGlow(
                    color = NeonGold.copy(alpha = 0.75f),
                    cornerRadius = 80.dp,
                    blurRadius = 28.dp,
                    spreadAlpha = 0.55f,
                ),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun Wordmark() {
    val titleStyleBase = TextStyle(
        fontSize = 32.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 5.sp,
        textAlign = TextAlign.Center,
    )
    val pulse by rememberInfiniteTransition(label = "titlePulse").animateFloat(
        initialValue = 0.5f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(4000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "p"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = "NEON JOKER",
                style = titleStyleBase.copy(
                    brush = Brush.linearGradient(
                        colors = listOf(NeonPurple, NeonCyan)
                    )
                ),
                modifier = Modifier
                    .blur(12.dp)
                    .graphicsLayer { alpha = pulse }
            )
            Text(
                text = "NEON JOKER",
                style = titleStyleBase.copy(
                    brush = Brush.linearGradient(
                        colors = listOf(Color.White, TitleMidViolet, NeonCyan)
                    )
                ),
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "MERGE · ASCEND · CONQUER",
            style = TextStyle(
                fontSize = 9.sp,
                letterSpacing = 6.sp,
                textAlign = TextAlign.Center,
                color = Color.White.copy(alpha = 0.3f),
            ),
        )
    }
}

@Composable
private fun GemStrip() {
    var active by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(700)
            active = (active + 1) % tierImages.size
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(38.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        tierImages.forEachIndexed { idx, res ->
            val isActive = idx == active
            val targetScale = if (isActive) 1.28f else 1f
            val targetAlpha = if (isActive) 1f else 0.45f
            val s by animateFloatAsState(targetScale, tween(280), label = "gs$idx")
            val a by animateFloatAsState(targetAlpha, tween(280), label = "ga$idx")
            Box(
                modifier = Modifier.size(30.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = res),
                    contentDescription = null,
                    modifier = Modifier
                        .size(30.dp)
                        .graphicsLayer {
                            scaleX = s
                            scaleY = s
                            alpha = a
                        }
                        .then(
                            if (isActive) Modifier.neonGlow(
                                color = tierGlows[idx],
                                cornerRadius = 15.dp,
                                blurRadius = 10.dp,
                                spreadAlpha = 0.7f,
                            ) else Modifier
                        ),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}

@Composable
private fun BestScoreCard(score: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(NeonGold.copy(alpha = 0.07f))
            .border(1.dp, NeonGold.copy(alpha = 0.22f), RoundedCornerShape(16.dp))
            .drawBehind {
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(NeonGold.copy(alpha = 0.06f), Color.Transparent),
                        start = Offset.Zero,
                        end = Offset(size.width * 0.6f, size.height * 0.6f),
                    )
                )
            }
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "BEST SCORE",
                    style = TextStyle(
                        fontSize = 9.sp,
                        letterSpacing = 3.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NeonGold.copy(alpha = 0.5f),
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = score.toString(),
                    style = TextStyle(
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Black,
                        color = NeonGold,
                        shadow = androidx.compose.ui.graphics.Shadow(
                            color = NeonGold.copy(alpha = 0.65f),
                            blurRadius = 20f,
                        ),
                    ),
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.sym_7),
                    contentDescription = null,
                    modifier = Modifier.size(38.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "#1",
                    style = TextStyle(
                        fontSize = 8.sp,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NeonGold.copy(alpha = 0.45f),
                    )
                )
            }
        }
    }
}

@Composable
private fun PrimaryGradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val source = remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val s by animateFloatAsState(if (pressed) 0.96f else 1f, label = "ps")
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .graphicsLayer { scaleX = s; scaleY = s }
            .neonGlow(
                color = PrimaryGradStart,
                cornerRadius = 14.dp,
                blurRadius = 28.dp,
                spreadAlpha = 0.6f,
            )
            .clip(RoundedCornerShape(14.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(PrimaryGradStart, PrimaryGradEnd)
                )
            )
            .clickable(
                interactionSource = source,
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = 13.sp,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
        )
    }
}

@Composable
private fun SecondaryGhostButton(
    text: String,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White.copy(alpha = 0.07f),
            contentColor = Color.White.copy(alpha = 0.85f),
        ),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.13f)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = 13.sp,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Bold,
            )
        )
    }
}

@Composable
private fun HomeParticles(modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier = modifier.clipToBounds()) {
        val density = LocalDensity.current
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { maxHeight.toPx() }
        repeat(14) { i ->
            val left = (4f + (i * 6.8f) % 92f) / 100f
            val delaySec = (i * 0.71f) % 7f
            val durSec = 7f + (i * 0.53f) % 7f
            val sizeDp: Dp = (18 + ((i * 31) % 18)).dp
            val driftDp: Dp = (-30f + (i * 8.3f) % 60f).dp
            FloatingParticle(
                imageRes = tierImages[i % 7],
                glow = tierGlows[i % 7],
                centerXPx = widthPx * left,
                heightPx = heightPx,
                size = sizeDp,
                drift = driftDp,
                durationMs = (durSec * 1000).toInt(),
                delayMs = (delaySec * 1000).toInt(),
            )
        }
    }
}

@Composable
private fun FloatingParticle(
    imageRes: Int,
    glow: Color,
    centerXPx: Float,
    heightPx: Float,
    size: Dp,
    drift: Dp,
    durationMs: Int,
    delayMs: Int,
) {
    val density = LocalDensity.current
    val sizePx = with(density) { size.toPx() }
    val driftPx = with(density) { drift.toPx() }
    val travelPx = with(density) { 920.dp.toPx() }
    val baseBelowPx = with(density) { 40.dp.toPx() }

    val transition = rememberInfiniteTransition(label = "particle$delayMs")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMs, delayMillis = delayMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "p"
    )

    val alpha = when {
        progress < 0.08f -> (progress / 0.08f) * 0.55f
        progress < 0.85f -> 0.55f - (progress - 0.08f) / 0.77f * 0.25f
        else -> 0.3f * (1f - (progress - 0.85f) / 0.15f)
    }.coerceIn(0f, 1f)
    val s = 0.6f + 0.5f * progress
    val rot = 360f * progress

    Image(
        painter = painterResource(id = imageRes),
        contentDescription = null,
        modifier = Modifier
            .size(size)
            .offset {
                val x = centerXPx - sizePx / 2f + driftPx * progress
                val y = heightPx + baseBelowPx - travelPx * progress - sizePx
                IntOffset(x.roundToInt(), y.roundToInt())
            }
            .graphicsLayer {
                this.alpha = alpha
                scaleX = s
                scaleY = s
                rotationZ = rot
            }
            .neonGlow(
                color = glow,
                cornerRadius = size / 2,
                blurRadius = 6.dp,
                spreadAlpha = 0.6f,
            ),
        contentScale = ContentScale.Fit
    )
}
