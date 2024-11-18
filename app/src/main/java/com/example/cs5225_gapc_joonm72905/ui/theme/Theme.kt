package com.example.cs5225_gapc_joonm72905.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    background = DarkGray20x,
    primaryContainer = DarkGray50x,
    primary = DarkGray10x,
    secondary = White,
    tertiary = Orange20x,
    inversePrimary = LightGreen20x,
    inverseSurface = LightRed20x
)

@Composable
fun CS5225_GAPC_JOONM72905Theme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}