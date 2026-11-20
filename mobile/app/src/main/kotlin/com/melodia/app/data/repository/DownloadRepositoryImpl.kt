package com.melodia.app.data.repository

import android.content.Context
import com.melodia.app.data.remote.mock.MockDataProvider
import com.melodia.app.domain.model.DownloadStatus
import com.melodia.app.domain.model.DownloadTask
import com.melodia.app.domain.model.Song
import com.melodia.app.domain.repository.DownloadRepository
import com.melodia.app.domain.repository.StorageInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class DownloadRepositoryImpl(
    private val context: Context,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : DownloadRepository {

    private val downloadedSongsFlow = MutableStateFlow<List<Song>>(
        MockDataProvider.songs.filter { it.isDownloaded }
    )

    private val activeTasksFlow = MutableStateFlow<List<DownloadTask>>(
        listOf(
            DownloadTask(
                songId = "s_active_1",
                title = "Starlight Drift",
                artist = "Aurora Waves",
                coverUrl = "https://picsum.photos/seed/starlight/400/400",
                totalBytes = 51_000_000L,
                downloadedBytes = 42_800_000L,
                speedBytesPerSec = 7_130_304L, // 6.8 MB/s
                status = DownloadStatus.DOWNLOADING
            ),
            DownloadTask(
                songId = "s_active_2",
                title = "Solar Flares",
                artist = "Aurora Waves",
                coverUrl = "https://picsum.photos/seed/solar/400/400",
                totalBytes = 47_100_000L,
                downloadedBytes = 24_500_000L,
                speedBytesPerSec = 4_718_592L, // 4.5 MB/s
                status = DownloadStatus.DOWNLOADING
            ),
            DownloadTask(
                songId = "s_active_3",
                title = "Hyperdrive Echo",
                artist = "Bicep & Friends",
                coverUrl = "https://picsum.photos/seed/hyper/400/400",
                totalBytes = 11_600_000L,
                downloadedBytes = 2_100_000L,
                speedBytesPerSec = 3_040_870L, // 2.9 MB/s
                status = DownloadStatus.DOWNLOADING
            )
        )
    )

    private val storageInfoFlow = MutableStateFlow(
        StorageInfo(
            melodiaUsedBytes = 5_798_205_850L, // ~5.4 GB
            otherUsedBytes = 45_097_156_608L,   // ~42 GB
            freeBytes = 86_543_974_400L,        // ~80.6 GB
            isWifiOnly = true
        )
    )

    override fun getDownloadedSongs(): Flow<List<Song>> = downloadedSongsFlow.asStateFlow()

    override fun getActiveTasks(): Flow<List<DownloadTask>> = activeTasksFlow.asStateFlow()

    override suspend fun enqueueDownload(song: Song) {
        val newTask = DownloadTask(
            songId = song.id,
            title = song.title,
            artist = song.artist,
            coverUrl = song.coverUrl,
            totalBytes = 35_000_000L,
            downloadedBytes = 0L,
            speedBytesPerSec = 5_242_880L,
            status = DownloadStatus.QUEUED
        )
        activeTasksFlow.value = activeTasksFlow.value + newTask

        // Simulate background download with progress
        scope.launch {
            delay(1000)
            activeTasksFlow.value = activeTasksFlow.value.map {
                if (it.songId == song.id) it.copy(status = DownloadStatus.DOWNLOADING) else it
            }
            var downloaded = 0L
            while (downloaded < 35_000_000L) {
                delay(500)
                downloaded += 5_000_000L
                activeTasksFlow.value = activeTasksFlow.value.map {
                    if (it.songId == song.id) it.copy(downloadedBytes = downloaded.coerceAtMost(35_000_000L)) else it
                }
            }
            // Finished
            activeTasksFlow.value = activeTasksFlow.value.filter { it.songId != song.id }
            val completedSong = song.copy(isDownloaded = true)
            downloadedSongsFlow.value = downloadedSongsFlow.value + completedSong
        }
    }

    override suspend fun pauseDownload(songId: String) {
        activeTasksFlow.value = activeTasksFlow.value.map {
            if (it.songId == songId) it.copy(status = DownloadStatus.PAUSED) else it
        }
    }

    override suspend fun resumeDownload(songId: String) {
        activeTasksFlow.value = activeTasksFlow.value.map {
            if (it.songId == songId) it.copy(status = DownloadStatus.DOWNLOADING) else it
        }
    }

    override suspend fun deleteDownload(songId: String) {
        downloadedSongsFlow.value = downloadedSongsFlow.value.filter { it.id != songId }
        activeTasksFlow.value = activeTasksFlow.value.filter { it.songId != songId }
    }

    override fun getStorageInfo(): Flow<StorageInfo> = storageInfoFlow.asStateFlow()
}
