package com.melodia.app.data.repository

import com.melodia.app.data.local.security.MemoryTokenStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AuthRepositoryTest {

    private lateinit var tokenStore: MemoryTokenStore
    private lateinit var authRepository: AuthRepositoryImpl

    @Before
    fun setUp() {
        tokenStore = MemoryTokenStore()
        authRepository = AuthRepositoryImpl(tokenStore)
    }

    @Test
    fun initialUser_isAlexRivera() = runTest {
        val user = authRepository.getCurrentUser().first()
        assertNotNull(user)
        assertEquals("Alex Rivera", user!!.displayName)
        assertFalse(user.isGuest)
    }

    @Test
    fun login_withValidCredentials_storesTokensAndUpdatesUser() = runTest {
        val result = authRepository.login("user@test.com", "validPass123")
        assertTrue(result.isSuccess)
        val user = result.getOrNull()
        assertNotNull(user)
        assertEquals("user@test.com", user!!.email)
        assertNotNull(tokenStore.accessToken)
        assertNotNull(tokenStore.refreshToken)
    }

    @Test
    fun login_withInvalidPassword_fails() = runTest {
        val result = authRepository.login("user@test.com", "123")
        assertTrue(result.isFailure)
    }

    @Test
    fun loginAsGuest_setsGuestUserWithoutTokens() = runTest {
        val guest = authRepository.loginAsGuest()
        assertTrue(guest.isGuest)
        val current = authRepository.getCurrentUser().first()
        assertEquals(guest.id, current?.id)
    }

    @Test
    fun logout_clearsUserAndTokens() = runTest {
        authRepository.login("user@test.com", "validPass123")
        assertNotNull(tokenStore.accessToken)

        authRepository.logout()
        assertNull(tokenStore.accessToken)
        assertNull(authRepository.getCurrentUser().first())
    }

    @Test
    fun savePreferredGenres_updatesUserGenres() = runTest {
        val genres = listOf("Synthwave", "Cyberpunk", "Outrun")
        authRepository.savePreferredGenres(genres)

        val user = authRepository.getCurrentUser().first()
        assertEquals(genres, user?.preferredGenres)
    }
}
