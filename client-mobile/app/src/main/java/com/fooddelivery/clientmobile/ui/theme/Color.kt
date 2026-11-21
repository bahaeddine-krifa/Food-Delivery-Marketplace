package com.fooddelivery.clientmobile.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Primary colors
val Primary = Color(0xFF2563EB) // Blue 600
val PrimaryContainer = Color(0xFFDBEAFE) // Blue 100
val OnPrimary = Color.White
val OnPrimaryContainer = Color(0xFF1E3A8A) // Blue 900

// Secondary colors
val Secondary = Color(0xFF059669) // Emerald 600
val SecondaryContainer = Color(0xFFD1FAE5) // Emerald 100
val OnSecondary = Color.White
val OnSecondaryContainer = Color(0xFF064E3B) // Emerald 900

// Tertiary colors
val Tertiary = Color(0xFFEA580C) // Orange 600
val TertiaryContainer = Color(0xFFFFEDD5) // Orange 100
val OnTertiary = Color.White
val OnTertiaryContainer = Color(0xFF7C2D12) // Orange 900

// Error colors
val Error = Color(0xFFDC2626) // Red 600
val ErrorContainer = Color(0xFFFEF2F2) // Red 50
val OnError = Color.White
val OnErrorContainer = Color(0xFF7F1D1D) // Red 900

// Neutral colors
val Background = Color(0xFFFAFAFA) // Neutral 50
val OnBackground = Color(0xFF18181B) // Neutral 950
val Surface = Color.White
val OnSurface = Color(0xFF18181B) // Neutral 950
val SurfaceVariant = Color(0xFFF4F4F5) // Neutral 100
val OnSurfaceVariant = Color(0xFF52525B) // Neutral 600
val Outline = Color(0xFFE4E4E7) // Neutral 200
val OutlineVariant = Color(0xFFF4F4F5) // Neutral 100

// Dark theme colors
val DarkPrimary = Color(0xFF60A5FA) // Blue 400
val DarkPrimaryContainer = Color(0xFF1E3A8A) // Blue 900
val DarkOnPrimary = Color(0xFF1E3A8A) // Blue 900
val DarkOnPrimaryContainer = Color(0xFFDBEAFE) // Blue 100

val DarkSecondary = Color(0xFF34D399) // Emerald 400
val DarkSecondaryContainer = Color(0xFF064E3B) // Emerald 900
val DarkOnSecondary = Color(0xFF064E3B) // Emerald 900
val DarkOnSecondaryContainer = Color(0xFFD1FAE5) // Emerald 100

val DarkTertiary = Color(0xFFFB923C) // Orange 400
val DarkTertiaryContainer = Color(0xFF7C2D12) // Orange 900
val DarkOnTertiary = Color(0xFF7C2D12) // Orange 900
val DarkOnTertiaryContainer = Color(0xFFFFEDD5) // Orange 100

val DarkError = Color(0xFFF87171) // Red 400
val DarkErrorContainer = Color(0xFF7F1D1D) // Red 900
val DarkOnError = Color(0xFF7F1D1D) // Red 900
val DarkOnErrorContainer = Color(0xFFFEF2F2) // Red 50

val DarkBackground = Color(0xFF18181B) // Neutral 950
val DarkOnBackground = Color(0xFFFAFAFA) // Neutral 50
val DarkSurface = Color(0xFF27272A) // Neutral 900
val DarkOnSurface = Color(0xFFFAFAFA) // Neutral 50
val DarkSurfaceVariant = Color(0xFF3F3F46) // Neutral 800
val DarkOnSurfaceVariant = Color(0xFFA1A1AA) // Neutral 400
val DarkOutline = Color(0xFF52525B) // Neutral 600
val DarkOutlineVariant = Color(0xFF3F3F46) // Neutral 800

@Composable
fun FoodDeliveryColorScheme(isDark: Boolean = false) = if (isDark) {
    MaterialTheme.colorScheme.copy(
        primary = DarkPrimary,
        primaryContainer = DarkPrimaryContainer,
        onPrimary = DarkOnPrimary,
        onPrimaryContainer = DarkOnPrimaryContainer,
        secondary = DarkSecondary,
        secondaryContainer = DarkSecondaryContainer,
        onSecondary = DarkOnSecondary,
        onSecondaryContainer = DarkOnSecondaryContainer,
        tertiary = DarkTertiary,
        tertiaryContainer = DarkTertiaryContainer,
        onTertiary = DarkOnTertiary,
        onTertiaryContainer = DarkOnTertiaryContainer,
        error = DarkError,
        errorContainer = DarkErrorContainer,
        onError = DarkOnError,
        onErrorContainer = DarkOnErrorContainer,
        background = DarkBackground,
        onBackground = DarkOnBackground,
        surface = DarkSurface,
        onSurface = DarkOnSurface,
        surfaceVariant = DarkSurfaceVariant,
        onSurfaceVariant = DarkOnSurfaceVariant,
        outline = DarkOutline,
        outlineVariant = DarkOutlineVariant
    )
} else {
    MaterialTheme.colorScheme.copy(
        primary = Primary,
        primaryContainer = PrimaryContainer,
        onPrimary = OnPrimary,
        onPrimaryContainer = OnPrimaryContainer,
        secondary = Secondary,
        secondaryContainer = SecondaryContainer,
        onSecondary = OnSecondary,
        onSecondaryContainer = OnSecondaryContainer,
        tertiary = Tertiary,
        tertiaryContainer = TertiaryContainer,
        onTertiary = OnTertiary,
        onTertiaryContainer = OnTertiaryContainer,
        error = Error,
        errorContainer = ErrorContainer,
        onError = OnError,
        onErrorContainer = OnErrorContainer,
        background = Background,
        onBackground = OnBackground,
        surface = Surface,
        onSurface = OnSurface,
        surfaceVariant = SurfaceVariant,
        onSurfaceVariant = OnSurfaceVariant,
        outline = Outline,
        outlineVariant = OutlineVariant
    )
}