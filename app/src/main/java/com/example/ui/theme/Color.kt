package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// ============================================================================
// Modern Bright & Professional Palette (Inspired by Eco/Sustainability Aesthetic)
// ============================================================================

// Primary Brand Colors - Deep Forest Green & Crisp Leaf Accents
val ForestGreen = Color(0xFF1B4324)        // Dominant brand green (from "BUILDING A SUSTAINABLE FUTURE")
val ForestGreenLight = Color(0xFF2E6F40)   // Medium leaf green
val ForestGreenDark = Color(0xFF112B17)    // Deepest shade for high contrast

val LeafGreen = Color(0xFF38A169)          // Vibrant energizing green
val MintLight = Color(0xFFE8F5EC)          // Soft pastel mint for badge & card highlights
val MintSurface = Color(0xFFF1F8F3)        // Elevated card background
val MintBorder = Color(0xFFCFE5D4)         // Mint tinted border

// Secondary Accents - Warm Amber & Fresh Marine Teal
val WarmAmber = Color(0xFFD97706)          // Warm golden amber for streak flame & warnings
val GoldenStreak = Color(0xFFE5A11A)       // Crisp golden yellow
val GoldenStreakLight = Color(0xFFFEF3C7)  // Soft yellow tint for streak badge

val MarineAqua = Color(0xFF0D9488)         // Fresh aqua for water tracker
val AquaLight = Color(0xFFE0F2FE)          // Soft blue tint
val CyberCyan = MarineAqua                 // Alias

val SuccessGreen = Color(0xFF16A34A)       // Crisp success checkmark
val DangerCrimson = Color(0xFFDC2626)      // Modern alert red
val WarningAmber = Color(0xFFD97706)       // Clear warning tone

// Bright Theme Surfaces (Light & Airy, Fresh Eco Tone)
val EcoBackground = Color(0xFFF5F8F5)      // Fresh light airy background (as in poster)
val CardWhite = Color(0xFFFFFFFF)          // Pure white card surfaces
val CardElevated = Color(0xFFFBFDFB)       // Elevated card
val CardBorder = Color(0xFFE3EDE5)         // Delicate soft border
val CardBorderFocused = Color(0xFF81C784)  // Focused border

// High Legibility Text Colors for Bright Surfaces
val TextPrimary = Color(0xFF142419)        // Deep slate forest - maximum clarity & authority
val TextSecondary = Color(0xFF496352)      // Legible botanical slate
val TextTertiary = Color(0xFF768E7E)       // Gentle caption grey-green
val TextOnDark = Color(0xFFFFFFFF)         // White text on dark buttons/chips

// Aliases for full compatibility with existing code while transitioning to bright UI
val ObsidianBlack = EcoBackground          // Maps screen container backgrounds to bright off-white!
val CharcoalDark = CardWhite
val CharcoalCard = CardWhite               // Maps cards to pure crisp white!
val CharcoalCardElevated = MintSurface     // Maps elevated boxes to refreshing soft mint!
val CharcoalBorder = CardBorder            // Maps borders to light crisp border!

val NeonFlame = ForestGreen                // Primary brand accents become Deep Forest Green
val FlameLight = ForestGreenLight
val FlameDark = ForestGreenDark
val CyanMuted = MarineAqua
