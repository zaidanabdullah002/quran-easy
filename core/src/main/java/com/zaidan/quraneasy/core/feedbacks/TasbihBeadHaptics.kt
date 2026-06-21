package com.zaidan.quraneasy.core.feedbacks

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.annotation.RequiresPermission

class TasbihBeadHaptics(
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
    fun playGrip() {
        vibrateOneShot(durationMs = 10L, amplitude = 90)
    }

    @RequiresPermission(Manifest.permission.VIBRATE)
    fun playTension() {
        vibrateOneShot(durationMs = 14L, amplitude = 135)
    }

    @RequiresPermission(Manifest.permission.VIBRATE)
    fun playRelease() {
        val deviceVibrator = vibrator ?: return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0L, 12L, 18L, 22L)
                val amplitudes = intArrayOf(0, 110, 0, 210)
                deviceVibrator.vibrate(
                    VibrationEffect.createWaveform(timings, amplitudes, -1)
                )
            } else {
                @Suppress("DEPRECATION")
                deviceVibrator.vibrate(52L)
            }
        } catch (securityException: SecurityException) {
            Log.w(TAG, "Skipping tasbih bead release haptic", securityException)
        }
    }

    @RequiresPermission(Manifest.permission.VIBRATE)
    fun playSnapBack() {
        vibrateOneShot(durationMs = 9L, amplitude = 70)
    }

    @RequiresPermission(Manifest.permission.VIBRATE)
    private fun vibrateOneShot(durationMs: Long, amplitude: Int) {
        val deviceVibrator = vibrator ?: return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                deviceVibrator.vibrate(VibrationEffect.createOneShot(durationMs, amplitude))
            } else {
                @Suppress("DEPRECATION")
                deviceVibrator.vibrate(durationMs)
            }
        } catch (securityException: SecurityException) {
            Log.w(TAG, "Skipping tasbih bead haptic", securityException)
        }
    }

    private companion object {
        const val TAG = "TasbihBeadHaptics"
    }
}
