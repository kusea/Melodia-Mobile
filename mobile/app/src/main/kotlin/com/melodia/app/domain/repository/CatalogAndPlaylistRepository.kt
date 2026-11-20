package com.melodia.app.domain.repository

import com.melodia.app.domain.model.Album
import com.melodia.app.domain.model.Artist
import com.melodia.app.domain.model.Playlist
import com.melodia.app.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface CatalogRepository {
    fun getQuickPicks(): Flow<List<Song>>
    fun getJumpBackIn(): Flow<List<Song>>
    fun getPopularArtists(): Flow<List<Artist>>
    fun getCuratedMixes(): Flow<List<Playlist>>
    fun searchSongs(query: String): Flow<List<Song>>
    fun searchArtists(query: String): Flow<List<Artist>>
    fun searchAlbums(query: String): Flow<List<Album>>
    fun searchPlaylists(query: String): Flow<List<Playlist>>
    fun getSongById(id: String): Flow<Song?>
    fun getArtistById(id: String): Flow<Artist?>
    fun getAlbumById(id: String): Flow<Album?>
    fun getFavorites(): Flow<List<Song>>
    suspend fun toggleFavorite(songId: String)
    fun getListeningHistory(): Flow<List<Song>>
    suspend fun recordPlayHistory(songId: String)
}

interface PlaylistRepository {
    fun getUserPlaylists(): Flow<List<Playlist>>
    fun getPlaylistById(id: String): Flow<Playlist?>
    suspend fun createPlaylist(name: String, description: String, isPublic: Boolean): Playlist
    suspend fun addSongToPlaylist(playlistId: String, songId: String)
    suspend fun removeSongFromPlaylist(playlistId: String, songId: String)
}
