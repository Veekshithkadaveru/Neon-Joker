package app.krafted.neonjoker.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import app.krafted.neonjoker.ui.theme.CyberOnDarkMuted
import app.krafted.neonjoker.ui.theme.CyberSurfaceVariant

private val MenuButtonShape = CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp)

@Composable
fun NeonMenuButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean,
    accent: Color,
    fillFraction: Float = 0.72f,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed && enabled) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "menuBtnPress"
    )
    Button(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interactionSource,
        shape = MenuButtonShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = CyberSurfaceVariant,
            contentColor = accent,
            disabledContainerColor = CyberSurfaceVariant.copy(alpha = 0.45f),
            disabledContentColor = CyberOnDarkMuted
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            disabledElevation = 0.dp
        ),
        modifier = modifier
            .fillMaxWidth(fillFraction)
            .height(54.dp)
            .then(
                if (enabled) {
                    Modifier.neonGlow(
                        accent,
                        cornerRadius = 10.dp,
                        blurRadius = 14.dp,
                        spreadAlpha = 0.36f
                    )
                } else {
                    Modifier
                }
            )
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                transformOrigin = TransformOrigin.Center
            }
    ) {
        Text(text = text, style = MaterialTheme.typography.titleLarge)
    }
}
