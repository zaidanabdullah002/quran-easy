package com.zaidan.quraneasy.qibla

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RotationTickGateTest {
    @Test fun smallOscillationsDoNotAccumulateIntoTicks() {
        val gate = RotationTickGate()
        assertFalse(gate.shouldTick(90.0, true, 0))
        repeat(100) { index ->
            assertFalse(gate.shouldTick(if (index % 2 == 0) 92.0 else 88.0, true, (index + 1) * 20L))
        }
    }

    @Test fun crossingNorthWorksInBothDirections() {
        val gate = RotationTickGate()
        assertFalse(gate.shouldTick(358.0, true, 0))
        assertFalse(gate.shouldTick(1.0, true, 20))
        assertTrue(gate.shouldTick(3.0, true, 40))
        assertTrue(gate.shouldTick(358.0, true, 120))
    }

    @Test fun rapidRotationIsRateLimitedWithoutDelayedTicks() {
        val gate = RotationTickGate()
        gate.shouldTick(0.0, true, 0)
        assertTrue(gate.shouldTick(5.0, true, 20))
        assertFalse(gate.shouldTick(10.0, true, 40))
        assertFalse(gate.shouldTick(10.0, true, 100))
        assertTrue(gate.shouldTick(15.0, true, 120))
    }

    @Test fun disabledOrResumedGuidanceStartsSilently() {
        val gate = RotationTickGate()
        gate.shouldTick(0.0, true, 0)
        assertFalse(gate.shouldTick(10.0, false, 20))
        assertFalse(gate.shouldTick(20.0, true, 40))
        assertTrue(gate.shouldTick(25.0, true, 60))
        gate.reset()
        assertFalse(gate.shouldTick(90.0, true, 80))
    }

    @Test fun staleReadingsAndCalibrationJumpsDoNotTick() {
        val gate = RotationTickGate()
        gate.shouldTick(0.0, true, 0)
        assertFalse(gate.shouldTick(10.0, true, 500))
        assertFalse(gate.shouldTick(100.0, true, 520))
        assertFalse(gate.shouldTick(null, true, 540))
        assertFalse(gate.shouldTick(110.0, true, 560))
    }
}
