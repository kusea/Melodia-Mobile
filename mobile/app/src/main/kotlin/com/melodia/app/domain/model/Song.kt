package com.melodia.app.domain.model

data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val artistId: String,
    val album: String,
    val albumId: String,
    val durationSec: Int,
    val audioUrl: String,
    val coverUrl: String,
    val isLossless: Boolean = true,
    val bitrateKbps: Int = 320,
    val isDownloaded: Boolean = false,
    val isFavorite: Boolean = false,
    val lyrics: String? = null,
    val playCount: Long = 0L
) {
    val durationFormatted: String
        get() {
            val minutes = durationSec / 60
            val seconds = durationSec % 60
            return "%d:%02d".format(minutes, seconds)
        }
}
