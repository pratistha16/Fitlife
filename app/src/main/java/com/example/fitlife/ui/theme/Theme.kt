package com.example.fitlife.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = BrandPrimary,
    onPrimary = Color.Black, // Dark text on bright Cyan
    primaryContainer = Color(0xFF004D40), // Dark Teal
    onPrimaryContainer = Color(0xFFE0F2F1),

    secondary = BrandSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF311B92), // Deep Purple
    onSecondaryContainer = Color(0xFFEDE7F6),

    tertiary = BrandTertiary,
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF004D40),
    onTertiaryContainer = Color(0xFFE0F2F1),

    background = DarkBackground,
    onBackground = TextWhite,
    surface = DarkSurface,
    onSurface = TextWhite,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextGray,
    outline = TextGray,
    error = ErrorRed
)

private val LightColorScheme = lightColorScheme(
    primary = BrandPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F7FA),
    onPrimaryContainer = Color(0xFF006064),

    secondary = BrandSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEDE7F6),
    onSecondaryContainer = Color(0xFF311B92),

    tertiary = BrandTertiary,
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFFE0F2F1),
    onTertiaryContainer = Color(0xFF004D40),

    background = LightBackground,
    onBackground = TextBlack,
    surface = LightSurface,
    onSurface = TextBlack,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = TextDarkGray,
    outline = Color(0xFFCFD8DC),
    error = ErrorRed
)

@Composable
fun FitlifeTheme(
    darkTheme: Boolean = true, // Force Dark Theme by default as per user request
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disable dynamic color to enforce our custom look
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
