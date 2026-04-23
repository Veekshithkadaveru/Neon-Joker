package app.krafted.neonjoker.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = CyberBg,
    primaryContainer = NeonPurple,
    onPrimaryContainer = NeonWhite,
    secondary = NeonPink,
    onSecondary = NeonWhite,
    secondaryContainer = CyberSurfaceVariant,
    onSecondaryContainer = NeonPink,
    tertiary = NeonGreen,
    onTertiary = CyberBg,
    tertiaryContainer = CyberSurfaceVariant,
    onTertiaryContainer = NeonGreen,
    background = CyberBg,
    onBackground = CyberOnDark,
    surface = CyberSurface,
    onSurface = CyberOnDark,
    surfaceVariant = CyberSurfaceVariant,
    onSurfaceVariant = CyberOnDarkMuted,
    outline = CyberOutline,
    error = NeonRed,
    onError = NeonWhite
)

private val LightColorScheme = lightColorScheme(
    primary = NeonPurple,
    onPrimary = NeonWhite,
    primaryContainer = Color(0xFFD7CAFF),
    onPrimaryContainer = Color(0xFF24105E),
    secondary = NeonPink,
    onSecondary = NeonWhite,
    secondaryContainer = Color(0xFFFFD9E5),
    onSecondaryContainer = Color(0xFF5E0E2C),
    tertiary = NeonGold,
    onTertiary = Color(0xFF2A2000),
    tertiaryContainer = Color(0xFFFFEFA6),
    onTertiaryContainer = Color(0xFF4A3A00),
    background = Color(0xFFF5F8FF),
    onBackground = Color(0xFF16172B),
    surface = Color(0xFFEEF1FF),
    onSurface = Color(0xFF17192E),
    surfaceVariant = Color(0xFFE0E4F7),
    onSurfaceVariant = Color(0xFF444A66),
    outline = Color(0xFF757D9E),
    error = NeonRed,
    onError = NeonWhite
)

@Composable
fun NeonJokerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
