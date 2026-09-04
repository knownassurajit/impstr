package com.knownassurajit.app.game.impstr.ui.components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.knownassurajit.app.game.impstr.ui.LocalInteractionTime
import kotlinx.coroutines.delay

/**
 * Keeps the screen on during active game phases, then releases the flag
 * after a stretch of no input so idle devices can dim normally.
 */
@Composable
fun KeepScreenOn(idleTimeoutMs: Long = 180_000L) {
    val context = LocalContext.current
    val activity = context.findActivity()
    val interactionTime = LocalInteractionTime.current

    DisposableEffect(activity) {
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    if (interactionTime != null) {
        val lastTouch = interactionTime.value
        LaunchedEffect(lastTouch, idleTimeoutMs) {
            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            delay(idleTimeoutMs)
            if (System.currentTimeMillis() - interactionTime.value >= idleTimeoutMs) {
                activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }
    }
}

private fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}
