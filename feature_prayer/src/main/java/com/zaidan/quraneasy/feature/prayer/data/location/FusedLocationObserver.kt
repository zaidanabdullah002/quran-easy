package com.zaidan.quraneasy.feature.prayer.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FusedLocationObserver @Inject constructor(
    @ApplicationContext private val context: Context
) : LocationObserver {

    companion object {
        private const val TAG = "FusedLocationObserver"
    }

    @SuppressLint("MissingPermission")
    override fun observeLocationUpdates(): Flow<Location> = callbackFlow {
        Log.i(TAG, "observeLocationUpdates(): creating callbackFlow")
        val client = LocationServices.getFusedLocationProviderClient(context)
        val request = LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 60_000L)
            .setMinUpdateDistanceMeters(500f)
            .build()
        Log.d(TAG, "observeLocationUpdates(): request=$request")

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                Log.d(TAG, "onLocationResult(): result=$result")
                result.lastLocation?.let {
                    Log.i(TAG, "onLocationResult(): emitting lat=${it.latitude}, long=${it.longitude}")
                    trySend(it).isSuccess
                }
            }
        }

        client.lastLocation.addOnSuccessListener { location ->
            Log.d(TAG, "observeLocationUpdates(): lastLocation success location=$location")
            location?.let {
                Log.i(TAG, "observeLocationUpdates(): emitting cached lat=${it.latitude}, long=${it.longitude}")
                trySend(it).isSuccess
            }
        }.addOnFailureListener { error ->
            Log.e(TAG, "observeLocationUpdates(): lastLocation failed", error)
        }
        Log.i(TAG, "observeLocationUpdates(): requesting location updates")
        client.requestLocationUpdates(request, callback, context.mainLooper)

        awaitClose {
            Log.i(TAG, "observeLocationUpdates(): removing location updates")
            client.removeLocationUpdates(callback)
        }
    }
}
