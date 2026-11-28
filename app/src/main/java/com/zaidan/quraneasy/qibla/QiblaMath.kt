package com.zaidan.quraneasy.qibla

import kotlin.math.*

data class QiblaLocation(val latitude: Double, val longitude: Double, val label: String)

object QiblaMath {
    val kaaba = QiblaLocation(21.422487, 39.826206, "Kaaba · Makkah")
    fun normalize(angle: Double) = (angle % 360 + 360) % 360
    fun turn(heading: Double, target: Double) = (target - heading + 540) % 360 - 180

    /**
     * Suppresses stationary compass noise while following intentional movement
     * almost immediately. UI components render this value directly and must not
     * apply another easing animation on top of it.
     */
    fun filterHeading(previous: Double?, raw: Double): Double {
        val normalizedRaw = normalize(raw)
        if (previous == null) return normalizedRaw
        val delta = turn(previous, normalizedRaw)
        val magnitude = abs(delta)
        if (magnitude < 1.0) return normalize(previous)
        val alpha = when {
            magnitude >= 20.0 -> .95
            magnitude >= 5.0 -> .85
            else -> .65
        }
        return normalize(previous + delta * alpha)
    }

    fun bearing(from: QiblaLocation): Double {
        val lat = Math.toRadians(from.latitude)
        val target = Math.toRadians(kaaba.latitude)
        val delta = Math.toRadians(kaaba.longitude - from.longitude)
        return normalize(Math.toDegrees(atan2(sin(delta) * cos(target), cos(lat) * sin(target) - sin(lat) * cos(target) * cos(delta))))
    }
    fun distanceKm(from: QiblaLocation): Double {
        val lat = Math.toRadians(from.latitude)
        val target = Math.toRadians(kaaba.latitude)
        val a = sin((target - lat) / 2).pow(2) + cos(lat) * cos(target) * sin(Math.toRadians(kaaba.longitude - from.longitude) / 2).pow(2)
        return 6371.0088 * 2 * atan2(sqrt(a.coerceIn(0.0, 1.0)), sqrt((1 - a).coerceIn(0.0, 1.0)))
    }
    // Spherical interpolation draws a great-circle path, including across the date line.
    fun path(from: QiblaLocation): List<QiblaLocation> {
        fun vector(p: QiblaLocation): DoubleArray {
            val lat = Math.toRadians(p.latitude); val lon = Math.toRadians(p.longitude)
            return doubleArrayOf(cos(lat) * cos(lon), cos(lat) * sin(lon), sin(lat))
        }
        val a = vector(from); val b = vector(kaaba)
        val omega = acos(a.indices.sumOf { a[it] * b[it] }.coerceIn(-1.0, 1.0))
        if (abs(sin(omega)) < 1e-8) return listOf(from, kaaba)
        return (0..128).map { index ->
            val t = index / 128.0
            val v = DoubleArray(3) { (sin((1 - t) * omega) * a[it] + sin(t * omega) * b[it]) / sin(omega) }
            QiblaLocation(Math.toDegrees(atan2(v[2], hypot(v[0], v[1]))), Math.toDegrees(atan2(v[1], v[0])), "")
        }
    }
}
