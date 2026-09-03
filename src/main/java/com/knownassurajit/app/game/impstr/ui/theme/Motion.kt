package com.knownassurajit.app.game.impstr.ui.theme

import androidx.compose.animation.core.CubicBezierEasing

// Material 3 Expressive Motion Tokens
object Motion {
    // Standard Easing
    val EmphasizedEasing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
    val EmphasizedDecelerateEasing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)
    val EmphasizedAccelerateEasing = CubicBezierEasing(0.3f, 0.0f, 0.8f, 0.15f)

    // Legacy / Standard
    val StandardEasing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
}
