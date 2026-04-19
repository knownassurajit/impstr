package com.game.impstr.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * IMPSTR Design System — Centralized design tokens.
 * Grid system: 4dp base multiples.
 */
object Dimens {
    // Screen Padding (16dp-24dp standard)
    val ScreenHorizontal: Dp = 24.dp
    val ScreenVertical: Dp = 24.dp

    // Section Spacing (4dp mutiples)
    val SpacingXs: Dp = 4.dp
    val SpacingSm: Dp = 8.dp     // Tightly related
    val SpacingMd: Dp = 12.dp    // Standard separation
    val SpacingLg: Dp = 16.dp    // Standard separation higher
    val SpacingXl: Dp = 24.dp    // Distinct sections
    val SpacingXxl: Dp = 32.dp

    // Component Sizes
    val TouchTargetMin: Dp = 48.dp
    val ButtonHeight: Dp = 48.dp
    val BottomBarPadding: Dp = 24.dp
    val IconSize: Dp = 24.dp
    val BorderWidth: Dp = 1.dp

    // Elevation (Tonal)
    val ElevationNone: Dp = 0.dp
    val ElevationBase: Dp = 1.dp
    val ElevationSlight: Dp = 3.dp
    val ElevationHigh: Dp = 6.dp
    val ElevationMax: Dp = 8.dp

    // Card Sizes
    val TimerCircleOuter: Dp = 280.dp
    val TimerCircleInner: Dp = 240.dp
    val AvatarLarge: Dp = 72.dp
    val AvatarMedium: Dp = 48.dp
    val AvatarSmall: Dp = 40.dp
}

/**
 * Standard opacities for content emphasis.
 */
object Alpha {
    const val High = 0.87f
    const val Medium = 0.60f
    const val Disabled = 0.38f
    const val Divider = 0.12f
}

/**
 * Corner shape tokens for consistent rounding. (Currently handled in Shapes.kt mostly)
 */
object Corners {
    val BottomBar = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    val Badge = RoundedCornerShape(4.dp)
}

/**
 * Animation duration and easing tokens.
 */
object Anim {
    const val DurationFast = 200
    const val DurationMedium = 300
    const val DurationCardFlip = 300

    val EmphasizedEasing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f) // Fast out, slow in
    val DecelerateEasing = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f)  // Linear out, slow in
}

/**
 * Semantic colors for game-specific elements.
 */
object GameColors {
    val ImposterRed = Color(0xFFef4444)
    val ImposterRedDim = Color(0xFFef4444).copy(alpha = 0.7f)
    val CrewmateGreen = Color(0xFF4CAF50)
    val CardGradientBlueStart = Color(0xFF1e1b4b)
    val CardGradientBlueEnd = Color(0xFF0f172a)
    val CardGradientRedStart = Color(0xFF450a0a)
    val CardGradientRedEnd = Color(0xFF000000)
    val WinGradientGreenStart = Color(0xFF064e3b)
    val WinGradientRedStart = Color(0xFF7f1d1d)
    val CursorBlue = Color(0xFF3B82F6)
}
