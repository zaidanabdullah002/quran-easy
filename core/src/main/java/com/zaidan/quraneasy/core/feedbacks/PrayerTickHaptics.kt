package com.zaidan.quraneasy.core.feedbacks

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.annotation.RequiresPermission

class PrayerTickHaptics(
    context: Context
) {
    private val appContext = context.applicationContext
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = appContext.getSystemService(VibratorManager::class.java)
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        appContext.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    @RequiresPermission(Manifest.permission.VIBRATE)
    fun playCheck() {
        vibrateOneShot(durationMs = 18L, amplitude = 170)
    }

    @RequiresPermission(Manifest.permission.VIBRATE)
    fun playUncheck() {
        vibrateOneShot(durationMs = 12L, amplitude = 110)
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
            Log.w(TAG, "Skipping completion haptic because vibration permission is unavailable", securityException)
        }
    }

    @RequiresPermission(Manifest.permission.VIBRATE)
    private fun vibrateOneShot(durationMs: Long, amplitude: Int) {
        val deviceVibrator = vibrator ?: return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                deviceVibrator.vibrate(
                    VibrationEffect.createOneShot(durationMs, amplitude)
                )
            } else {
                @Suppress("DEPRECATION")
                deviceVibrator.vibrate(durationMs)
            }
        } catch (securityException: SecurityException) {
            Log.w(TAG, "Skipping tick haptic because vibration permission is unavailable", securityException)
        }
    }

    private companion object {
        const val TAG = "PrayerTickHaptics"
    }
}
