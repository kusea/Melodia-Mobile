package com.melodia.app.data.repository

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.melodia.app.domain.model.SleepTimerConfig
import com.melodia.app.domain.model.Song
import com.melodia.app.domain.repository.AuthRepository
import com.melodia.app.domain.repository.PlayerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class PlayerRepositoryImpl(
    private val context: Context,
    private val authRepository: AuthRepository,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) : PlayerRepository {

    private val player: ExoPlayer = ExoPlayer.Builder(context).build()

    private val _currentSong = MutableStateFlow<Song?>(null)
    override val currentSong: StateFlow<Song?> = _currentSong.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    override val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0L)
    override val currentPositionMs: StateFlow<Long> = _currentPositionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(0L)
    override val durationMs: StateFlow<Long> = _durationMs.asStateFlow()

    private val _isShuffleEnabled = MutableStateFlow(false)
    override val isShuffleEnabled: StateFlow<Boolean> = _isShuffleEnabled.asStateFlow()

    private val _isRepeatEnabled = MutableStateFlow(false)
    override val isRepeatEnabled: StateFlow<Boolean> = _isRepeatEnabled.asStateFlow()

    private val _currentQueue = MutableStateFlow<List<Song>>(emptyList())
    override val currentQueue: StateFlow<List<Song>> = _currentQueue.asStateFlow()

    private val _sleepTimerConfig = MutableStateFlow(SleepTimerConfig())
    override val sleepTimerConfig: StateFlow<SleepTimerConfig> = _sleepTimerConfig.asStateFlow()

    private var progressTrackingJob: Job? = null
    private var sleepTimerJob: Job? = null

    init {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                _isPlaying.value = playing
                if (playing) {
                    startProgressTracking()
                } else {
                    stopProgressTracking()
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    _durationMs.value = player.duration.coerceAtLeast(0L)
                } else if (playbackState == Player.STATE_ENDED) {
                    if (_isRepeatEnabled.value) {
                        player.seekTo(0)
                        player.play()
                    } else {
                        skipToNext()
                    }
                }
            }
        })
    }

    private fun startProgressTracking() {
        progressTrackingJob?.cancel()
        progressTrackingJob = scope.launch {
            while (isActive && player.isPlaying) {
                val pos = player.currentPosition
                _currentPositionMs.value = pos
                _durationMs.value = player.duration.coerceAtLeast(0L)

                // Check guest mode 30-second preview limit
                val currentUser = authRepository.getCurrentUser().firstOrNull()
                if (currentUser?.isGuest == true && pos >= 30_000L) {
                    player.pause()
                    _isPlaying.value = false
                    break
                }
                delay(250)
            }
        }
    }

    private fun stopProgressTracking() {
        progressTrackingJob?.cancel()
        progressTrackingJob = null
    }

    override fun playSong(song: Song, queue: List<Song>) {
        _currentSong.value = song
        _currentQueue.value = if (queue.contains(song)) queue else listOf(song) + queue

        val mediaItem = MediaItem.fromUri(song.audioUrl)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
        _isPlaying.value = true
    }

    override fun togglePlayPause() {
        if (player.isPlaying) {
            player.pause()
            _isPlaying.value = false
        } else {
            if (_currentSong.value != null) {
                player.play()
                _isPlaying.value = true
            }
        }
    }

    override fun seekTo(positionMs: Long) {
        player.seekTo(positionMs)
        _currentPositionMs.value = positionMs
    }

    override fun skipToNext() {
        val queue = _currentQueue.value
        val current = _currentSong.value ?: return
        val currentIndex = queue.indexOfFirst { it.id == current.id }
        if (currentIndex != -1 && currentIndex < queue.size - 1) {
            val nextSong = queue[currentIndex + 1]
            playSong(nextSong, queue)
        } else if (_isShuffleEnabled.value && queue.isNotEmpty()) {
            val randomSong = queue.random()
            playSong(randomSong, queue)
        } else if (queue.isNotEmpty()) {
            // Loop back to start
            playSong(queue.first(), queue)
        }
    }

    override fun skipToPrevious() {
        val queue = _currentQueue.value
        val current = _currentSong.value ?: return
        val currentIndex = queue.indexOfFirst { it.id == current.id }
        if (currentIndex > 0) {
            val prevSong = queue[currentIndex - 1]
            playSong(prevSong, queue)
        } else {
            seekTo(0)
        }
    }

    override fun toggleShuffle() {
        _isShuffleEnabled.value = !_isShuffleEnabled.value
    }

    override fun toggleRepeat() {
        _isRepeatEnabled.value = !_isRepeatEnabled.value
    }

    override fun reorderQueue(fromIndex: Int, toIndex: Int) {
        val list = _currentQueue.value.toMutableList()
        if (fromIndex in list.indices && toIndex in list.indices) {
            val item = list.removeAt(fromIndex)
            list.add(toIndex, item)
            _currentQueue.value = list
        }
    }

    override fun removeFromQueue(index: Int) {
        val list = _currentQueue.value.toMutableList()
        if (index in list.indices) {
            list.removeAt(index)
            _currentQueue.value = list
        }
    }

    override fun clearQueue() {
        _currentQueue.value = emptyList()
    }

    override fun setSleepTimer(minutes: Int, smoothFadeOut: Boolean) {
        cancelSleepTimer()
        if (minutes <= 0) return

        val totalSeconds = minutes * 60
        _sleepTimerConfig.value = SleepTimerConfig(
            durationMinutes = minutes,
            isSmoothFadeOut = smoothFadeOut,
            remainingSeconds = totalSeconds,
            isRunning = true
        )

        sleepTimerJob = scope.launch {
            var remaining = totalSeconds
            while (isActive && remaining > 0) {
                delay(1000)
                remaining--
                _sleepTimerConfig.value = _sleepTimerConfig.value.copy(remainingSeconds = remaining)

                // If smooth fade out enabled and under 30 seconds left
                if (smoothFadeOut && remaining in 1..30) {
                    val volume = remaining / 30f
                    player.volume = volume
                }
            }
            // Timer expired: stop playback
            player.pause()
            player.volume = 1f
            _isPlaying.value = false
            _sleepTimerConfig.value = SleepTimerConfig(isRunning = false)
        }
    }

    override fun cancelSleepTimer() {
        sleepTimerJob?.cancel()
        sleepTimerJob = null
        player.volume = 1f
        _sleepTimerConfig.value = SleepTimerConfig(isRunning = false)
    }
}
