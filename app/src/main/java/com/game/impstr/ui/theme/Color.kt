package com.game.impstr.ui.theme

import androidx.compose.ui.graphics.Color

// --- Normal Mode (Material Design 3 Expressive — Dark Scheme) ---
// Source: material-theme.json. Calm, modern, friendly.
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

// MD3 Expressive surface containers — tonal hierarchy from lowest (bg) to highest.
val SurfaceContainerLowest = Color(0xFF0A0F12)
val SurfaceContainerLow = Color(0xFF181C20)
val SurfaceContainer = Color(0xFF1C2024)
val SurfaceContainerHigh = Color(0xFF262A2E)
val SurfaceContainerHighest = Color(0xFF313539)

// --- Game element accent palette (avatars, info cards) ---
// Intentionally vibrant. Used as backgrounds; text drawn on these uses dark
// onColor (Color.Black variants) for sufficient contrast.
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

// --- Stealth Mode (Neon Cyberpunk) ---
// Pure-black canvas with high-luminance neon accents. All foreground colors
// here meet or exceed WCAG AA (4.5:1) against StealthBlack/StealthSurface.
// The previous StealthViolet (#8A2BE2) was ~2.6:1 against black and has been
// promoted to a container/decorative role only.
val StealthBlack = Color(0xFF000000)
val StealthSurface = Color(0xFF0A0A0A)
val StealthSurfaceVariant = Color(0xFF1A1A1A)
val StealthSurfaceContainer = Color(0xFF141414)
val StealthSurfaceContainerHigh = Color(0xFF1F1F1F)

val StealthLime = Color(0xFF39FF14)         // Primary interactive — 16:1 on black
val StealthOnLime = Color(0xFF001500)       // Dark text on lime — readable
val StealthLimeContainer = Color(0xFF0B3D0A) // Dim lime — selected backgrounds
val StealthOnLimeContainer = Color(0xFFB9FFB0)

val StealthNeonPurple = Color(0xFFC77DFF)   // Accent — ~7.5:1 on black (a11y safe)
val StealthOnNeonPurple = Color(0xFF1A0033)
val StealthPurple = Color(0xFFB026FF)       // Decorative only — fails AA alone
val StealthViolet = Color(0xFF8A2BE2)       // Decorative only — fails AA alone

val StealthCyan = Color(0xFF00E5FF)         // Tertiary — ~13:1 on black
val StealthOnCyan = Color(0xFF002023)

val StealthWhite = Color(0xFFFFFFFF)
val StealthOnSurfaceVariant = Color(0xFFCFCFCF) // Soft white for secondary text
val StealthOutline = Color(0xFF7A7A7A)
val StealthOutlineVariant = Color(0xFF2A2A2A)

val StealthError = Color(0xFFFF3D6E)        // Neon pink-red — ~6:1 on black
val StealthOnError = Color(0xFF1A0008)
val StealthErrorContainer = Color(0xFF5C0019)
val StealthOnErrorContainer = Color(0xFFFFB3C2)

// Stealth player accent palette — each color verified >= 4.5:1 on black.
val StealthPlayerColors =
    listOf(
        StealthLime,                // Lime green
        StealthCyan,                // Cyan
        StealthNeonPurple,          // Soft neon purple
        Color(0xFFFFE600),          // Neon yellow
        Color(0xFFFF6EC7),          // Hot pink
        Color(0xFFFF9F1C),          // Neon orange
        Color(0xFF7FFFD4),          // Aquamarine
        StealthError,               // Neon red-pink
    )
