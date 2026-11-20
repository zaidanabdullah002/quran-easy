package com.zaidan.quraneasy.feature.manzil.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaidan.quraneasy.feature.manzil.domain.ManzilPassage
import com.zaidan.quraneasy.feature.manzil.domain.manzilPassages
import com.zaidan.quraneasy.feature.quran.data.translation.AyahTranslationRepository
import com.zaidan.quraneasy.feature.quran.domain.repository.QuranRepository
import com.zaidan.quraneasy.feature.quran.presentation.audio.AyahAudioItem
import com.zaidan.quraneasy.feature.quran.presentation.audio.QuranAyahAudioPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ManzilAyahUi(
    val numberInSurah: Int,
    val arabic: String,
    val english: String,
)

data class ManzilPassageUi(
    val title: String,
    val surahNumber: Int,
    val surahName: String,
    val startAyah: Int,
    val endAyah: Int,
    val ayahs: List<ManzilAyahUi>,
)

data class ManzilUiState(
    val isLoading: Boolean = true,
    val passage: ManzilPassageUi? = null,
    val passages: List<ManzilPassageUi> = emptyList(),
    val message: String = "",
)

@HiltViewModel
class ManzilViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val quranRepository: QuranRepository,
    private val translationRepository: AyahTranslationRepository,
    private val audioPlayer: QuranAyahAudioPlayer,
) : ViewModel() {

    private val passageIndex: Int = savedStateHandle.get<Int>("index") ?: ALL_PASSAGES_INDEX
    private val _uiState = MutableStateFlow(ManzilUiState())
    val uiState: StateFlow<ManzilUiState> = _uiState.asStateFlow()
    private var loadJob: Job? = null
    val audioState = audioPlayer.state

    fun prepareAudio() {
        val state = _uiState.value
        val passages = state.passages.ifEmpty { listOfNotNull(state.passage) }
        audioPlayer.setPlaylist(passages.toAudioPlaylist())
    }

    fun toggleAudio() { prepareAudio(); audioPlayer.togglePlayback() }
    fun previousAyah() = audioPlayer.previous()
    fun nextAyah() = audioPlayer.next()
    fun stopAudio() = audioPlayer.stop()


    init {
        load()
    }

    fun load() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = ManzilUiState(isLoading = true)
            try {
                _uiState.value = ManzilUiState(
                    isLoading = false,
                    passage = if (passageIndex == ALL_PASSAGES_INDEX) null else loadPassage(passageIndex),
                    passages = if (passageIndex == ALL_PASSAGES_INDEX) loadAllPassages() else emptyList(),
                )
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _uiState.value = ManzilUiState(
                    isLoading = false,
                    message = error.toManzilMessage(),
                )
            }
        }
    }

    private suspend fun loadPassage(index: Int): ManzilPassageUi {
        val passage = manzilPassages.getOrNull(index) ?: error("Unknown Manzil passage")
        val ayahNumbers = (passage.startAyah..passage.endAyah).toSet()
        retrying { quranRepository.ensureSurahListDownloaded() }
        retrying { quranRepository.ensureSurahDownloaded(passage.surahNumber) }
        val arabic = quranRepository.observeSurah(passage.surahNumber)
            .first()
            .associate { it.numberInSurah to it.arabicText }
        val english = retrying {
            translationRepository.getSurahTranslations(passage.surahNumber, ayahNumbers)
        }
        val ayahs = (passage.startAyah..passage.endAyah).map { number ->
            val reference = "${passage.surahNumber}:$number"
            ManzilAyahUi(
                numberInSurah = number,
                arabic = arabic[number] ?: error("No Arabic for $reference"),
                english = english[number] ?: error("No English for $reference"),
            )
        }
        return passage.toUi(ayahs)
    }

    private suspend fun loadAllPassages(): List<ManzilPassageUi> {
        retrying { quranRepository.ensureSurahListDownloaded() }
        return manzilPassages
            .groupBy { it.surahNumber }
            .flatMap { (surahNumber, passages) ->
                retrying { quranRepository.ensureSurahDownloaded(surahNumber) }
                val arabic = quranRepository.observeSurah(surahNumber)
                    .first()
                    .associate { it.numberInSurah to it.arabicText }
                val requestedAyahs = passages
                    .flatMap { it.startAyah..it.endAyah }
                    .toSet()
                val english = retrying {
                    translationRepository.getSurahTranslations(surahNumber, requestedAyahs)
                }
                passages.map { passage ->
                    val ayahs = (passage.startAyah..passage.endAyah).map { number ->
                        val reference = "$surahNumber:$number"
                        ManzilAyahUi(
                            numberInSurah = number,
                            arabic = arabic[number] ?: error("No Arabic for $reference"),
                            english = english[number] ?: error("No English for $reference"),
                        )
                    }
                    passage.toUi(ayahs)
                }
            }
    }

    private fun ManzilPassage.toUi(ayahs: List<ManzilAyahUi>) = ManzilPassageUi(
        title = title,
        surahNumber = surahNumber,
        surahName = surahName,
        startAyah = startAyah,
        endAyah = endAyah,
        ayahs = ayahs,
    )

    private suspend fun <T> retrying(block: suspend () -> T): T {
        var waitMillis = 1_000L
        var lastError: Exception? = null
        repeat(RETRY_COUNT) { attempt ->
            try {
                return block()
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                lastError = error
                val rateLimited = error.message?.contains("429") == true
                if (!rateLimited || attempt == RETRY_COUNT - 1) throw error
                delay(waitMillis)
                waitMillis *= 2
            }
        }
        throw lastError ?: IllegalStateException("Could not load Manzil")
    }

    private fun Exception.toManzilMessage(): String {
        return if (message?.contains("429") == true) {
            "The Quran service is busy. Wait a moment and try again."
        } else {
            message ?: "Could not load Manzil"
        }
    }

    private companion object {
        const val ALL_PASSAGES_INDEX = -1
        const val RETRY_COUNT = 4
    }
}

/** Flatten actual loaded passages, preserving gaps between the selected Quran ranges. */
internal fun List<ManzilPassageUi>.toAudioPlaylist(): List<AyahAudioItem> = flatMap { passage ->
    passage.ayahs.map { AyahAudioItem(passage.surahNumber, it.numberInSurah) }
}
