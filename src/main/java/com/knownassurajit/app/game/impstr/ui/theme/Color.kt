package com.knownassurajit.app.game.impstr.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Material 3 tonal palettes for IMPSTR.
 * Seed: cyan-blue (#006590). Dark and light are independently specified so
 * light theme never inherits dark surfaces.
 */

// --- Dark scheme ---
val Primary = Color(0xFF93CDF6)
val OnPrimary = Color(0xFF00344C)
val PrimaryContainer = Color(0xFF004C6D)
val OnPrimaryContainer = Color(0xFFC8E6FF)

val Secondary = Color(0xFFB6C9D8)
val OnSecondary = Color(0xFF21323E)
val SecondaryContainer = Color(0xFF384956)
val OnSecondaryContainer = Color(0xFFD2E5F5)

val Tertiary = Color(0xFFCDC0E9)
val OnTertiary = Color(0xFF342B4B)
val TertiaryContainer = Color(0xFF4B4263)
val OnTertiaryContainer = Color(0xFFE9DDFF)

val Error = Color(0xFFFFB4AB)
val OnError = Color(0xFF690005)
val ErrorContainer = Color(0xFF93000A)
val OnErrorContainer = Color(0xFFFFDAD6)

val Background = Color(0xFF101417)
val OnBackground = Color(0xFFDFE3E7)

val Surface = Color(0xFF101417)
val OnSurface = Color(0xFFDFE3E7)
val SurfaceVariant = Color(0xFF41484D)
val OnSurfaceVariant = Color(0xFFC1C7CE)

val Outline = Color(0xFF8B9198)
val OutlineVariant = Color(0xFF41484D)

val SurfaceDim = Color(0xFF101417)
val SurfaceBright = Color(0xFF363A3E)
val SurfaceContainerLowest = Color(0xFF0A0F12)
val SurfaceContainerLow = Color(0xFF181C20)
val SurfaceContainer = Color(0xFF1C2024)
val SurfaceContainerHigh = Color(0xFF262A2E)
val SurfaceContainerHighest = Color(0xFF313539)

val InverseSurface = Color(0xFFDFE3E7)
val InverseOnSurface = Color(0xFF2E3133)
val InversePrimary = Color(0xFF006590)
val Scrim = Color(0xFF000000)

// --- Light scheme (same seed, light surfaces) ---
val LightPrimary = Color(0xFF006590)
val LightOnPrimary = Color(0xFFFFFFFF)
val LightPrimaryContainer = Color(0xFFC8E6FF)
val LightOnPrimaryContainer = Color(0xFF001E2E)

val LightSecondary = Color(0xFF4E616D)
val LightOnSecondary = Color(0xFFFFFFFF)
val LightSecondaryContainer = Color(0xFFD1E5F4)
val LightOnSecondaryContainer = Color(0xFF0A1E28)

val LightTertiary = Color(0xFF63597C)
val LightOnTertiary = Color(0xFFFFFFFF)
val LightTertiaryContainer = Color(0xFFE9DDFF)
val LightOnTertiaryContainer = Color(0xFF1F1635)

val LightError = Color(0xFFBA1A1A)
val LightOnError = Color(0xFFFFFFFF)
val LightErrorContainer = Color(0xFFFFDAD6)
val LightOnErrorContainer = Color(0xFF410002)

val LightBackground = Color(0xFFF8F9FC)
val LightOnBackground = Color(0xFF191C1E)

val LightSurface = Color(0xFFF8F9FC)
val LightOnSurface = Color(0xFF191C1E)
val LightSurfaceVariant = Color(0xFFDDE3EA)
val LightOnSurfaceVariant = Color(0xFF41484D)

val LightOutline = Color(0xFF71787E)
val LightOutlineVariant = Color(0xFFC1C7CE)

val LightSurfaceDim = Color(0xFFD8DADD)
val LightSurfaceBright = Color(0xFFF8F9FC)
val LightSurfaceContainerLowest = Color(0xFFFFFFFF)
val LightSurfaceContainerLow = Color(0xFFF2F4F6)
val LightSurfaceContainer = Color(0xFFECEEF1)
val LightSurfaceContainerHigh = Color(0xFFE6E8EB)
val LightSurfaceContainerHighest = Color(0xFFE0E3E6)

val LightInverseSurface = Color(0xFF2E3133)
val LightInverseOnSurface = Color(0xFFEFF1F4)
val LightInversePrimary = Color(0xFF93CDF6)

// Random/vibrant colors for player avatars (kept saturated by design)
val CardYellow = Color(0xFFFFD54F)
val CardPurple = Color(0xFFBA68C8)
val CardRed = Color(0xFFE57373)
val CardTeal = Color(0xFF4DB6AC)
val CardGreen = Color(0xFFAED581)
val CardOrange = Color(0xFFFFB74D)
val CardBlue = Color(0xFF64B5F6)
val CardPink = Color(0xFFF06292)

val PlayerColors =
    listOf(
        CardYellow,
        CardPurple,
        CardRed,
        CardTeal,
        CardGreen,
        CardOrange,
        CardBlue,
        CardPink,
    )

// Stealth Mode Neon Colors
val StealthBlack = Color(0xFF000000)
val StealthSurface = Color(0xFF050505)
val StealthSurfaceVariant = Color(0xFF111111)
val StealthViolet = Color(0xFF8A2BE2)
val StealthPurple = Color(0xFFB026FF)
val StealthLime = Color(0xFF39FF14)
val StealthWhite = Color(0xFFFFFFFF)
val StealthOnLime = Color(0xFF000000)
val StealthOnPurple = Color(0xFFFFFFFF)
val StealthError = Color(0xFFFF003C)
val StealthSurfaceContainerLowest = Color(0xFF000000)
val StealthSurfaceContainerLow = Color(0xFF0A0A0A)
val StealthSurfaceContainer = Color(0xFF111111)
val StealthSurfaceContainerHigh = Color(0xFF1A1A1A)
val StealthSurfaceContainerHighest = Color(0xFF242424)

val StealthPlayerColors =
    listOf(
        StealthLime,
        StealthPurple,
        StealthViolet,
        StealthError,
        Color(0xFF00FFFF),
        Color(0xFFFF00FF),
        Color(0xFFFFFF00),
        Color(0xFFFF5F00),
    )
