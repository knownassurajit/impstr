package com.game.impstr.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.game.impstr.ui.theme.LogoFont

/**
 * Animated IMPSTR logo composable — a faithful Compose reproduction of IMPSTR.svg.
 *
 * Features:
 * - Blue cursor rectangle (#3B82F6) behind "M" with rounded corners
 * - 1200ms infinite blink cycle matching SVG keyframes:
 *     visible 0–588ms → hidden 600–1200ms
 * - "M" fill toggles black ↔ onBackground in sync with cursor
 * - Clickable: tapping the logo triggers the [onClick] callback.
 */
@Composable
fun ImpstrLogo(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    textStyle: TextStyle = MaterialTheme.typography.displayMedium.copy(
        fontFamily = LogoFont,
        letterSpacing = 2.sp,
    ),
) {
    val infiniteTransition = rememberInfiniteTransition(label = "impstrBlink")

    // Cursor opacity — matches SVG @keyframes blink exactly
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1200
                1f at 0
                1f at 588
                0f at 600
                0f at 1200
            },
            repeatMode = RepeatMode.Restart,
        ),
        label = "cursorAlpha",
    )

    Row(
        modifier = modifier.clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // "I"
        Text(
            text = "I",
            style = textStyle,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Black,
        )

        // "M" with animated cursor background
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(horizontal = 2.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF3B82F6).copy(alpha = cursorAlpha))
                .padding(horizontal = 4.dp),
        ) {
            Text(
                text = "M",
                style = textStyle,
                color = if (cursorAlpha > 0.5f) Color.Black else MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Black,
            )
        }

        // "PSTR"
        Text(
            text = "PSTR",
            style = textStyle,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Black,
        )
    }
}
