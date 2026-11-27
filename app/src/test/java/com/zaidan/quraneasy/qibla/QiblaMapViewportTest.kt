package com.zaidan.quraneasy.qibla

import kotlin.math.pow
import org.junit.Assert.*
import org.junit.Test

class QiblaMapViewportTest {
    @Test fun portraitLandscapeAndTabletStayInsideOneWorld() {
        for (width in listOf(360.0, 800.0, 1280.0, 2048.0)) {
            val minimum = QiblaMapViewport.minimumZoom(width)
            for (zoom in listOf(minimum, minimum + 1, 8.0)) {
                val halfVisibleLongitude = 180.0 * width / (512 * 2.0.pow(zoom))
                val limit = QiblaMapViewport.longitudeLimit(width, zoom)
                assertTrue("East edge repeats at width $width, zoom $zoom", limit + halfVisibleLongitude <= 180.0)
                assertTrue("West edge repeats at width $width, zoom $zoom", -limit - halfVisibleLongitude >= -180.0)
            }
        }
    }
    @Test fun wideningViewRaisesZoomFloorAndZoomingInAllowsMorePanning() {
        assertTrue(QiblaMapViewport.minimumZoom(1280.0) > QiblaMapViewport.minimumZoom(360.0))
        assertTrue(QiblaMapViewport.longitudeLimit(1280.0, 4.0) > QiblaMapViewport.longitudeLimit(1280.0, 2.0))
    }
}
