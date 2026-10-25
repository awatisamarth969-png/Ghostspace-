package com.ghostspace.privacy.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = PurplePrimary,
    secondary = PurpleLight,
    tertiary = AccentCyan,
    background = ObsidianBlack,
    surface = SurfaceDark,
    surfaceVariant = SurfaceCard,
    onPrimary = TextWhite,
    onSecondary = TextWhite,
    onBackground = TextWhite,
    onSurface = TextWhite,
    error = ErrorRed
)

private val LightColorScheme = lightColorScheme(
    primary = PurpleDark,
    secondary = PurplePrimary,
    tertiary = AccentCyan,
    background = LightCanvas,
    surface = LightSurface,
    surfaceVariant = LightBorder,
    onPrimary = TextWhite,
    onBackground = LightText,
    onSurface = LightText,
    error = ErrorRed
)

@Composable
fun GhostSpaceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
