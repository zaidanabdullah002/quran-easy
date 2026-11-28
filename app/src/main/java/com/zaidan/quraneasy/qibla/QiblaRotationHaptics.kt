package com.zaidan.quraneasy.qibla

import android.os.Build
import android.os.SystemClock
import android.view.HapticFeedbackConstants
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
internal fun QiblaRotationHaptics(heading: Double?, enabled: Boolean) {
    val view = LocalView.current
    val owner = LocalLifecycleOwner.current
    val gate = remember { RotationTickGate() }
    DisposableEffect(owner, gate) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) gate.reset()
        }
        owner.lifecycle.addObserver(observer)
        onDispose {
            owner.lifecycle.removeObserver(observer)
            gate.reset()
        }
    }
    LaunchedEffect(heading, enabled, view, owner) {
        val active = enabled && view.hasWindowFocus() &&
            owner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
        if (gate.shouldTick(heading, active, SystemClock.elapsedRealtime())) {
            // Let Android select the hardware-specific light tick and honor the
            // user's haptic settings; never force a vibration waveform.
            view.performHapticFeedback(
                if (Build.VERSION.SDK_INT >= 34) HapticFeedbackConstants.SEGMENT_FREQUENT_TICK
                else HapticFeedbackConstants.CLOCK_TICK
            )
        }
    }
}
