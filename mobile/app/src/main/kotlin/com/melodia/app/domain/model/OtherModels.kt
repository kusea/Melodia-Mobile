package com.melodia.app.domain.model

data class Playlist(
    val id: String,
    val name: String,
    val description: String,
    val coverUrl: String,
    val creatorName: String = "You",
    val isPublic: Boolean = true,
    val isCollaborative: Boolean = false,
    val isAutoSync: Boolean = true,
    val songCount: Int = 0,
    val durationSec: Int = 0,
    val songs: List<Song> = emptyList()
)

data class User(
    val id: String,
    val email: String,
    val displayName: String,
    val avatarUrl: String = "",
    val isGuest: Boolean = false,
    val preferredGenres: List<String> = emptyList()
)

data class LyricLine(
    val timestampMs: Long,
    val text: String
)

enum class DownloadStatus {
    QUEUED, DOWNLOADING, PAUSED, COMPLETED, FAILED
}

data class DownloadTask(
    val songId: String,
    val title: String,
    val artist: String,
    val coverUrl: String,
    val totalBytes: Long,
    val downloadedBytes: Long,
    val speedBytesPerSec: Long = 0L,
    val status: DownloadStatus = DownloadStatus.QUEUED
) {
    val progressPercent: Int
        get() = if (totalBytes > 0) ((downloadedBytes * 100) / totalBytes).toInt() else 0

    val speedMbPerSec: String
        get() = "%.1f".format(speedBytesPerSec / (1024.0 * 1024.0))
}

data class SleepTimerConfig(
    val durationMinutes: Int = 0,
    val isSmoothFadeOut: Boolean = true,
    val remainingSeconds: Int = 0,
    val isRunning: Boolean = false
)
