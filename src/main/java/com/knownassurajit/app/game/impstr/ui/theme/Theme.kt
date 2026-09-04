package com.knownassurajit.app.game.impstr.ui.theme

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
        inverseSurface = InverseSurface,
        inverseOnSurface = InverseOnSurface,
        inversePrimary = InversePrimary,
        scrim = Scrim,
        surfaceTint = Primary,
        surfaceDim = SurfaceDim,
        surfaceBright = SurfaceBright,
        surfaceContainerLowest = SurfaceContainerLowest,
        surfaceContainerLow = SurfaceContainerLow,
        surfaceContainer = SurfaceContainer,
        surfaceContainerHigh = SurfaceContainerHigh,
        surfaceContainerHighest = SurfaceContainerHighest,
    )

private val LightColorScheme =
    lightColorScheme(
        primary = LightPrimary,
        onPrimary = LightOnPrimary,
        primaryContainer = LightPrimaryContainer,
        onPrimaryContainer = LightOnPrimaryContainer,
        secondary = LightSecondary,
        onSecondary = LightOnSecondary,
        secondaryContainer = LightSecondaryContainer,
        onSecondaryContainer = LightOnSecondaryContainer,
        tertiary = LightTertiary,
        onTertiary = LightOnTertiary,
        tertiaryContainer = LightTertiaryContainer,
        onTertiaryContainer = LightOnTertiaryContainer,
        error = LightError,
        onError = LightOnError,
        errorContainer = LightErrorContainer,
        onErrorContainer = LightOnErrorContainer,
        background = LightBackground,
        onBackground = LightOnBackground,
        surface = LightSurface,
        onSurface = LightOnSurface,
        surfaceVariant = LightSurfaceVariant,
        onSurfaceVariant = LightOnSurfaceVariant,
        outline = LightOutline,
        outlineVariant = LightOutlineVariant,
        inverseSurface = LightInverseSurface,
        inverseOnSurface = LightInverseOnSurface,
        inversePrimary = LightInversePrimary,
        scrim = Scrim,
        surfaceTint = LightPrimary,
        surfaceDim = LightSurfaceDim,
        surfaceBright = LightSurfaceBright,
        surfaceContainerLowest = LightSurfaceContainerLowest,
        surfaceContainerLow = LightSurfaceContainerLow,
        surfaceContainer = LightSurfaceContainer,
        surfaceContainerHigh = LightSurfaceContainerHigh,
        surfaceContainerHighest = LightSurfaceContainerHighest,
    )

private val StealthColorScheme =
    darkColorScheme(
        primary = StealthViolet,
        onPrimary = StealthWhite,
        primaryContainer = StealthPurple,
        onPrimaryContainer = StealthWhite,
        secondary = StealthLime,
        onSecondary = StealthOnLime,
        secondaryContainer = StealthSurfaceVariant,
        onSecondaryContainer = StealthLime,
        tertiary = StealthPurple,
        onTertiary = StealthWhite,
        tertiaryContainer = StealthViolet,
        onTertiaryContainer = StealthWhite,
        error = StealthError,
        onError = StealthWhite,
        errorContainer = StealthSurfaceVariant,
        onErrorContainer = StealthError,
        background = StealthBlack,
        onBackground = StealthWhite,
        surface = StealthSurface,
        onSurface = StealthWhite,
        surfaceVariant = StealthSurfaceVariant,
        onSurfaceVariant = StealthWhite,
        outline = StealthLime,
        outlineVariant = StealthViolet,
        inverseSurface = StealthWhite,
        inverseOnSurface = StealthBlack,
        inversePrimary = StealthLime,
        scrim = Scrim,
        surfaceTint = StealthViolet,
        surfaceDim = StealthBlack,
        surfaceBright = StealthSurfaceContainerHighest,
        surfaceContainerLowest = StealthSurfaceContainerLowest,
        surfaceContainerLow = StealthSurfaceContainerLow,
        surfaceContainer = StealthSurfaceContainer,
        surfaceContainerHigh = StealthSurfaceContainerHigh,
        surfaceContainerHighest = StealthSurfaceContainerHighest,
    )

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

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context.findActivity())?.window
            if (window != null) {
                val lightBars = !isStealthMode && !darkTheme
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = lightBars
                    isAppearanceLightNavigationBars = lightBars
                }
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

private fun android.content.Context.findActivity(): android.app.Activity? {
    var context = this
    while (context is android.content.ContextWrapper) {
        if (context is android.app.Activity) return context
        context = context.baseContext
    }
    return null
}
