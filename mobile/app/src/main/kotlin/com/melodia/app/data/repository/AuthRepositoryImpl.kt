package com.melodia.app.data.repository

import com.melodia.app.data.local.security.TokenStoreInterface
import com.melodia.app.domain.model.User
import com.melodia.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class AuthRepositoryImpl(private val tokenStore: TokenStoreInterface) : AuthRepository {

    private val currentUserFlow = MutableStateFlow<User?>(
        // Default signed-in user as Alex Rivera from Figma mockup
        User(
            id = "u_101",
            email = "alex.rivera@melodia.stream",
            displayName = "Alex Rivera",
            avatarUrl = "https://picsum.photos/seed/alex/200/200",
            isGuest = false,
            preferredGenres = listOf("Synthwave", "Lo-Fi Chill", "Cyberpunk EDM")
        )
    )

    override fun getCurrentUser(): Flow<User?> = currentUserFlow.asStateFlow()

    override suspend fun login(email: String, password: String): Result<User> {
        if (email.isBlank() || password.length < 6) {
            return Result.failure(IllegalArgumentException("Invalid email or password (min 6 characters)"))
        }
        val user = User(
            id = "u_${UUID.randomUUID().toString().take(6)}",
            email = email,
            displayName = email.substringBefore("@").replace(".", " ").capitalizeWords(),
            isGuest = false
        )
        tokenStore.accessToken = "mock_jwt_access_token_${System.currentTimeMillis()}"
        tokenStore.refreshToken = "mock_jwt_refresh_token_${System.currentTimeMillis()}"
        tokenStore.userId = user.id
        currentUserFlow.value = user
        return Result.success(user)
    }

    override suspend fun register(username: String, email: String, password: String): Result<User> {
        if (email.isBlank() || password.length < 8) {
            return Result.failure(IllegalArgumentException("Password must be at least 8 characters"))
        }
        val user = User(
            id = "u_${UUID.randomUUID().toString().take(6)}",
            email = email,
            displayName = username.ifBlank { email.substringBefore("@") },
            isGuest = false
        )
        tokenStore.accessToken = "mock_jwt_access_token_${System.currentTimeMillis()}"
        tokenStore.refreshToken = "mock_jwt_refresh_token_${System.currentTimeMillis()}"
        tokenStore.userId = user.id
        currentUserFlow.value = user
        return Result.success(user)
    }

    override suspend fun loginAsGuest(): User {
        val guest = User(
            id = "guest_${UUID.randomUUID().toString().take(6)}",
            email = "guest@melodia.local",
            displayName = "Guest Listener",
            isGuest = true
        )
        currentUserFlow.value = guest
        return guest
    }

    override suspend fun logout() {
        tokenStore.clear()
        currentUserFlow.value = null
    }

    override suspend fun savePreferredGenres(genres: List<String>) {
        currentUserFlow.value = currentUserFlow.value?.copy(preferredGenres = genres)
    }

    private fun String.capitalizeWords(): String =
        split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
}
