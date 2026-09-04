package com.knownassurajit.app.game.impstr.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

class DesignTokensTest {

    @Test
    fun spacing_followsFourDpGrid() {
        val tokens =
            listOf(
                Dimens.ScreenHorizontal,
                Dimens.ScreenVertical,
                Dimens.SheetPadding,
                Dimens.CardPadding,
                Dimens.CardPaddingTight,
                Dimens.SpacingXs,
                Dimens.SpacingSm,
                Dimens.SpacingMd,
                Dimens.SpacingLg,
                Dimens.SpacingXl,
                Dimens.SpacingXxl,
                Dimens.SideSheetWidth,
                Dimens.TouchTargetMin,
                Dimens.ButtonHeight,
                Dimens.BottomBarPadding,
                Dimens.IconSize,
            )
        tokens.forEach { token ->
            assertEquals("Token $token must sit on the 4dp grid", 0, token.value.toInt() % 4)
        }
    }

    @Test
    fun touchTargets_meetAccessibilityMinimum() {
        assertTrue(Dimens.TouchTargetMin >= 48.dp)
        assertTrue(Dimens.ButtonHeight >= Dimens.TouchTargetMin)
        assertTrue(Dimens.AvatarMedium >= 48.dp)
    }

    @Test
    fun typeScale_matchesMaterial3Sizes() {
        assertEquals(57, Typography.displayLarge.fontSize.value.toInt())
        assertEquals(16, Typography.bodyLarge.fontSize.value.toInt())
        assertEquals(16, Typography.titleMedium.fontSize.value.toInt())
        assertEquals(24, Typography.bodyLarge.lineHeight.value.toInt())
        assertEquals(DisplayFont, Typography.bodyLarge.fontFamily)
        assertEquals(DisplayFont, Typography.displayLarge.fontFamily)
        assertEquals(DisplayFont, LogoFont)
    }

    @Test
    fun darkAndLightPalettes_areDistinctAndContrasting() {
        assertNotEquals(Background, LightBackground)
        assertNotEquals(OnBackground, LightOnBackground)
        assertTrue(contrast(OnPrimary, Primary) >= 4.5)
        assertTrue(contrast(OnBackground, Background) >= 4.5)
        assertTrue(contrast(LightOnPrimary, LightPrimary) >= 4.5)
        assertTrue(contrast(LightOnBackground, LightBackground) >= 4.5)
        assertTrue(contrast(OnPrimaryContainer, PrimaryContainer) >= 3.0)
        assertTrue(contrast(LightOnPrimaryContainer, LightPrimaryContainer) >= 4.5)
    }

    @Test
    fun semanticGameColors_keepRoleContrast() {
        assertTrue(contrast(Color.White, GameColors.CrewmateGreen) >= 2.5)
        assertTrue(contrast(Color.White, GameColors.ImposterRed) >= 3.0)
        assertEquals(Color(0xFF3B82F6), GameColors.CursorBlue)
    }

    private fun contrast(
        foreground: Color,
        background: Color,
    ): Double {
        val lighter = max(relativeLuminance(foreground), relativeLuminance(background))
        val darker = min(relativeLuminance(foreground), relativeLuminance(background))
        return (lighter + 0.05) / (darker + 0.05)
    }

    private fun relativeLuminance(color: Color): Double {
        fun linearize(channel: Float): Double {
            val value = channel.toDouble()
            return if (value <= 0.04045) value / 12.92 else ((value + 0.055) / 1.055).pow(2.4)
        }
        return 0.2126 * linearize(color.red) + 0.7152 * linearize(color.green) + 0.0722 * linearize(color.blue)
    }
}
