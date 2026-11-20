package com.melodia.app.domain.repository

import com.melodia.app.domain.model.DownloadTask
import com.melodia.app.domain.model.SleepTimerConfig
import com.melodia.app.domain.model.Song
import com.melodia.app.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    fun getCurrentUser(): Flow<User?>
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(username: String, email: String, password: String): Result<User>
    suspend fun loginAsGuest(): User
    suspend fun logout()
    suspend fun savePreferredGenres(genres: List<String>)
}

interface DownloadRepository {
    fun getDownloadedSongs(): Flow<List<Song>>
    fun getActiveTasks(): Flow<List<DownloadTask>>
    suspend fun enqueueDownload(song: Song)
    suspend fun pauseDownload(songId: String)
    suspend fun resumeDownload(songId: String)
    suspend fun deleteDownload(songId: String)
    fun getStorageInfo(): Flow<StorageInfo>
}

data class StorageInfo(
    val melodiaUsedBytes: Long,
    val otherUsedBytes: Long,
    val freeBytes: Long,
    val isWifiOnly: Boolean = true
)

interface PlayerRepository {
    val currentSong: StateFlow<Song?>
    val isPlaying: StateFlow<Boolean>
    val currentPositionMs: StateFlow<Long>
    val durationMs: StateFlow<Long>
    val isShuffleEnabled: StateFlow<Boolean>
    val isRepeatEnabled: StateFlow<Boolean>
    val currentQueue: StateFlow<List<Song>>
    val sleepTimerConfig: StateFlow<SleepTimerConfig>

    fun playSong(song: Song, queue: List<Song> = listOf(song))
    fun togglePlayPause()
    fun seekTo(positionMs: Long)
    fun skipToNext()
    fun skipToPrevious()
    fun toggleShuffle()
    fun toggleRepeat()
    fun reorderQueue(fromIndex: Int, toIndex: Int)
    fun removeFromQueue(index: Int)
    fun clearQueue()
    fun setSleepTimer(minutes: Int, smoothFadeOut: Boolean)
    fun cancelSleepTimer()
}
