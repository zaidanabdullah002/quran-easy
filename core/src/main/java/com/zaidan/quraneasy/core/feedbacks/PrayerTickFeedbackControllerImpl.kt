package com.zaidan.quraneasy.core.feedbacks

import android.Manifest
import android.media.AudioManager
import android.media.ToneGenerator
import androidx.annotation.RequiresPermission
import com.zaidan.quraneasy.core.controllers.PrayerTickFeedbackController

class PrayerTickFeedbackControllerImpl(
    private val haptics: PrayerTickHaptics
) : PrayerTickFeedbackController {
    private val toneGenerator = ToneGenerator(AudioManager.STREAM_SYSTEM, 72)

    @RequiresPermission(Manifest.permission.VIBRATE)
    override fun playCheckFeedback() {
        haptics.playCheck()
        toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP2, 45)
    }

    @RequiresPermission(Manifest.permission.VIBRATE)
    override fun playUncheckFeedback() {
        haptics.playUncheck()
        toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 30)
    }

    @RequiresPermission(Manifest.permission.VIBRATE)
    override fun playAllPrayersCompletedFeedback() {
        haptics.playAllCompleted()
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 140)
    }

    override fun release() {
        toneGenerator.release()
    }
}
