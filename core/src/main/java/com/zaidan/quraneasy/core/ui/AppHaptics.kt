package com.zaidan.quraneasy.core.ui

import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalView

enum class AppHapticType {
    Tap,
    ToggleOn,
    ToggleOff,
    Success,
    Reject
}

class AppHaptics(
    private val view: View
) {
    fun perform(type: AppHapticType = AppHapticType.Tap) {
        val constant = when (type) {
            AppHapticType.Tap -> HapticFeedbackConstants.KEYBOARD_TAP
            AppHapticType.ToggleOn -> HapticFeedbackConstants.CLOCK_TICK
            AppHapticType.ToggleOff -> HapticFeedbackConstants.VIRTUAL_KEY
            AppHapticType.Success -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    HapticFeedbackConstants.CONFIRM
                } else {
                    HapticFeedbackConstants.KEYBOARD_TAP
                }
            }
            AppHapticType.Reject -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    HapticFeedbackConstants.REJECT
                } else {
                    HapticFeedbackConstants.LONG_PRESS
                }
            }
        }

        view.performHapticFeedback(constant)
    }
}

@Composable
fun rememberAppHaptics(): AppHaptics {
    val view = LocalView.current
    return remember(view) { AppHaptics(view) }
}

@Composable
fun hapticClick(
    type: AppHapticType = AppHapticType.Tap,
    onClick: () -> Unit
): () -> Unit {
    val haptics = rememberAppHaptics()
    return remember(haptics, type, onClick) {
        {
            haptics.perform(type)
            onClick()
        }
    }
}

fun Modifier.hapticClickable(
    enabled: Boolean = true,
    type: AppHapticType = AppHapticType.Tap,
    onClick: () -> Unit
): Modifier = composed {
    val haptics = rememberAppHaptics()
    clickable(enabled = enabled) {
        haptics.perform(type)
        onClick()
    }
}
