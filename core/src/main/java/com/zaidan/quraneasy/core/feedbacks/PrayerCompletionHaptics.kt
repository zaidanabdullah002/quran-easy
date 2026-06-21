package com.zaidan.quraneasy.core.feedbacks

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.annotation.RequiresPermission

class PrayerCompletionHaptics(
    context: Context
) {
    private val appContext = context.applicationContext
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        appContext.getSystemService(VibratorManager::class.java)?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        appContext.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    @RequiresPermission(Manifest.permission.VIBRATE)
    fun playAllCompleted() {
        val deviceVibrator = vibrator ?: return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0L, 20L, 28L, 36L)
                val amplitudes = intArrayOf(0, 150, 0, 255)
                deviceVibrator.vibrate(
                    VibrationEffect.createWaveform(timings, amplitudes, -1)
                )
            } else {
                @Suppress("DEPRECATION")
                deviceVibrator.vibrate(84L)
            }
        } catch (securityException: SecurityException) {
            Log.w(TAG, "Skipping prayer completion haptic because vibration permission is unavailable", securityException)
        }
    }

    private companion object {
        const val TAG = "PrayerCompletionHaptics"
    }
}
