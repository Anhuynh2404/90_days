package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Modern bright, friendly, and professional theme matching the clean sustainability aesthetic
private val Project90LightColorScheme = lightColorScheme(
    primary = ForestGreen,
    onPrimary = Color.White,
    primaryContainer = MintLight,
    onPrimaryContainer = ForestGreenDark,
    secondary = GoldenStreak,
    onSecondary = Color.White,
    secondaryContainer = GoldenStreakLight,
    onSecondaryContainer = ForestGreen,
    tertiary = MarineAqua,
    onTertiary = Color.White,
    tertiaryContainer = AquaLight,
    onTertiaryContainer = ForestGreenDark,
    background = EcoBackground,
    onBackground = TextPrimary,
    surface = CardWhite,
    onSurface = TextPrimary,
    surfaceVariant = MintSurface,
    onSurfaceVariant = TextSecondary,
    outline = CardBorder,
    error = DangerCrimson,
    onError = Color.White
)

@Composable
fun Project90Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = Project90LightColorScheme,
        typography = Typography,
        content = content
    )
}
