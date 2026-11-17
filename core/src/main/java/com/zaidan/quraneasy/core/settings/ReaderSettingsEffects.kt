package com.zaidan.quraneasy.core.settings

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.zaidan.quraneasy.core.theme.LocalAppSettings

@Composable
fun ReaderSettingsEffects() {
    val keepScreenAwake = LocalAppSettings.current.keepScreenAwakeWhileReading
    val window = LocalContext.current.findActivity()?.window
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(window, keepScreenAwake, lifecycle) {
        if (window == null || !keepScreenAwake) {
            onDispose { }
        } else {
            var owned = false
            fun update() {
                val active = lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
                if (active && !owned &&
                    window.attributes.flags and WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON == 0) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    owned = true
                } else if (!active && owned) {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    owned = false
                }
            }
            val observer = LifecycleEventObserver { _, _ -> update() }
            lifecycle.addObserver(observer)
            update()
            onDispose {
                lifecycle.removeObserver(observer)
                if (owned) window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
