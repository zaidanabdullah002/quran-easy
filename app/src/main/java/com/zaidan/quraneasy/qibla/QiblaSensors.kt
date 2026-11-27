package com.zaidan.quraneasy.qibla

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.*
import android.location.*
import android.os.Looper
import android.view.Surface
import android.view.WindowManager
import androidx.compose.runtime.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

internal data class Heading(val degrees: Double? = null, val reliable: Boolean = false, val available: Boolean = true)

@Composable
internal fun rememberHeading(context: Context, location: QiblaLocation?): Heading {
    var heading by remember { mutableStateOf(Heading()) }
    val currentLocation by rememberUpdatedState(location)
    val owner = LocalLifecycleOwner.current
    DisposableEffect(context, owner) {
        val manager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor = manager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
            ?: manager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR)
        heading = Heading(available = sensor != null)
        var accuracy = SensorManager.SENSOR_STATUS_UNRELIABLE
        var filtered: Double? = null
        val listener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, value: Int) { accuracy = value }
            override fun onSensorChanged(event: SensorEvent) {
                val matrix = FloatArray(9); val rotated = FloatArray(9)
                SensorManager.getRotationMatrixFromVector(matrix, event.values)
                @Suppress("DEPRECATION")
                val rotation = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.rotation
                val axes = when (rotation) {
                    Surface.ROTATION_90 -> SensorManager.AXIS_Y to SensorManager.AXIS_MINUS_X
                    Surface.ROTATION_180 -> SensorManager.AXIS_MINUS_X to SensorManager.AXIS_MINUS_Y
                    Surface.ROTATION_270 -> SensorManager.AXIS_MINUS_Y to SensorManager.AXIS_X
                    else -> SensorManager.AXIS_X to SensorManager.AXIS_Y
                }
                SensorManager.remapCoordinateSystem(matrix, axes.first, axes.second, rotated)
                val orientation = SensorManager.getOrientation(rotated, FloatArray(3))
                val p = currentLocation
                val declination = if (p == null) 0.0 else GeomagneticField(p.latitude.toFloat(), p.longitude.toFloat(), 0f, System.currentTimeMillis()).declination.toDouble()
                val raw = QiblaMath.normalize(Math.toDegrees(orientation[0].toDouble()) + declination)
                filtered = filtered?.let { QiblaMath.normalize(it + QiblaMath.turn(it, raw) * .18) } ?: raw
                heading = Heading(filtered, accuracy >= SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM, sensor != null)
            }
        }
        var listening = false
        fun start() { if (!listening && sensor != null) { filtered = null; accuracy = 0; listening = manager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI) } }
        fun stop() { manager.unregisterListener(listener); listening = false; heading = heading.copy(degrees = null, reliable = false) }
        val observer = LifecycleEventObserver { _, event ->
            when (event) { Lifecycle.Event.ON_RESUME -> start(); Lifecycle.Event.ON_PAUSE -> stop(); else -> Unit }
        }
        owner.lifecycle.addObserver(observer)
        if (owner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) start()
        onDispose { owner.lifecycle.removeObserver(observer); stop() }
    }
    return heading
}

@Composable
internal fun ObserveLocation(context: Context, enabled: Boolean, onLocation: (QiblaLocation) -> Unit, onStatus: (String) -> Unit) {
    val owner = LocalLifecycleOwner.current
    val update by rememberUpdatedState(onLocation)
    val status by rememberUpdatedState(onStatus)
    DisposableEffect(context, owner, enabled) {
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val listener = object : LocationListener {
            override fun onLocationChanged(location: Location) { update(QiblaLocation(location.latitude, location.longitude, "Current location")); status("") }
            override fun onProviderDisabled(provider: String) { status("Location is disabled. Enable it or choose a city.") }
            override fun onProviderEnabled(provider: String) = Unit
            @Deprecated("Legacy callback") override fun onStatusChanged(provider: String?, status: Int, extras: android.os.Bundle?) = Unit
        }
        fun stop() { manager.removeUpdates(listener) }
        fun start() {
            if (!enabled) return
            val permitted = context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED || context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            if (!permitted) { status("Location permission unavailable. Choose a city or tap Use location."); return }
            try {
                val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER).filter { manager.isProviderEnabled(it) }
                if (providers.isEmpty()) status("Enable device location or choose a city.") else status("Finding your location… You can also choose a city.")
                providers.mapNotNull { manager.getLastKnownLocation(it) }.filter { System.currentTimeMillis() - it.time < 300_000 }.minByOrNull { it.accuracy }?.let(listener::onLocationChanged)
                providers.forEach { manager.requestLocationUpdates(it, 10_000L, 25f, listener, Looper.getMainLooper()) }
            } catch (_: SecurityException) { status("Location permission unavailable. Choose a city.") }
        }
        val observer = LifecycleEventObserver { _, event -> when (event) { Lifecycle.Event.ON_RESUME -> start(); Lifecycle.Event.ON_PAUSE -> stop(); else -> Unit } }
        owner.lifecycle.addObserver(observer)
        if (owner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) start()
        onDispose { owner.lifecycle.removeObserver(observer); stop() }
    }
}
