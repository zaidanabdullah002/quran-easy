package com.zaidan.quraneasy.feature.prayer.data.context

import com.zaidan.quraneasy.feature.prayer.data.location.LocationObserver
import com.zaidan.quraneasy.feature.prayer.domain.PrayerContext
import com.zaidan.quraneasy.feature.prayer.domain.PrayerLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrayerContextObserver @Inject constructor(
    private val dayObserver: DayObserver,
    private val locationObserver: LocationObserver
) {
    fun observe(): Flow<PrayerContext> =
        combine(
            dayObserver.observeDate(),
            locationObserver.observeLocationUpdates()
        ) { date, location ->
            PrayerContext(
                date = date,
                location = PrayerLocation(location.latitude, location.longitude)
            )
        }
}
