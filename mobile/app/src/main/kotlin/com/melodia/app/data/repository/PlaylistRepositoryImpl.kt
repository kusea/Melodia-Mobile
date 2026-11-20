package com.melodia.app.data.repository

import com.melodia.app.data.remote.mock.MockDataProvider
import com.melodia.app.domain.model.Playlist
import com.melodia.app.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.util.UUID

class PlaylistRepositoryImpl : PlaylistRepository {

    private val playlistsFlow = MutableStateFlow(MockDataProvider.playlists)

    override fun getUserPlaylists(): Flow<List<Playlist>> = playlistsFlow.asStateFlow()

    override fun getPlaylistById(id: String): Flow<Playlist?> = playlistsFlow.map { list ->
        list.find { it.id == id }
    }

    override suspend fun createPlaylist(name: String, description: String, isPublic: Boolean): Playlist {
        val newPlaylist = Playlist(
            id = "pl_${UUID.randomUUID().toString().take(8)}",
            name = name,
            description = description,
            coverUrl = "https://picsum.photos/seed/${name.hashCode()}/400/400",
            creatorName = "You",
            isPublic = isPublic,
            isAutoSync = true,
            songCount = 0,
            durationSec = 0,
            songs = emptyList()
        )
        playlistsFlow.value = listOf(newPlaylist) + playlistsFlow.value
        return newPlaylist
    }

    override suspend fun addSongToPlaylist(playlistId: String, songId: String) {
        val songToAdd = MockDataProvider.songs.find { it.id == songId } ?: return
        playlistsFlow.value = playlistsFlow.value.map { pl ->
            if (pl.id == playlistId) {
                val updatedSongs = pl.songs + songToAdd
                pl.copy(
                    songs = updatedSongs,
                    songCount = pl.songCount + 1,
                    durationSec = pl.durationSec + songToAdd.durationSec
                )
            } else pl
        }
    }

    override suspend fun removeSongFromPlaylist(playlistId: String, songId: String) {
        playlistsFlow.value = playlistsFlow.value.map { pl ->
            if (pl.id == playlistId) {
                val songToRemove = pl.songs.find { it.id == songId }
                val updatedSongs = pl.songs.filter { it.id != songId }
                pl.copy(
                    songs = updatedSongs,
                    songCount = (pl.songCount - 1).coerceAtLeast(0),
                    durationSec = if (songToRemove != null) (pl.durationSec - songToRemove.durationSec).coerceAtLeast(0) else pl.durationSec
                )
            } else pl
        }
    }
}
