package app.krafted.neonjoker.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.krafted.neonjoker.ui.theme.CyberBg
import app.krafted.neonjoker.ui.theme.CyberSurface
import app.krafted.neonjoker.ui.theme.CyberSurfaceVariant


fun Modifier.neonGlow(
    color: Color,
    cornerRadius: Dp = 8.dp,
    blurRadius: Dp = 14.dp,
    spreadAlpha: Float = 0.55f
): Modifier = drawBehind {
    val radiusPx = cornerRadius.toPx()
    val glow = color.copy(alpha = spreadAlpha)
    drawIntoCanvas { canvas ->
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.isAntiAlias = true
        frameworkPaint.color = glow.toArgb()
        frameworkPaint.style = android.graphics.Paint.Style.FILL
        frameworkPaint.setShadowLayer(
            blurRadius.toPx(),
            0f,
            0f,
            glow.toArgb()
        )
        canvas.nativeCanvas.drawRoundRect(
            0f,
            0f,
            size.width,
            size.height,
            radiusPx,
            radiusPx,
            frameworkPaint
        )
    }
}


fun Modifier.neonGlowCircle(
    color: Color,
    blurRadius: Dp = 10.dp,
    spreadAlpha: Float = 0.5f
): Modifier = drawBehind {
    val glow = color.copy(alpha = spreadAlpha)
    val r = size.minDimension / 2f
    drawIntoCanvas { canvas ->
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.isAntiAlias = true
        frameworkPaint.color = glow.toArgb()
        frameworkPaint.style = android.graphics.Paint.Style.FILL
        frameworkPaint.setShadowLayer(
            blurRadius.toPx(),
            0f,
            0f,
            glow.toArgb()
        )
        canvas.nativeCanvas.drawCircle(
            size.width / 2f,
            size.height / 2f,
            r,
            frameworkPaint
        )
    }
}

fun cyberpunkGameGradientBrush(): Brush = Brush.verticalGradient(
    colors = listOf(
        CyberBg,
        Color(0xFF08081A),
        CyberSurfaceVariant.copy(alpha = 0.85f),
        CyberSurface.copy(alpha = 0.9f),
        CyberBg
    )
)
