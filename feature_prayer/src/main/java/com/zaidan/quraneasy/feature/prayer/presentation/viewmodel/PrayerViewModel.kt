package com.zaidan.quraneasy.feature.prayer.presentation.viewmodel

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaidan.quraneasy.feature.prayer.application.PrayerRefreshCoordinator
import com.zaidan.quraneasy.feature.prayer.domain.PrayerDataResult
import com.zaidan.quraneasy.feature.prayer.domain.PrayerState
import com.zaidan.quraneasy.feature.prayer.domain.repository.PrayerRepository
import com.zaidan.quraneasy.feature.prayer.domain.usercase.ObservePrayerStateUseCase
import com.zaidan.quraneasy.feature.prayer.domain.usercase.TogglePrayerStatusUseCase
import com.zaidan.quraneasy.feature.prayer.presentation.PrayerCardMode
import com.zaidan.quraneasy.feature.prayer.presentation.PrayerCardUiState
import com.zaidan.quraneasy.feature.prayer.presentation.PrayerRowUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class PrayerViewModel @Inject constructor(
    private val coordinator: PrayerRefreshCoordinator,
    private val prayerRepository: PrayerRepository,
    private val observePrayerStateUseCase: ObservePrayerStateUseCase,
    private val togglePrayerStatusUseCase: TogglePrayerStatusUseCase,
    private val application: Application
) : ViewModel() {
    private val permissionState = MutableStateFlow(false)
    private var observationJob: Job? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    private val prayerState = coordinator.activeContext.flatMapLatest { context ->
        if (context == null) {
            flowOf(PrayerState())
        } else {
            observePrayerStateUseCase(context.date, context.location.key)
        }
    }

    val uiStateFlow: StateFlow<PrayerCardUiState> = combine(
        prayerState,
        permissionState,
        coordinator.result
    ) { state, hasPermission, result ->
        val prayers = state.prayers.map { prayer ->
            PrayerRowUi(
                name = prayer.name,
                time = prayer.time,
                completed = prayer.completed,
                enabled = prayer.name != "Sunrise",
                trackable = prayer.name != "Sunrise"
            )
        }
        val mode = when {
            !hasPermission -> PrayerCardMode.NoPermission
            prayers.isEmpty() && result == PrayerDataResult.Unavailable -> PrayerCardMode.Unavailable
            prayers.isEmpty() -> PrayerCardMode.Loading
            result == PrayerDataResult.Cached -> PrayerCardMode.ReadyOfflineCache
            else -> PrayerCardMode.Ready
        }
        val visiblePrayers = if (
            mode == PrayerCardMode.Ready || mode == PrayerCardMode.ReadyOfflineCache
        ) prayers else PrayerRowUi.placeholderList()

        PrayerCardUiState(
            cardMode = mode,
            prayers = visiblePrayers.map { row ->
                row.copy(
                    enabled = row.trackable &&
                        (mode == PrayerCardMode.Ready || mode == PrayerCardMode.ReadyOfflineCache)
                )
            },
            locationLabel = state.locationLabel,
            banner = when (mode) {
                PrayerCardMode.NoPermission -> "Enable location to load prayer times"
                PrayerCardMode.ReadyOfflineCache -> "Showing saved or locally calculated timings."
                PrayerCardMode.Unavailable -> "Couldn't load prayer times"
                else -> null
            },
            actionLabel = when (mode) {
                PrayerCardMode.NoPermission -> "Enable"
                PrayerCardMode.Unavailable -> "Retry"
                else -> null
            }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PrayerCardUiState()
    )

    init {
        updatePermissionStatus()
        restoreCachedContext()
    }

    fun updatePermissionStatus() {
        permissionState.value =
            ContextCompat.checkSelfPermission(application, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(application, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
    }

    fun startObservingLocation() {
        updatePermissionStatus()
        if (!permissionState.value || observationJob?.isActive == true) return
        observationJob = viewModelScope.launch {
            coordinator.observeUpdates().collect {}
        }
    }

    fun stopObservingLocation() {
        observationJob?.cancel()
        observationJob = null
    }

    fun togglePrayer(index: Int) {
        viewModelScope.launch {
            val prayer = uiStateFlow.value.prayers.getOrNull(index) ?: return@launch
            val context = coordinator.activeContext.value ?: return@launch
            if (!prayer.enabled || !prayer.trackable || prayer.time == "--:--") return@launch
            togglePrayerStatusUseCase(context.date, prayer.name)
        }
    }

    private fun restoreCachedContext() {
        viewModelScope.launch {
            val date = SimpleDateFormat("dd-MM-yyyy", Locale.US).format(Date())
            val key = prayerRepository.getCachedLocationKey(date) ?: return@launch
            coordinator.restore(date, key)
        }
    }
}
