package com.zaidan.quraneasy.qibla

import org.junit.Assert.*
import org.junit.Test
import kotlin.math.abs

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
    @Test fun headingFilterCrossesNorthUsingShortestPath() {
        val clockwise = QiblaMath.filterHeading(359.0, 1.0)
        val counterClockwise = QiblaMath.filterHeading(1.0, 359.0)
        assertTrue(QiblaMath.turn(359.0, clockwise) > 0.0)
        assertTrue(QiblaMath.turn(1.0, counterClockwise) < 0.0)
        assertTrue(abs(QiblaMath.turn(clockwise, 1.0)) < 1.0)
        assertTrue(abs(QiblaMath.turn(counterClockwise, 359.0)) < 1.0)
    }
    @Test fun headingFilterRespondsQuicklyToLargeTurns() {
        var heading: Double? = 0.0
        repeat(2) { heading = QiblaMath.filterHeading(heading, 90.0) }
        assertTrue(abs(QiblaMath.turn(heading!!, 90.0)) < 2.0)
    }
    @Test fun headingFilterRejectsSubDegreeNoise() {
        assertEquals(120.0, QiblaMath.filterHeading(120.0, 120.9), 0.0)
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
