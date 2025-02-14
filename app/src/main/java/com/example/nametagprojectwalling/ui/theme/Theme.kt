package com.example.nametagprojectwalling.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFFF9800),          // orange
    primaryContainer = Color(0xFFF57C00),  // dark_orange
    secondary = Color(0xFFFFB74D),        // accent_pink
    onPrimary = Color(0xFF2D2D2D),        // text_dark
    onSecondary = Color(0xFF2D2D2D),      // text_dark
    onBackground = Color(0xFF2D2D2D),     // text_dark
    onSurface = Color(0xFF2D2D2D)         // text_dark
)

@Composable
fun NametagProjectWallingTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}