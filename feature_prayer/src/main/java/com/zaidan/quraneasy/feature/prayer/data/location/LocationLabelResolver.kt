package com.zaidan.quraneasy.feature.prayer.data.location

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

interface LocationLabelResolver {
    suspend fun resolve(latitude: Double, longitude: Double, locationKey: String): String
}

@Singleton
class AndroidLocationLabelResolver @Inject constructor(
    @ApplicationContext private val context: Context
) : LocationLabelResolver {
    override suspend fun resolve(
        latitude: Double,
        longitude: Double,
        locationKey: String
    ): String {
        val fallback = "Prayer times for $locationKey"
        return try {
            withTimeoutOrNull(10_000) {
                resolveAddress(latitude, longitude, fallback)
            } ?: fallback
        } catch (error: CancellationException) {
            throw error
        } catch (_: Exception) {
            fallback
        }
    }

    private suspend fun resolveAddress(
        latitude: Double,
        longitude: Double,
        fallback: String
    ): String {
        if (!Geocoder.isPresent()) return fallback
        val geocoder = Geocoder(context, Locale.getDefault())
        val address = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            suspendCancellableCoroutine<Address?> { continuation ->
                geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
                    if (continuation.isActive) continuation.resume(addresses.firstOrNull())
                }
            }
        } else {
            @Suppress("DEPRECATION")
            withContext(Dispatchers.IO) {
                geocoder.getFromLocation(latitude, longitude, 1)?.firstOrNull()
            }
        }
        val place = listOfNotNull(
            address?.locality,
            address?.subAdminArea,
            address?.adminArea,
            address?.countryName
        )
            .distinct()
            .take(2)
            .joinToString(", ")
        return if (place.isBlank()) fallback else "Prayer times for $place"
    }
}
