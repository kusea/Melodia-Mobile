package com.melodia.app.data.repository

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PlaylistRepositoryTest {

    private lateinit var playlistRepository: PlaylistRepositoryImpl

    @Before
    fun setUp() {
        playlistRepository = PlaylistRepositoryImpl()
    }

    @Test
    fun getUserPlaylists_returnsDefaultPlaylists() = runTest {
        val playlists = playlistRepository.getUserPlaylists().first()
        assertTrue(playlists.isNotEmpty())
        assertEquals("Synthwave Dreams", playlists.first().name)
    }

    @Test
    fun createPlaylist_prependsNewPlaylist() = runTest {
        val created = playlistRepository.createPlaylist(
            name = "Late Night Hyperdrive",
            description = "High tempo synth",
            isPublic = true
        )
        assertNotNull(created.id)
        assertEquals("Late Night Hyperdrive", created.name)

        val playlists = playlistRepository.getUserPlaylists().first()
        assertEquals(created.id, playlists.first().id)
    }

    @Test
    fun addAndRemoveSongInPlaylist_updatesCountCorrectly() = runTest {
        val initialPlaylists = playlistRepository.getUserPlaylists().first()
        val targetPlaylist = initialPlaylists.first()
        val initialCount = targetPlaylist.songCount

        playlistRepository.addSongToPlaylist(targetPlaylist.id, "s_07")
        val updated = playlistRepository.getPlaylistById(targetPlaylist.id).first()
        assertNotNull(updated)
        assertEquals(initialCount + 1, updated!!.songCount)

        playlistRepository.removeSongFromPlaylist(targetPlaylist.id, "s_07")
        val finalPlaylist = playlistRepository.getPlaylistById(targetPlaylist.id).first()
        assertNotNull(finalPlaylist)
        assertEquals(initialCount, finalPlaylist!!.songCount)
    }
}
