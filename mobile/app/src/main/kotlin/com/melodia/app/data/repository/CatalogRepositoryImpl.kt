package com.melodia.app.data.repository

import com.melodia.app.data.remote.mock.MockDataProvider
import com.melodia.app.domain.model.Album
import com.melodia.app.domain.model.Artist
import com.melodia.app.domain.model.Playlist
import com.melodia.app.domain.model.Song
import com.melodia.app.domain.repository.CatalogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class CatalogRepositoryImpl : CatalogRepository {

    private val songsFlow = MutableStateFlow(MockDataProvider.songs)
    private val artistsFlow = MutableStateFlow(MockDataProvider.artists)
    private val albumsFlow = MutableStateFlow(MockDataProvider.albums)
    private val playlistsFlow = MutableStateFlow(MockDataProvider.playlists)
    private val playHistoryFlow = MutableStateFlow<List<Song>>(MockDataProvider.songs.take(5))

    override fun getQuickPicks(): Flow<List<Song>> = songsFlow.map { it.take(6) }

    override fun getJumpBackIn(): Flow<List<Song>> = songsFlow.map { it.drop(2).take(4) }

    override fun getPopularArtists(): Flow<List<Artist>> = artistsFlow.asStateFlow()

    override fun getCuratedMixes(): Flow<List<Playlist>> = playlistsFlow.asStateFlow()

    override fun searchSongs(query: String): Flow<List<Song>> = songsFlow.map { list ->
        if (query.isBlank()) emptyList()
        else list.filter {
            it.title.contains(query, ignoreCase = true) ||
            it.artist.contains(query, ignoreCase = true) ||
            it.album.contains(query, ignoreCase = true)
        }
    }

    override fun searchArtists(query: String): Flow<List<Artist>> = artistsFlow.map { list ->
        if (query.isBlank()) emptyList()
        else list.filter { it.name.contains(query, ignoreCase = true) }
    }

    override fun searchAlbums(query: String): Flow<List<Album>> = albumsFlow.map { list ->
        if (query.isBlank()) emptyList()
        else list.filter { it.title.contains(query, ignoreCase = true) || it.artist.contains(query, ignoreCase = true) }
    }

    override fun searchPlaylists(query: String): Flow<List<Playlist>> = playlistsFlow.map { list ->
        if (query.isBlank()) emptyList()
        else list.filter { it.name.contains(query, ignoreCase = true) }
    }

    override fun getSongById(id: String): Flow<Song?> = songsFlow.map { list ->
        list.find { it.id == id }
    }

    override fun getArtistById(id: String): Flow<Artist?> = artistsFlow.map { list ->
        list.find { it.id == id }
    }

    override fun getAlbumById(id: String): Flow<Album?> = albumsFlow.map { list ->
        list.find { it.id == id }
    }

    override fun getFavorites(): Flow<List<Song>> = songsFlow.map { list ->
        list.filter { it.isFavorite }
    }

    override suspend fun toggleFavorite(songId: String) {
        val current = songsFlow.value
        val updated = current.map {
            if (it.id == songId) it.copy(isFavorite = !it.isFavorite) else it
        }
        songsFlow.value = updated
    }

    override fun getListeningHistory(): Flow<List<Song>> = playHistoryFlow.asStateFlow()

    override suspend fun recordPlayHistory(songId: String) {
        val song = songsFlow.value.find { it.id == songId } ?: return
        val currentHistory = playHistoryFlow.value.toMutableList()
        currentHistory.removeAll { it.id == songId }
        currentHistory.add(0, song)
        playHistoryFlow.value = currentHistory
    }
}
