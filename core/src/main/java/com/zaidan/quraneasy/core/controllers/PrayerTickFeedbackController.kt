package com.zaidan.quraneasy.core.controllers

interface PrayerTickFeedbackController {
    fun playCheckFeedback()
    fun playUncheckFeedback()
    fun playAllPrayersCompletedFeedback()
    fun release()
}