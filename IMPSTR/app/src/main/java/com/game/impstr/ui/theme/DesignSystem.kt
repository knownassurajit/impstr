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
 *
 * All spacing, elevation, animation, and semantic color values should be
 * referenced from here instead of using ad-hoc literals in screens.
 */
object Dimens {
    // Screen Padding
    val ScreenHorizontal: Dp = 24.dp
    val ScreenVertical: Dp = 24.dp

    // Section Spacing
    val SpacingXs: Dp = 4.dp
    val SpacingSm: Dp = 8.dp
    val SpacingMd: Dp = 12.dp
    val SpacingLg: Dp = 16.dp
    val SpacingXl: Dp = 24.dp
    val SpacingXxl: Dp = 32.dp

    // Button Heights
    val ButtonHeight: Dp = 56.dp
    val ButtonHeightSmall: Dp = 48.dp

    // Bottom Bar
    val BottomBarPadding: Dp = 24.dp

    // Elevation
    val ElevationNone: Dp = 0.dp
    val ElevationLow: Dp = 2.dp
    val ElevationMedium: Dp = 4.dp
    val ElevationHigh: Dp = 8.dp

    // Card Sizes
    val TimerCircleOuter: Dp = 280.dp
    val TimerCircleInner: Dp = 240.dp
    val AvatarLarge: Dp = 72.dp
    val AvatarMedium: Dp = 48.dp
    val AvatarSmall: Dp = 40.dp
}

/**
 * Corner shape tokens for consistent rounding across all components.
 * Prefer these over hardcoded RoundedCornerShape values.
 */
object Corners {
    val BottomBar = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    val Badge = RoundedCornerShape(4.dp)
}

/**
 * Animation duration and easing tokens.
 */
object Anim {
    const val DurationFast = 200
    const val DurationMedium = 300
    const val DurationSlow = 500
    const val DurationCardFlip = 400

    val EmphasizedEasing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
    val DecelerateEasing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)
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
