package com.example.windwidget.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val WindColorScheme = lightColorScheme(
    primary = Color(0xFF1A73E8),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD2E3FC),
    onPrimaryContainer = Color(0xFF001D36),
    secondary = Color(0xFF00897B),
    onSecondary = Color.White,
    surface = Color(0xFFFAFCFF),
    onSurface = Color(0xFF1A1C1E),
    background = Color(0xFFFAFCFF),
    onBackground = Color(0xFF1A1C1E),
)

@Composable
fun WindWidgetTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = WindColorScheme,
        content = content
    )
}
