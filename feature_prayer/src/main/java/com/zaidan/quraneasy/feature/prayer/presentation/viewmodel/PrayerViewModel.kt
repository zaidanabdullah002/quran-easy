package com.zaidan.quraneasy.feature.prayer.presentation.viewmodel

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaidan.quraneasy.feature.prayer.data.location.LocationObserver
import com.zaidan.quraneasy.feature.prayer.domain.PrayerDataResult
import com.zaidan.quraneasy.feature.prayer.domain.repository.PrayerRepository
import com.zaidan.quraneasy.feature.prayer.domain.usercase.ObservePrayerStateUseCase
import com.zaidan.quraneasy.feature.prayer.domain.usercase.RefreshPrayerTimingsUseCase
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume

@HiltViewModel
class PrayerViewModel @Inject constructor(
    private val locationObserver: LocationObserver,
    private val prayerRepository: PrayerRepository,
    private val observePrayerStateUseCase: ObservePrayerStateUseCase,
    private val togglePrayerStatusUseCase: TogglePrayerStatusUseCase,
    private val refreshPrayerTimingsUseCase: RefreshPrayerTimingsUseCase,
    private val application: Application
) : ViewModel() {

    companion object {
        private const val TAG = "PrayerViewModel"
    }

    private val permissionState = MutableStateFlow(false)
    private val cardModeState = MutableStateFlow(PrayerCardMode.NoPermission)
    private val locationLabelState = MutableStateFlow<String?>(null)
    private val bannerState = MutableStateFlow<String?>(null)
    private val activeDateState = MutableStateFlow(todayDate())
    private val activeLocationKeyState = MutableStateFlow<String?>(null)
    private var locationObservationJob: Job? = null
    private var lastAcceptedLocation: Location? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiStateFlow: StateFlow<PrayerCardUiState> = activeDateState
        .combine(activeLocationKeyState) { date, locationKey ->
            date to locationKey
        }
        .flatMapLatest { (date, locationKey) ->
            if (locationKey == null) {
                flowOf(emptyList())
            } else {
                observePrayerStateUseCase(date, locationKey)
                    .map { state ->
                        state.prayers.map { prayer ->
                            PrayerRowUi(
                                name = prayer.name,
                                time = prayer.time,
                                completed = prayer.completed,
                                enabled = prayer.name != "Sunrise",
                                trackable = prayer.name != "Sunrise"
                            )
                        }
                    }
            }
        }
        .combine(permissionState) { prayers, hasPermission ->
            prayers to hasPermission
        }
        .combine(cardModeState) { (prayers, hasPermission), cardMode ->
            PrayerUiInputs(
                prayers = prayers,
                hasPermission = hasPermission,
                cardMode = cardMode,
                locationLabel = null
            )
        }
        .combine(locationLabelState) { inputs, locationLabel ->
            inputs.copy(locationLabel = locationLabel)
        }
        .combine(bannerState) { inputs, banner ->
            val prayers = inputs.prayers
            val hasPermission = inputs.hasPermission
            val cardMode = inputs.cardMode
            val locationLabel = inputs.locationLabel
            val effectiveMode = if (!hasPermission) {
                PrayerCardMode.NoPermission
            } else if (cardMode == PrayerCardMode.NoPermission && prayers.isNotEmpty()) {
                PrayerCardMode.Ready
            } else if (cardMode == PrayerCardMode.Loading && prayers.isNotEmpty()) {
                PrayerCardMode.Ready
            } else if (cardMode == PrayerCardMode.NoPermission) {
                if (prayers.isEmpty()) PrayerCardMode.Loading else PrayerCardMode.Ready
            } else {
                cardMode
            }

            val visiblePrayers = when (effectiveMode) {
                PrayerCardMode.Ready,
                PrayerCardMode.ReadyOfflineCache -> prayers.ifEmpty { PrayerRowUi.placeholderList() }
                PrayerCardMode.NoPermission,
                PrayerCardMode.Loading,
                PrayerCardMode.Unavailable -> PrayerRowUi.placeholderList()
            }

            PrayerCardUiState(
                cardMode = effectiveMode,
                prayers = visiblePrayers.map { row ->
                    row.copy(
                        enabled = row.trackable &&
                            (effectiveMode == PrayerCardMode.Ready || effectiveMode == PrayerCardMode.ReadyOfflineCache)
                    )
                },
                locationLabel = locationLabel,
                banner = bannerForState(
                    cardMode = effectiveMode,
                    explicitBanner = banner
                ),
                actionLabel = actionLabelForState(effectiveMode)
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PrayerCardUiState()
        )

    init {
        Log.i(TAG, "init(): PrayerViewModel created")
        updatePermissionStatus()
        restoreCachedLocationKey()
    }

    fun updatePermissionStatus() {
        val isCoarseGranted = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val isFineGranted = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val isGranted = isCoarseGranted || isFineGranted

        permissionState.value = isGranted
        Log.i(
            TAG,
            "updatePermissionStatus(): coarse=$isCoarseGranted fine=$isFineGranted granted=$isGranted currentCardMode=${cardModeState.value}"
        )
        if (!isGranted) {
            cardModeState.value = PrayerCardMode.NoPermission
            bannerState.value = "Enable location to load prayer times"
            locationLabelState.value = null
            Log.d(TAG, "updatePermissionStatus(): moved to NoPermission state")
        } else if (activeLocationKeyState.value != null) {
            cardModeState.value = PrayerCardMode.Ready
            bannerState.value = null
            Log.d(TAG, "updatePermissionStatus(): permission granted with cached location key")
        }
    }

    fun startObservingLocation() {
        Log.i(
            TAG,
            "startObservingLocation(): currentJobActive=${locationObservationJob?.isActive == true} activeLocationKey=${activeLocationKeyState.value}"
        )
        updatePermissionStatus()
        if (!permissionState.value) {
            Log.i(TAG, "startObservingLocation(): permission missing, not starting observer")
            return
        }
        if (locationObservationJob != null) {
            Log.d(TAG, "startObservingLocation(): observer already running, skipping")
            return
        }
        if (activeLocationKeyState.value == null) {
            cardModeState.value = PrayerCardMode.Loading
            bannerState.value = null
            Log.d(TAG, "startObservingLocation(): no active location yet, showing loading")
        }

        locationObservationJob = viewModelScope.launch {
            Log.i(TAG, "startObservingLocation(): location collection started")
            locationObserver.observeLocationUpdates().collect { location ->
                Log.i(
                    TAG,
                    "startObservingLocation(): location update lat=${location.latitude}, long=${location.longitude}"
                )
                refreshForLocationIfNeeded(location)
            }
        }
    }

    fun stopObservingLocation() {
        Log.i(
            TAG,
            "stopObservingLocation(): cancelling observer active=${locationObservationJob?.isActive == true}"
        )
        locationObservationJob?.cancel()
        locationObservationJob = null
    }

    fun togglePrayer(index: Int) {
        Log.i(TAG, "togglePrayer(): index=$index")
        viewModelScope.launch {
            val uiState = uiStateFlow.value
            Log.d(TAG, "togglePrayer(): currentUiState=$uiState")
            if (uiState.cardMode != PrayerCardMode.Ready &&
                uiState.cardMode != PrayerCardMode.ReadyOfflineCache
            ) {
                Log.i(TAG, "togglePrayer(): ignored because cardMode=${uiState.cardMode}")
                return@launch
            }

            val prayer = uiState.prayers.getOrNull(index) ?: return@launch
            Log.i(TAG, "togglePrayer(): toggling prayer=${prayer.name} date=${activeDateState.value}")
            togglePrayerStatusUseCase(date = activeDateState.value, prayerName = prayer.name)
        }
    }

    private suspend fun refreshForLocationIfNeeded(location: Location) {
        val date = todayDate()
        val locationKey = buildLocationKey(location.latitude, location.longitude)
        val shouldRefresh = shouldRefreshFor(date, location, locationKey)
        Log.i(
            TAG,
            "refreshForLocationIfNeeded(): date=$date locationKey=$locationKey shouldRefresh=$shouldRefresh lastAccepted=$lastAcceptedLocation"
        )

        if (!shouldRefresh) {
            Log.d(TAG, "refreshForLocationIfNeeded(): skipping refresh")
            return
        }

        lastAcceptedLocation = location
        activeDateState.value = date
        activeLocationKeyState.value = locationKey
        locationLabelState.value = resolveLocationLabel(location)
        cardModeState.value = PrayerCardMode.Loading
        bannerState.value = null
        Log.d(
            TAG,
            "refreshForLocationIfNeeded(): updated active states date=$date locationKey=$locationKey locationLabel=${locationLabelState.value}"
        )

        when (
            refreshPrayerTimingsUseCase(
                date = date,
                latitude = location.latitude,
                longitude = location.longitude,
                locationKey = locationKey,
                locationLabel = locationLabelState.value ?: "Prayer times for $locationKey"
            )
        ) {
            PrayerDataResult.Fresh -> {
                cardModeState.value = PrayerCardMode.Ready
                bannerState.value = null
                Log.i(TAG, "refreshForLocationIfNeeded(): loaded fresh timings")
            }
            PrayerDataResult.Cached -> {
                cardModeState.value = PrayerCardMode.ReadyOfflineCache
                bannerState.value = "Offline. Showing saved timings."
                Log.i(TAG, "refreshForLocationIfNeeded(): using cached timings")
            }
            PrayerDataResult.Unavailable -> {
                cardModeState.value = PrayerCardMode.Unavailable
                bannerState.value = "Couldn't load prayer times"
                Log.i(TAG, "refreshForLocationIfNeeded(): no usable timings available")
            }
        }
        Log.d(TAG, "refreshForLocationIfNeeded(): final cardMode=${cardModeState.value} banner=${bannerState.value}")
    }

    private fun shouldRefreshFor(
        date: String,
        location: Location,
        locationKey: String
    ): Boolean {
        if (activeLocationKeyState.value == null) {
            Log.d(TAG, "shouldRefreshFor(): true because no active location key")
            return true
        }
        if (activeDateState.value != date) {
            Log.d(TAG, "shouldRefreshFor(): true because date changed from ${activeDateState.value} to $date")
            return true
        }
        if (activeLocationKeyState.value != locationKey &&
            hasSignificantLocationChange(lastAcceptedLocation, location)
        ) {
            Log.d(
                TAG,
                "shouldRefreshFor(): true because location changed from ${activeLocationKeyState.value} to $locationKey"
            )
            return true
        }
        val needsRetry = cardModeState.value == PrayerCardMode.ReadyOfflineCache ||
            cardModeState.value == PrayerCardMode.Unavailable
        Log.d(TAG, "shouldRefreshFor(): fallback retry=$needsRetry currentCardMode=${cardModeState.value}")
        return needsRetry
    }

    private fun hasSignificantLocationChange(
        previous: Location?,
        current: Location,
        thresholdMeters: Float = 10_000f
    ): Boolean {
        if (previous == null) {
            Log.d(TAG, "hasSignificantLocationChange(): true because previous is null")
            return true
        }
        val distance = previous.distanceTo(current)
        val changed = distance >= thresholdMeters
        Log.d(
            TAG,
            "hasSignificantLocationChange(): distance=$distance threshold=$thresholdMeters changed=$changed"
        )
        return changed
    }

    private fun todayDate(): String {
        return SimpleDateFormat("dd-MM-yyyy", Locale.US).format(Date())
    }

    private fun buildLocationKey(latitude: Double, longitude: Double): String {
        return String.format(Locale.US, "%.3f,%.3f", latitude, longitude)
    }

    private suspend fun resolveLocationLabel(location: Location): String {
        val fallback = "Prayer times for ${buildLocationKey(location.latitude, location.longitude)}"
        val geocoder = Geocoder(application, Locale.getDefault())
        Log.d(TAG, "resolveLocationLabel(): fallback=$fallback geocoderPresent=${Geocoder.isPresent()}")
        if (!Geocoder.isPresent()) return fallback

        val address = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            suspendCancellableCoroutine<Address?> { continuation ->
                geocoder.getFromLocation(location.latitude, location.longitude, 1) { addresses ->
                    continuation.resume(addresses.firstOrNull())
                }
            }
        } else {
            @Suppress("DEPRECATION")
            withContext(kotlinx.coroutines.Dispatchers.IO) {
                geocoder.getFromLocation(location.latitude, location.longitude, 1)?.firstOrNull()
            }
        }

        val locality = address?.locality
        val adminArea = address?.adminArea
        val country = address?.countryName

        val resolved = listOfNotNull(locality, adminArea ?: country)
            .distinct()
            .take(2)
            .joinToString(", ")
            .ifBlank { fallback }
            .let { "Prayer times for $it" }
        Log.d(
            TAG,
            "resolveLocationLabel(): locality=$locality adminArea=$adminArea country=$country resolved=$resolved"
        )
        return resolved
    }

    private fun restoreCachedLocationKey() {
        viewModelScope.launch {
            val date = activeDateState.value
            val cachedLocationKey = prayerRepository.getCachedLocationKey(date)
            Log.i(TAG, "restoreCachedLocationKey(): date=$date cachedLocationKey=$cachedLocationKey")
            if (cachedLocationKey != null) {
                activeLocationKeyState.value = cachedLocationKey
                locationLabelState.value = prayerRepository.getCachedLocationLabel(date, cachedLocationKey)
                    ?: "Prayer times for $cachedLocationKey"
                if (permissionState.value) {
                    cardModeState.value = PrayerCardMode.Ready
                    bannerState.value = null
                }
            }
        }
    }

    private fun bannerForState(
        cardMode: PrayerCardMode,
        explicitBanner: String?
    ): String? {
        return explicitBanner ?: when (cardMode) {
            PrayerCardMode.NoPermission -> "Enable location to load prayer times"
            PrayerCardMode.ReadyOfflineCache -> "Offline. Showing saved timings."
            PrayerCardMode.Loading,
            PrayerCardMode.Ready,
            PrayerCardMode.Unavailable -> null
        }
    }

    private fun actionLabelForState(cardMode: PrayerCardMode): String? {
        return when (cardMode) {
            PrayerCardMode.NoPermission -> "Enable"
            PrayerCardMode.Unavailable -> "Retry"
            PrayerCardMode.Loading,
            PrayerCardMode.Ready,
            PrayerCardMode.ReadyOfflineCache -> null
        }
    }
}

private data class PrayerUiInputs(
    val prayers: List<PrayerRowUi>,
    val hasPermission: Boolean,
    val cardMode: PrayerCardMode,
    val locationLabel: String?
)
