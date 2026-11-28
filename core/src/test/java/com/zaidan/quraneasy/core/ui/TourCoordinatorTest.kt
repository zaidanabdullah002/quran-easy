package com.zaidan.quraneasy.core.ui

import com.zaidan.quraneasy.core.settings.TourStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TourCoordinatorTest {
    @Test
    fun completeTourSequenceAdvancesThroughTasbihAndQibla() {
        val coordinator = TourCoordinator()

        coordinator.startTour()
        assertEquals(TourStep.READ_QURAN_HOME, coordinator.currentStep)

        coordinator.nextStep()
        assertEquals(TourStep.SURAH_LIST, coordinator.currentStep)

        coordinator.nextStep()
        assertEquals(TourStep.BACK_TO_HOME, coordinator.currentStep)

        coordinator.nextStep()
        assertEquals(TourStep.DIGITAL_TASBIH, coordinator.currentStep)

        coordinator.nextStep()
        assertEquals(TourStep.QIBLA_FINDER, coordinator.currentStep)

        coordinator.nextStep()
        assertEquals(TourStep.SETTINGS_TAB, coordinator.currentStep)

        coordinator.nextStep()
        assertEquals(TourStatus.TOUR_COMPLETED, coordinator.tourStatus)
        assertNull(coordinator.currentStep)
    }
}
