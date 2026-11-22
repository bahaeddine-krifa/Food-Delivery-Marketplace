package com.fooddelivery.deliverymobile.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

private val DarkPrimary = Color(0xFF1E88E5)
private val DarkSecondary = Color(0xFF43A047)
private val DarkTertiary = Color(0xFFFFA000)
private val DarkError = Color(0xFFE57373)
private val DarkSurface = Color(0xFF121212)

private val LightPrimary = Color(0xFF1565C0)
private val LightSecondary = Color(0xFF2E7D32)
private val LightTertiary = Color(0xFFF57F17)
private val LightError = Color(0xFFC62828)
private val LightSurface = Color(0xFFFFFFFF)

val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    secondary = DarkSecondary,
    tertiary = DarkTertiary,
    error = DarkError,
    surface = DarkSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    onError = Color.White,
    onSurface = Color.White,
    outline = Color(0xFF757575),
    outlineVariant = Color(0xFF616161),
    primaryContainer = Color(0xFF0D47A1),
    secondaryContainer = Color(0xFF1B5E20),
    tertiaryContainer = Color(0xFFE65100),
    errorContainer = Color(0xFFB71C1C),
    onPrimaryContainer = Color(0xFFBBDEFB),
    onSecondaryContainer = Color(0xFFC8E6C9),
    onTertiaryContainer = Color(0xFFFFE0B2),
    onErrorContainer = Color(0xFFFFCDD2),
    surfaceContainerHighest = Color(0xFF1E1E1E),
    surfaceContainerHigh = Color(0xFF1E1E1E),
    surfaceContainer = Color(0xFF2C2C2C),
    surfaceContainerLow = Color(0xFF2C2C2C),
    surfaceContainerLowest = Color(0xFF121212),
)

val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    secondary = LightSecondary,
    tertiary = LightTertiary,
    error = LightError,
    surface = LightSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    onError = Color.White,
    onSurface = Color.Black,
    outline = Color(0xFF9E9E9E),
    outlineVariant = Color(0xFFBDBDBD),
    primaryContainer = Color(0xFFE3F2FD),
    secondaryContainer = Color(0xFFE8F5E9),
    tertiaryContainer = Color(0xFFFFF3E0),
    errorContainer = Color(0xFFFFEBEE),
    onPrimaryContainer = Color(0xFF0D47A1),
    onSecondaryContainer = Color(0xFF1B5E20),
    onTertiaryContainer = Color(0xFFE65100),
    onErrorContainer = Color(0xFFB71C1C),
    surfaceContainerHighest = Color(0xFFE0E0E0),
    surfaceContainerHigh = Color(0xFFEEEEEE),
    surfaceContainer = Color(0xFFF5F5F5),
    surfaceContainerLow = Color(0xFFFAFAFA),
    surfaceContainerLowest = Color(0xFFFFFFFF),
)