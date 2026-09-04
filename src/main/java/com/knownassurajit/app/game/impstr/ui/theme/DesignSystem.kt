package com.knownassurajit.app.game.impstr.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * IMPSTR Design System — Material 3 spacing, elevation, and semantic game colors.
 * Grid: 4dp base / 8dp layout rhythm. Compact screens use 16dp page margins;
 * sheets use 24dp per the M3 bottom-sheet spec.
 */
object Dimens {
    val ScreenHorizontal: Dp = 16.dp
    val ScreenVertical: Dp = 16.dp
    val SheetPadding: Dp = 24.dp
    val CardPadding: Dp = 16.dp
    val CardPaddingTight: Dp = 12.dp

    val OpticalInset: Dp = 2.dp
    val SpacingXs: Dp = 4.dp
    val SpacingSm: Dp = 8.dp
    val SpacingMd: Dp = 12.dp
    val SpacingLg: Dp = 16.dp
    val SpacingXl: Dp = 24.dp
    val SpacingXxl: Dp = 32.dp

    val SideSheetWidth: Dp = 320.dp

    val TouchTargetMin: Dp = 48.dp
    val ButtonHeight: Dp = 56.dp
    val BottomBarPadding: Dp = 16.dp
    val IconSize: Dp = 24.dp
    val IconSizeSm: Dp = 20.dp
    val IconSizeXs: Dp = 16.dp
    val BorderWidth: Dp = 1.dp
    val DividerThickness: Dp = 1.dp

    val ElevationNone: Dp = 0.dp
    val ElevationBase: Dp = 1.dp
    val ElevationSlight: Dp = 3.dp
    val ElevationHigh: Dp = 6.dp
    val ElevationMax: Dp = 8.dp

    val TimerCircleOuter: Dp = 280.dp
    val TimerCircleInner: Dp = 240.dp
    val AvatarLarge: Dp = 72.dp
    val AvatarMedium: Dp = 48.dp
    val AvatarSmall: Dp = 40.dp
    val InfoCardHeight: Dp = 112.dp
    val VoteCardHeight: Dp = 180.dp
    val RevealHeroHeight: Dp = 140.dp
    val ProgressHeight: Dp = 8.dp
}

object Alpha {
    const val High = 0.87f
    const val Medium = 0.60f
    const val Disabled = 0.38f
    const val Divider = 0.12f
    const val Scrim = 0.32f
}

object Corners {
    val BottomBar = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    val SideSheet = RoundedCornerShape(topStart = 28.dp, bottomStart = 28.dp)
    val Badge = RoundedCornerShape(4.dp)
}

object Anim {
    const val DurationFast = 200
    const val DurationMedium = 300
    const val DurationCardFlip = 300

    val EmphasizedEasing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)
    val DecelerateEasing = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f)
}

object GameColors {
    val ImposterRed = Color(0xFFEF4444)
    val ImposterRedDim = Color(0xFFEF4444).copy(alpha = 0.7f)
    val CrewmateGreen = Color(0xFF1B7A3D)
    val OnVibrant = Color(0xFF1C1B1F)
    val CardGradientBlueStart = Color(0xFF1E1B4B)
    val CardGradientBlueEnd = Color(0xFF0F172A)
    val CardGradientRedStart = Color(0xFF450A0A)
    val CardGradientRedEnd = Color(0xFF000000)
    val WinGradientGreenStart = Color(0xFF064E3B)
    val WinGradientRedStart = Color(0xFF7F1D1D)
    val CursorBlue = Color(0xFF3B82F6)
    val OnCursor = Color(0xFF000000)
}
