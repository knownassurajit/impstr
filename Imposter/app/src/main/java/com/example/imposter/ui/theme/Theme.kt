package com.example.imposter.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Material Design 3 Dark Color Scheme
 * Optimized for dark theme with proper elevation and contrast
 */
private val DarkColorScheme = darkColorScheme(
    // Primary colors
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,
    
    // Secondary colors
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = SecondaryContainerDark,
    onSecondaryContainer = OnSecondaryContainerDark,
    
    // Tertiary colors
    tertiary = TertiaryDark,
    onTertiary = OnTertiaryDark,
    tertiaryContainer = TertiaryContainerDark,
    onTertiaryContainer = OnTertiaryContainerDark,
    
    // Error colors
    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark,
    
    // Background colors
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    
    // Surface colors
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    
    // Outline colors
    outline = OutlineDark,
    outlineVariant = OutlineVariantDark
)

/**
 * Material Design 3 Light Color Scheme
 * For future light theme support
 */
private val LightColorScheme = lightColorScheme(
    // Primary colors
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryDark,
    
    // Secondary colors
    secondary = SecondaryLight,
    onSecondary = OnSecondaryLight,
    secondaryContainer = CardTeal,
    onSecondaryContainer = OnSecondaryDark,
    
    // Tertiary colors
    tertiary = CardPurple,
    onTertiary = OnTertiaryDark,
    tertiaryContainer = TertiaryContainerDark,
    onTertiaryContainer = OnTertiaryDark,
    
    // Background colors
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    
    // Surface colors
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),
    
    // Error colors
    error = ErrorDark,
    onError = White,
    
    // Outline colors
    outline = Color(0xFF94A3B8),
    outlineVariant = Color(0xFFCBD5E1),
)

/**
 * Material Design 3 Theme
 * 
 * @param darkTheme Whether to use dark theme (default: true, follows system)
 * @param dynamicColor Whether to use dynamic color (Android 12+)
 * @param content The composable content
 */
@Composable
fun ImposterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
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
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
