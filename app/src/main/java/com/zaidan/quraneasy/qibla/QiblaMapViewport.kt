package com.zaidan.quraneasy.qibla

import kotlin.math.log2
import kotlin.math.pow

/** Single-world limits for the north-up, untilted MapLibre viewport (512 dp at zoom 0). */
internal object QiblaMapViewport {
    fun minimumZoom(widthDp: Double): Double = log2(widthDp.coerceAtLeast(1.0) / 512.0).coerceAtLeast(0.0) + .001

    fun longitudeLimit(widthDp: Double, zoom: Double): Double {
        val halfSpan = 180.0 * widthDp / (512.0 * 2.0.pow(zoom))
        return (180.0 - halfSpan - .0001).coerceIn(.0001, 179.9999)
    }
}
