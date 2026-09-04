package com.knownassurajit.app.game.impstr.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf

val LocalInteractionTime =
    compositionLocalOf<MutableState<Long>?> {
        null
    }
