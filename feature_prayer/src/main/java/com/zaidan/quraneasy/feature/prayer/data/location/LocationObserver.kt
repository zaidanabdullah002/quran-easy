package com.zaidan.quraneasy.feature.prayer.data.location

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationObserver {
    fun observeLocationUpdates(): Flow<Location>
}
