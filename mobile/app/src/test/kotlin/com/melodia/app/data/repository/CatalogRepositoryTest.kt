package com.melodia.app.data.repository

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CatalogRepositoryTest {

    private lateinit var catalogRepository: CatalogRepositoryImpl

    @Before
    fun setUp() {
        catalogRepository = CatalogRepositoryImpl()
    }

    @Test
    fun getQuickPicks_returnsNonEmptyList() = runTest {
        val songs = catalogRepository.getQuickPicks().first()
        assertTrue(songs.isNotEmpty())
        assertEquals("Midnight Echoes", songs.first().title)
    }

    @Test
    fun searchSongs_withMatchingQuery_returnsMatchingSongs() = runTest {
        val results = catalogRepository.searchSongs("Aurora").first()
        assertTrue(results.isNotEmpty())
        assertTrue(results.all { it.artist.contains("Aurora") || it.title.contains("Aurora") })
    }

    @Test
    fun searchSongs_withNonMatchingQuery_returnsEmptyList() = runTest {
        val results = catalogRepository.searchSongs("Xyzz99synth").first()
        assertTrue(results.isEmpty())
    }

    @Test
    fun toggleFavorite_flipsFavoriteState() = runTest {
        val initialFavorites = catalogRepository.getFavorites().first()
        val targetSong = initialFavorites.first()
        assertTrue(targetSong.isFavorite)

        catalogRepository.toggleFavorite(targetSong.id)

        val updatedFavorites = catalogRepository.getFavorites().first()
        assertFalse(updatedFavorites.any { it.id == targetSong.id })

        catalogRepository.toggleFavorite(targetSong.id)
        val restoredFavorites = catalogRepository.getFavorites().first()
        assertTrue(restoredFavorites.any { it.id == targetSong.id })
    }

    @Test
    fun recordPlayHistory_addsSongToFrontOfHistory() = runTest {
        val songId = "s_06"
        catalogRepository.recordPlayHistory(songId)

        val history = catalogRepository.getListeningHistory().first()
        assertEquals(songId, history.first().id)
    }
}
