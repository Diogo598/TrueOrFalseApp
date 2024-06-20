package com.example.trueorfalseapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColorScheme(
    primary = Color(0xFF1EB980),
    secondary = Color(0xFF037B73),
    background = Color(0xFF121212)
)

private val LightColorPalette = lightColorScheme(
    primary = Color(0xFF1EB980),
    secondary = Color(0xFF037B73),
    background = Color(0xFFFFFFFF)
)

@Composable
fun AnimationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}