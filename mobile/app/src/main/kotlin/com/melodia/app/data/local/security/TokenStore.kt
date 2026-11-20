package com.melodia.app.data.local.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

interface TokenStoreInterface {
    var accessToken: String?
    var refreshToken: String?
    var userId: String?
    fun clear()
}

class MemoryTokenStore : TokenStoreInterface {
    override var accessToken: String? = null
    override var refreshToken: String? = null
    override var userId: String? = null
    override fun clear() {
        accessToken = null
        refreshToken = null
        userId = null
    }
}

class TokenStore(context: Context) : TokenStoreInterface {

    private val prefs: SharedPreferences = try {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context,
            "melodia_secure_tokens",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    } catch (e: Exception) {
        // Fallback for emulator / non-supported hardware
        context.getSharedPreferences("melodia_secure_tokens_fallback", Context.MODE_PRIVATE)
    }

    override var accessToken: String?
        get() = prefs.getString("access_token", null)
        set(value) = prefs.edit().putString("access_token", value).apply()

    override var refreshToken: String?
        get() = prefs.getString("refresh_token", null)
        set(value) = prefs.edit().putString("refresh_token", value).apply()

    override var userId: String?
        get() = prefs.getString("user_id", null)
        set(value) = prefs.edit().putString("user_id", value).apply()

    override fun clear() {
        prefs.edit().clear().apply()
    }
}
