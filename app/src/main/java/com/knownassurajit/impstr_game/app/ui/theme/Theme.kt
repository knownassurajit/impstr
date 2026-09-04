package com.knownassurajit.impstr_game.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Material Design 3 Expressive — dark color scheme (Normal mode).
 *
 * Tonal hierarchy is built around the [SurfaceContainer] family so cards,
 * sheets and bars sit on distinct elevations without requiring shadows.
 */
private val DarkColorScheme =
    darkColorScheme(
        primary = Primary,
        onPrimary = OnPrimary,
        primaryContainer = PrimaryContainer,
        onPrimaryContainer = OnPrimaryContainer,
        secondary = Secondary,
        onSecondary = OnSecondary,
        secondaryContainer = SecondaryContainer,
        onSecondaryContainer = OnSecondaryContainer,
        tertiary = Tertiary,
        onTertiary = OnTertiary,
        tertiaryContainer = TertiaryContainer,
        onTertiaryContainer = OnTertiaryContainer,
        error = Error,
        onError = OnError,
        errorContainer = ErrorContainer,
        onErrorContainer = OnErrorContainer,
        background = Background,
        onBackground = OnBackground,
        surface = Surface,
        onSurface = OnSurface,
        surfaceVariant = SurfaceVariant,
        onSurfaceVariant = OnSurfaceVariant,
        outline = Outline,
        outlineVariant = OutlineVariant,
        surfaceContainerLowest = SurfaceContainerLowest,
        surfaceContainerLow = SurfaceContainerLow,
        surfaceContainer = SurfaceContainer,
        surfaceContainerHigh = SurfaceContainerHigh,
        surfaceContainerHighest = SurfaceContainerHighest,
    )

/** Light scheme retained for completeness — the app currently always renders dark. */
private val LightColorScheme =
    lightColorScheme(
        primary = Primary,
        onPrimary = OnPrimary,
        primaryContainer = PrimaryContainer,
        onPrimaryContainer = OnPrimaryContainer,
        secondary = Secondary,
        onSecondary = OnSecondary,
        secondaryContainer = SecondaryContainer,
        onSecondaryContainer = OnSecondaryContainer,
        tertiary = Tertiary,
        onTertiary = OnTertiary,
        tertiaryContainer = TertiaryContainer,
        onTertiaryContainer = OnTertiaryContainer,
        error = Error,
        onError = OnError,
        errorContainer = ErrorContainer,
        onErrorContainer = OnErrorContainer,
        background = Background,
        onBackground = OnBackground,
        surface = Surface,
        onSurface = OnSurface,
        surfaceVariant = SurfaceVariant,
        onSurfaceVariant = OnSurfaceVariant,
        outline = Outline,
        outlineVariant = OutlineVariant,
    )

/**
 * Stealth Mode (Neon Cyberpunk).
 *
 * Primary maps to [StealthLime] because it is the highest-contrast accent
 * against pure black. The darker neon violet/purple shades are restricted to
 * container slots where text is not drawn directly on top of them.
 */
private val StealthColorScheme =
    darkColorScheme(
        primary = StealthLime,
        onPrimary = StealthOnLime,
        primaryContainer = StealthLimeContainer,
        onPrimaryContainer = StealthOnLimeContainer,
        secondary = StealthNeonPurple,
        onSecondary = StealthOnNeonPurple,
        secondaryContainer = StealthSurfaceContainerHigh,
        onSecondaryContainer = StealthNeonPurple,
        tertiary = StealthCyan,
        onTertiary = StealthOnCyan,
        tertiaryContainer = StealthSurfaceContainer,
        onTertiaryContainer = StealthCyan,
        error = StealthError,
        onError = StealthOnError,
        errorContainer = StealthErrorContainer,
        onErrorContainer = StealthOnErrorContainer,
        background = StealthBlack,
        onBackground = StealthWhite,
        surface = StealthSurface,
        onSurface = StealthWhite,
        surfaceVariant = StealthSurfaceVariant,
        onSurfaceVariant = StealthOnSurfaceVariant,
        outline = StealthOutline,
        outlineVariant = StealthOutlineVariant,
        surfaceContainerLowest = StealthBlack,
        surfaceContainerLow = StealthSurface,
        surfaceContainer = StealthSurfaceContainer,
        surfaceContainerHigh = StealthSurfaceContainerHigh,
        surfaceContainerHighest = StealthSurfaceVariant,
    )

/**
 * IMPSTR theme entry point.
 *
 * @param darkTheme Follow the system dark/light setting. Has no effect when
 *   [isStealthMode] is true.
 * @param isStealthMode Switches the entire app into the neon cyberpunk scheme.
 *   Dynamic color is also bypassed in stealth mode so the look stays consistent
 *   across OEMs.
 * @param dynamicColor Whether to source colors from the device wallpaper on
 *   Android 12+. Ignored in stealth mode.
 */
@Composable
fun IMPSTRTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    isStealthMode: Boolean = false,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            isStealthMode -> StealthColorScheme
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }
            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }

    // System bars are kept transparent via enableEdgeToEdge() in MainActivity.
    // We only need to keep the appearance flags in sync with the active scheme
    // so status/nav-bar icons remain legible.
    val view = LocalView.current
    if (!view.isInEditMode) {
        val isLightSurface = !(isStealthMode || darkTheme)
        SideEffect {
            val window = (view.context as? android.app.Activity)?.window ?: return@SideEffect
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = isLightSurface
                isAppearanceLightNavigationBars = isLightSurface
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        shapes = Shapes,
        typography = Typography,
        content = content,
    )
}
