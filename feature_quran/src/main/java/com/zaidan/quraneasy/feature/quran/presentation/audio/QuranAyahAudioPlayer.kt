package com.zaidan.quraneasy.feature.quran.presentation.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

data class ReaderAudioUiState(
    val currentIndex: Int = 0,
    val isPlaying: Boolean = false,
    val isPreparing: Boolean = false,
    val errorMessage: String? = null
)

@Singleton
class QuranAyahAudioPlayer @Inject constructor(
    @ApplicationContext context: Context
) {
    private val audioManager = context.getSystemService(AudioManager::class.java)
    private val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
        .build()
    private val _state = MutableStateFlow(ReaderAudioUiState())
    val state: StateFlow<ReaderAudioUiState> = _state.asStateFlow()

    private var playlist: List<AyahAudioItem> = emptyList()
    private var source: AyahAudioSource = AlafasyAudioSource
    private var mediaPlayer: MediaPlayer? = null
    private var prepared = false
    private var playWhenReady = false

    private val focusListener = AudioManager.OnAudioFocusChangeListener { change ->
        when (change) {
            AudioManager.AUDIOFOCUS_GAIN -> mediaPlayer?.setVolume(1f, 1f)
            AudioManager.AUDIOFOCUS_LOSS,
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> pause()
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> mediaPlayer?.setVolume(0.2f, 0.2f)
        }
    }
    private val focusRequest: AudioFocusRequest? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(audioAttributes)
            .setOnAudioFocusChangeListener(focusListener)
            .build()
    } else {
        null
    }

    fun setPlaylist(ayahs: List<AyahAudioItem>) {
        if (playlist == ayahs) return
        stop()
        playlist = ayahs.toList()
        _state.value = ReaderAudioUiState()
    }

    fun setSource(newSource: AyahAudioSource) {
        if (source === newSource) return
        stop()
        source = newSource
        _state.value = ReaderAudioUiState()
    }

    fun togglePlayback() {
        if (playlist.isEmpty()) return
        val player = mediaPlayer
        when {
            _state.value.isPreparing -> {
                playWhenReady = !playWhenReady
                if (!playWhenReady) {
                    releasePlayer()
                    _state.value = _state.value.copy(isPreparing = false)
                }
            }
            player != null && prepared && player.runCatching { isPlaying }.getOrDefault(false) -> pause()
            player != null && prepared -> startPreparedPlayer()
            else -> prepareCurrentAyah(autoPlay = true)
        }
    }

    fun previous() {
        selectIndex((_state.value.currentIndex - 1).coerceAtLeast(0))
    }

    fun next() {
        selectIndex((_state.value.currentIndex + 1).coerceAtMost(playlist.lastIndex))
    }

    fun stop() {
        playWhenReady = false
        releasePlayer()
        abandonAudioFocus()
        _state.value = _state.value.copy(isPlaying = false, isPreparing = false, errorMessage = null)
    }

    fun release() {
        stop()
        playlist = emptyList()
    }

    fun playIndex(index: Int) {
        if (playlist.isEmpty() || index !in playlist.indices) return
        playWhenReady = true
        releasePlayer()
        _state.value = _state.value.copy(
            currentIndex = index,
            isPlaying = false,
            isPreparing = false,
            errorMessage = null
        )
        prepareCurrentAyah(autoPlay = true)
    }

    private fun selectIndex(index: Int) {
        if (playlist.isEmpty() || index == _state.value.currentIndex) return
        val shouldContinue = _state.value.isPlaying || _state.value.isPreparing
        playWhenReady = shouldContinue
        releasePlayer()
        _state.value = _state.value.copy(
            currentIndex = index,
            isPlaying = false,
            isPreparing = false,
            errorMessage = null
        )
        if (shouldContinue) prepareCurrentAyah(autoPlay = true)
    }

    private fun prepareCurrentAyah(autoPlay: Boolean) {
        val ayah = playlist.getOrNull(_state.value.currentIndex) ?: return
        releasePlayer()
        prepared = false
        playWhenReady = autoPlay
        _state.value = _state.value.copy(
            isPlaying = false,
            isPreparing = true,
            errorMessage = null
        )

        try {
            val player = MediaPlayer()
            mediaPlayer = player
            player.setAudioAttributes(audioAttributes)
            player.setDataSource(source.urlFor(ayah))
            player.setOnPreparedListener { readyPlayer ->
                if (mediaPlayer !== readyPlayer) return@setOnPreparedListener
                prepared = true
                _state.value = _state.value.copy(isPreparing = false)
                if (playWhenReady) startPreparedPlayer()
            }
            player.setOnCompletionListener { completedPlayer ->
                if (mediaPlayer !== completedPlayer) return@setOnCompletionListener
                if (_state.value.currentIndex < playlist.lastIndex) {
                    selectIndex(_state.value.currentIndex + 1)
                } else {
                    playWhenReady = false
                    _state.value = _state.value.copy(isPlaying = false)
                    abandonAudioFocus()
                }
            }
            player.setOnErrorListener { failedPlayer, _, _ ->
                if (mediaPlayer === failedPlayer) failPlayback("Unable to load recitation. Check your connection.")
                true
            }
            player.prepareAsync()
        } catch (_: Exception) {
            failPlayback("Unable to load recitation. Check your connection.")
        }
    }

    private fun startPreparedPlayer() {
        val player = mediaPlayer ?: return
        if (!requestAudioFocus()) {
            playWhenReady = false
            _state.value = _state.value.copy(isPlaying = false, errorMessage = "Audio focus is unavailable.")
            return
        }
        playWhenReady = true
        try {
            player.setVolume(1f, 1f)
            player.start()
            _state.value = _state.value.copy(isPlaying = true, errorMessage = null)
        } catch (_: IllegalStateException) {
            failPlayback("Unable to play recitation. Please try again.")
        }
    }

    private fun pause() {
        playWhenReady = false
        if (prepared) mediaPlayer?.runCatching { if (isPlaying) pause() }
        else releasePlayer()
        abandonAudioFocus()
        _state.value = _state.value.copy(isPlaying = false, isPreparing = false)
    }

    private fun requestAudioFocus(): Boolean {
        val result = runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                audioManager.requestAudioFocus(requireNotNull(focusRequest))
            } else {
                @Suppress("DEPRECATION")
                audioManager.requestAudioFocus(
                    focusListener,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN
                )
            }
        }.getOrDefault(AudioManager.AUDIOFOCUS_REQUEST_FAILED)
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    private fun abandonAudioFocus() {
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                focusRequest?.let(audioManager::abandonAudioFocusRequest)
            } else {
                @Suppress("DEPRECATION")
                audioManager.abandonAudioFocus(focusListener)
            }
        }
    }

    private fun releasePlayer() {
        prepared = false
        val player = mediaPlayer
        mediaPlayer = null
        player?.runCatching { reset() }
        player?.runCatching { release() }
    }

    private fun failPlayback(message: String) {
        playWhenReady = false
        releasePlayer()
        abandonAudioFocus()
        _state.value = _state.value.copy(
            isPlaying = false,
            isPreparing = false,
            errorMessage = message
        )
    }
}

internal fun alafasyAyahAudioUrl(surahNumber: Int, ayahNumber: Int): String {
    require(surahNumber in 1..114) { "Surah number must be between 1 and 114." }
    require(ayahNumber > 0) { "Ayah number must be positive." }
    val fileName = String.format(Locale.US, "%03d%03d.mp3", surahNumber, ayahNumber)
    return "https://everyayah.com/data/Alafasy_128kbps/$fileName"
}
