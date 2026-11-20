package com.melodia.app.domain.model

data class Artist(
    val id: String,
    val name: String,
    val bio: String,
    val imageUrl: String,
    val monthlyListeners: String,
    val isVerified: Boolean = true,
    val isFollowed: Boolean = false,
    val topSongs: List<Song> = emptyList(),
    val albums: List<Album> = emptyList()
)

data class Album(
    val id: String,
    val title: String,
    val artist: String,
    val artistId: String,
    val coverUrl: String,
    val releaseYear: Int,
    val trackCount: Int,
    val durationSec: Int,
    val recordLabel: String = "Melodia Records",
    val songs: List<Song> = emptyList()
)
