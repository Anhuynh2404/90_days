package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Project90DarkColorScheme = darkColorScheme(
    primary = NeonFlame,
    onPrimary = Color.White,
    primaryContainer = FlameDark,
    onPrimaryContainer = Color.White,
    secondary = GoldenStreak,
    onSecondary = Color.Black,
    secondaryContainer = CharcoalCardElevated,
    onSecondaryContainer = GoldenStreakLight,
    tertiary = CyberCyan,
    onTertiary = Color.Black,
    tertiaryContainer = CharcoalCardElevated,
    onTertiaryContainer = CyberCyan,
    background = ObsidianBlack,
    onBackground = TextPrimary,
    surface = CharcoalDark,
    onSurface = TextPrimary,
    surfaceVariant = CharcoalCard,
    onSurfaceVariant = TextSecondary,
    outline = CharcoalBorder,
    error = DangerCrimson,
    onError = Color.White
)

private val Project90LightColorScheme = darkColorScheme( // Keep badass dark aesthetic even in light mode
    primary = NeonFlame,
    onPrimary = Color.White,
    primaryContainer = FlameDark,
    onPrimaryContainer = Color.White,
    secondary = GoldenStreak,
    onSecondary = Color.Black,
    secondaryContainer = CharcoalCardElevated,
    onSecondaryContainer = GoldenStreakLight,
    tertiary = CyberCyan,
    onTertiary = Color.Black,
    background = ObsidianBlack,
    onBackground = TextPrimary,
    surface = CharcoalDark,
    onSurface = TextPrimary,
    surfaceVariant = CharcoalCard,
    onSurfaceVariant = TextSecondary,
    outline = CharcoalBorder,
    error = DangerCrimson,
    onError = Color.White
)

@Composable
fun Project90Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = Project90DarkColorScheme,
        typography = Typography,
        content = content
    )
}
