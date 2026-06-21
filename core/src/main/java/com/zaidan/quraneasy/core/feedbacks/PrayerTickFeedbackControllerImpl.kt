package com.zaidan.quraneasy.core.feedbacks

import android.Manifest
import android.media.AudioManager
import android.media.ToneGenerator
import androidx.annotation.RequiresPermission
import com.zaidan.quraneasy.core.controllers.PrayerTickFeedbackController
import com.zaidan.quraneasy.core.ui.AppHapticType
import com.zaidan.quraneasy.core.ui.AppHaptics

class PrayerTickFeedbackControllerImpl(
    private val appHaptics: AppHaptics,
    private val prayerCompletionHaptics: PrayerCompletionHaptics
) : PrayerTickFeedbackController {
    private val toneGenerator = ToneGenerator(AudioManager.STREAM_SYSTEM, 72)

    @RequiresPermission(Manifest.permission.VIBRATE)
    override fun playCheckFeedback() {
        appHaptics.perform(AppHapticType.ToggleOn)
        toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP2, 45)
    }

    @RequiresPermission(Manifest.permission.VIBRATE)
    override fun playUncheckFeedback() {
        appHaptics.perform(AppHapticType.ToggleOff)
        toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 30)
    }

    @RequiresPermission(Manifest.permission.VIBRATE)
    override fun playAllPrayersCompletedFeedback() {
        prayerCompletionHaptics.playAllCompleted()
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 140)
    }

    override fun release() {
        toneGenerator.release()
    }
}
