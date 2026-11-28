package com.zaidan.quraneasy.qibla

import kotlin.math.abs

/** One tactile tick per 5 degrees, capped at 12.5 ticks/second. No queued pulses. */
internal class RotationTickGate {
    private var anchor: Double? = null
    private var previous: Double? = null
    private var sampleTime = 0L
    private var tickTime: Long? = null

    fun reset() {
        anchor = null
        previous = null
        tickTime = null
    }

    fun shouldTick(heading: Double?, enabled: Boolean, nowMs: Long): Boolean {
        if (!enabled || heading == null || !heading.isFinite()) {
            reset()
            return false
        }
        val current = QiblaMath.normalize(heading)
        val last = previous
        val restart = last == null || nowMs - sampleTime > 250L ||
            abs(QiblaMath.turn(last, current)) > 45.0
        previous = current
        sampleTime = nowMs
        if (restart) {
            anchor = current
            tickTime = null
            return false
        }
        // Measure net displacement so back-and-forth sensor noise does not accumulate.
        if (abs(QiblaMath.turn(anchor ?: current, current)) < 5.0) return false
        anchor = current
        val lastTick = tickTime
        if (lastTick != null && nowMs - lastTick < 80L) return false
        tickTime = nowMs
        return true
    }
}
