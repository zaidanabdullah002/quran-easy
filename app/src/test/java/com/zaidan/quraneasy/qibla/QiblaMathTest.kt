package com.zaidan.quraneasy.qibla

import org.junit.Assert.*
import org.junit.Test

class QiblaMathTest {
    @Test fun delhiBearingAndDistance() {
        val delhi = QiblaLocation(28.6139, 77.2090, "Delhi")
        assertEquals(266.6, QiblaMath.bearing(delhi), .2)
        assertEquals(3835.0, QiblaMath.distanceKm(delhi), 10.0)
    }
    @Test fun shortestTurnAcrossNorth() {
        assertEquals(2.0, QiblaMath.turn(359.0, 1.0), 0.0)
        assertEquals(-2.0, QiblaMath.turn(1.0, 359.0), 0.0)
    }
    @Test fun coincidentLocationIsFinite() {
        assertEquals(0.0, QiblaMath.distanceKm(QiblaMath.kaaba), 0.0)
        assertTrue(QiblaMath.bearing(QiblaMath.kaaba).isFinite())
    }
    @Test fun greatCircleReachesKaabaFromNewZealand() {
        val from = QiblaLocation(-43.5321, 172.6362, "Christchurch")
        val path = QiblaMath.path(from)
        assertEquals(from.latitude, path.first().latitude, 1e-8)
        assertEquals(QiblaMath.kaaba.longitude, path.last().longitude, 1e-8)
        assertTrue(path.all { it.latitude.isFinite() && it.longitude in -180.0..180.0 })
    }
}
