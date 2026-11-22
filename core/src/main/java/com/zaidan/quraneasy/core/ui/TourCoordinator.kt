package com.zaidan.quraneasy.core.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import com.zaidan.quraneasy.core.settings.TourStatus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TourCoordinator @Inject constructor() {
    var currentStep by mutableStateOf<TourStep?>(null)
        private set

    var tourStatus by mutableStateOf(TourStatus.NOT_STARTED)
        private set

    private val targets = mutableStateMapOf<String, Rect>()

    fun registerTarget(id: String, coordinates: LayoutCoordinates) {
        targets[id] = coordinates.boundsInWindow()
    }

    fun unregisterTarget(id: String) {
        targets.remove(id)
    }

    fun getTargetBounds(id: String): Rect? = targets[id]

    fun startTour() {
        tourStatus = TourStatus.TOUR_ACTIVE
        currentStep = TourStep.READ_QURAN_HOME
    }

    fun nextStep() {
        currentStep = when (currentStep) {
            TourStep.READ_QURAN_HOME -> TourStep.SURAH_LIST
            TourStep.SURAH_LIST -> TourStep.BACK_TO_HOME
            TourStep.BACK_TO_HOME -> TourStep.DIGITAL_TASBIH
            TourStep.DIGITAL_TASBIH -> TourStep.SETTINGS_TAB
            TourStep.SETTINGS_TAB -> {
                completeTour()
                null
            }
            null -> null
        }
    }

    fun completeTour() {
        tourStatus = TourStatus.TOUR_COMPLETED
        currentStep = null
    }

    fun exitTour() {
        tourStatus = TourStatus.TOUR_EXITED
        currentStep = null
    }

    fun initialize(status: TourStatus) {
        tourStatus = status
        if (status == TourStatus.TOUR_ACTIVE && currentStep == null) {
            currentStep = TourStep.READ_QURAN_HOME
        }
    }
}

enum class TourStep(
    val targetId: String,
    val text: String,
    val expectedRoute: String? = null
) {
    READ_QURAN_HOME("read_quran_card", "Start your journey by exploring the Holy Quran."),
    SURAH_LIST("surah_card_1", "Browse through Surahs, Juz, and your Bookmarks."),
    BACK_TO_HOME("back_button", "Tap here to return to the main dashboard."),
    DIGITAL_TASBIH("tasbih_nav", "Keep track of your daily Dhikr with the Digital Tasbih."),
    SETTINGS_TAB("settings_nav", "Personalize your experience in the Settings menu.")
}
