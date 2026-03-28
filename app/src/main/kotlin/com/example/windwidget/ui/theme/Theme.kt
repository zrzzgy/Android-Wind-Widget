package com.example.windwidget.ui.theme

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

private val LightColors = lightColorScheme(
    primary = Color(0xFF1565C0),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1E4FF),
    onPrimaryContainer = Color(0xFF001D36),
    secondary = Color(0xFF00897B),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFB2DFDB),
    onSecondaryContainer = Color(0xFF002020),
    tertiary = Color(0xFF7C5800),
    onTertiary = Color.White,
    surface = Color(0xFFF8FAFE),
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = Color(0xFFE0E3E8),
    onSurfaceVariant = Color(0xFF44474E),
    background = Color(0xFFF8FAFE),
    onBackground = Color(0xFF1A1C1E),
    outline = Color(0xFF74777F),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF9ECAFF),
    onPrimary = Color(0xFF003258),
    primaryContainer = Color(0xFF00497D),
    onPrimaryContainer = Color(0xFFD1E4FF),
    secondary = Color(0xFF80CBC4),
    onSecondary = Color(0xFF003735),
    secondaryContainer = Color(0xFF00504D),
    onSecondaryContainer = Color(0xFFB2DFDB),
    tertiary = Color(0xFFFABD00),
    onTertiary = Color(0xFF3F2E00),
    surface = Color(0xFF111318),
    onSurface = Color(0xFFE2E2E6),
    surfaceVariant = Color(0xFF44474E),
    onSurfaceVariant = Color(0xFFC4C6CF),
    background = Color(0xFF111318),
    onBackground = Color(0xFFE2E2E6),
    outline = Color(0xFF8E9099),
)

@Composable
fun WindWidgetTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Use dynamic color on Android 12+
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
