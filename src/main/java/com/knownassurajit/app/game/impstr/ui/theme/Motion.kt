package com.knownassurajit.app.game.impstr.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween

// Material 3 Expressive Motion Tokens
object Motion {
    const val DurationShort = 200
    const val DurationMedium = 350
    const val DurationLong = 500

    val EmphasizedEasing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
    val EmphasizedDecelerateEasing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)
    val EmphasizedAccelerateEasing = CubicBezierEasing(0.3f, 0.0f, 0.8f, 0.15f)
    val StandardEasing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)

    val SpatialSpring =
        spring<Float>(
            dampingRatio = 0.82f,
            stiffness = 380f,
        )
    val EffectsSpring =
        spring<Float>(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = 800f,
        )

    fun <T> emphasizedTween(duration: Int = DurationMedium) =
        tween<T>(durationMillis = duration, easing = EmphasizedEasing)

    fun <T> enterTween() =
        tween<T>(durationMillis = DurationMedium, easing = EmphasizedDecelerateEasing)

    fun <T> exitTween() =
        tween<T>(durationMillis = DurationShort, easing = EmphasizedAccelerateEasing)
}
