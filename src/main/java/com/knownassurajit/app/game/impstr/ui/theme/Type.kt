package com.knownassurajit.app.game.impstr.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.knownassurajit.app.game.impstr.R

/**
 * IMPSTR type scale — Material 3 sizes/line-heights/tracking with Poppins.
 * Poppins is the only bundled TrueType family (Inter/Roboto assets were invalid HTML).
 * includeFontPadding is off so Poppins ascenders stay inside the M3 line box.
 */
val DisplayFont =
    FontFamily(
        Font(R.font.poppins_regular, FontWeight.Normal),
        Font(R.font.poppins_medium, FontWeight.Medium),
        Font(R.font.poppins_semibold, FontWeight.SemiBold),
        Font(R.font.poppins_bold, FontWeight.Bold),
    )

val BodyFont = DisplayFont

val LogoFont = DisplayFont

private val ImpstrPlatformStyle = PlatformTextStyle(includeFontPadding = false)

private val ImpstrLineHeightStyle =
    LineHeightStyle(
        alignment = LineHeightStyle.Alignment.Center,
        trim = LineHeightStyle.Trim.None,
    )

private fun impstrStyle(
    fontWeight: FontWeight,
    fontSize: TextUnit,
    lineHeight: TextUnit,
    letterSpacing: TextUnit,
    fontFamily: FontFamily = DisplayFont,
): TextStyle =
    TextStyle(
        fontFamily = fontFamily,
        fontWeight = fontWeight,
        fontSize = fontSize,
        lineHeight = lineHeight,
        letterSpacing = letterSpacing,
        platformStyle = ImpstrPlatformStyle,
        lineHeightStyle = ImpstrLineHeightStyle,
    )

val Typography =
    Typography(
        displayLarge = impstrStyle(FontWeight.Bold, 57.sp, 64.sp, (-0.25).sp),
        displayMedium = impstrStyle(FontWeight.Bold, 45.sp, 52.sp, 0.sp),
        displaySmall = impstrStyle(FontWeight.Bold, 36.sp, 44.sp, 0.sp),
        headlineLarge = impstrStyle(FontWeight.SemiBold, 32.sp, 40.sp, 0.sp),
        headlineMedium = impstrStyle(FontWeight.SemiBold, 28.sp, 36.sp, 0.sp),
        headlineSmall = impstrStyle(FontWeight.SemiBold, 24.sp, 32.sp, 0.sp),
        titleLarge = impstrStyle(FontWeight.SemiBold, 22.sp, 28.sp, 0.sp, BodyFont),
        titleMedium = impstrStyle(FontWeight.SemiBold, 16.sp, 24.sp, 0.15.sp, BodyFont),
        titleSmall = impstrStyle(FontWeight.SemiBold, 14.sp, 20.sp, 0.1.sp, BodyFont),
        bodyLarge = impstrStyle(FontWeight.Normal, 16.sp, 24.sp, 0.5.sp, BodyFont),
        bodyMedium = impstrStyle(FontWeight.Normal, 14.sp, 20.sp, 0.25.sp, BodyFont),
        bodySmall = impstrStyle(FontWeight.Normal, 12.sp, 16.sp, 0.4.sp, BodyFont),
        labelLarge = impstrStyle(FontWeight.SemiBold, 14.sp, 20.sp, 0.1.sp, BodyFont),
        labelMedium = impstrStyle(FontWeight.SemiBold, 12.sp, 16.sp, 0.5.sp, BodyFont),
        labelSmall = impstrStyle(FontWeight.SemiBold, 11.sp, 16.sp, 0.5.sp, BodyFont),
    )
