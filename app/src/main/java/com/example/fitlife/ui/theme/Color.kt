package com.example.fitlife.ui.theme

import androidx.compose.ui.graphics.Color

// Primary Brand Colors (Vibrant Gradients)
val BrandPrimary = Color(0xFF00E5FF) // Cyan Accent
val BrandSecondary = Color(0xFF7C4DFF) // Deep Purple Accent
val BrandTertiary = Color(0xFF69F0AE) // Mint Green Accent

// Neutral / Background Colors (Dark Mode Focus)
val DarkBackground = Color(0xFF121212) // Deep Black/Grey
val DarkSurface = Color(0xFF1E1E1E) // Card Background
val DarkSurfaceVariant = Color(0xFF2D2D2D) // Lighter Card/Input

// Neutral / Background Colors (Light Mode - Keeping for fallback)
val LightBackground = Color(0xFFF8FAFC)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFF1F5F9)

// Accents & Functional
val SuccessGreen = Color(0xFF00C853)
val ErrorRed = Color(0xFFFF5252)
val WarningOrange = Color(0xFFFFAB40)

// Text Colors
val TextWhite = Color(0xFFFFFFFF)
val TextGray = Color(0xFFB0BEC5)
val TextBlack = Color(0xFF121212)
val TextDarkGray = Color(0xFF455A64)

// Gradient Helpers
val GradientCyanPurple = listOf(BrandPrimary, BrandSecondary)
val GradientPurpleMint = listOf(BrandSecondary, BrandTertiary)
