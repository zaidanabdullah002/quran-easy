package com.zaidan.quraneasy.feature.prayer.application

import com.zaidan.quraneasy.feature.prayer.data.context.PrayerContextObserver
import com.zaidan.quraneasy.feature.prayer.data.location.LocationLabelResolver
import com.zaidan.quraneasy.feature.prayer.data.schedule.IslamicAppPrayerScheduleSource
import com.zaidan.quraneasy.feature.prayer.data.schedule.LocalPrayerScheduleSource
import com.zaidan.quraneasy.feature.prayer.domain.PrayerContext
import com.zaidan.quraneasy.feature.prayer.domain.PrayerContextChange
import com.zaidan.quraneasy.feature.prayer.domain.PrayerContextChangeDetector
import com.zaidan.quraneasy.feature.prayer.domain.PrayerDataResult
import com.zaidan.quraneasy.feature.prayer.domain.PrayerLocation
import com.zaidan.quraneasy.feature.prayer.domain.repository.PrayerRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrayerRefreshCoordinator @Inject constructor(
    private val contextObserver: PrayerContextObserver,
    private val localSource: LocalPrayerScheduleSource,
    private val remoteSource: IslamicAppPrayerScheduleSource,
    private val repository: PrayerRepository,
    private val labelResolver: LocationLabelResolver
) {
    private val _activeContext = MutableStateFlow<PrayerContext?>(null)
    val activeContext: StateFlow<PrayerContext?> = _activeContext.asStateFlow()

    private val _result = MutableStateFlow<PrayerDataResult>(PrayerDataResult.Unavailable)
    val result: StateFlow<PrayerDataResult> = _result.asStateFlow()

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    fun observeUpdates(): Flow<Unit> =
        stableContexts().mapLatest { context ->
            _activeContext.value = context
            val fallbackLabel = "Prayer times for ${context.location.key}"
            val cachedLabel =
                repository.getCachedLocationLabel(context.date, context.location.key) ?: fallbackLabel
            _result.value = PrayerDataResult.Cached

            supervisorScope {
                val labelTask = async {
                    labelResolver.resolve(
                        context.location.latitude,
                        context.location.longitude,
                        context.location.key
                    )
                }

                val scheduleTask = async {
                    val hadCache = repository.hasSchedule(context.date, context.location.key)
                    if (!hadCache) {
                        try {
                            repository.saveSchedule(
                                localSource.getSchedule(context),
                                cachedLabel
                            )
                        } catch (error: CancellationException) {
                            throw error
                        } catch (_: Exception) {
                            // Remote providers still get a chance if local calculation fails.
                        }
                    }

                    try {
                        val remoteSchedule = remoteSource.getSchedule(context)
                        repository.saveSchedule(
                            remoteSchedule,
                            cachedLabel
                        )
                        if (_activeContext.value?.key == context.key) {
                            _result.value = PrayerDataResult.Fresh
                        }
                    } catch (error: CancellationException) {
                        throw error
                    } catch (_: Exception) {
                        if (_activeContext.value?.key == context.key) {
                            _result.value =
                                if (repository.hasSchedule(context.date, context.location.key)) {
                                    PrayerDataResult.Cached
                                } else {
                                    PrayerDataResult.Unavailable
                                }
                        }
                    }
                }
                scheduleTask.await()
                val resolvedLabel = labelTask.await()
                repository.updateLocationLabel(
                    context.date,
                    context.location.key,
                    resolvedLabel
                )
            }
        }

    private fun stableContexts(): Flow<PrayerContext> = flow {
        var previous = _activeContext.value
        var firstEmission = true
        contextObserver.observe().collect { observed ->
            val detectedChange = PrayerContextChangeDetector.detect(previous, observed)
            val context = if (previous != null && detectedChange == PrayerContextChange.DATE) {
                observed.copy(location = previous!!.location)
            } else {
                observed
            }
            if (firstEmission || detectedChange != PrayerContextChange.NONE) {
                firstEmission = false
                previous = context
                emit(context)
            }
        }
    }

    fun restore(date: String, locationKey: String) {
        val coordinates = locationKey.split(",").mapNotNull(String::toDoubleOrNull)
        if (coordinates.size != 2) return
        _activeContext.value = PrayerContext(
            date = date,
            location = PrayerLocation(coordinates[0], coordinates[1])
        )
        _result.value = PrayerDataResult.Cached
    }
}
