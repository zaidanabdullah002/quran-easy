package com.zaidan.quraneasy.feature.prayer.domain

sealed interface PrayerDataResult {
    data object Fresh : PrayerDataResult
    data object Cached : PrayerDataResult
    data object Unavailable : PrayerDataResult
}
