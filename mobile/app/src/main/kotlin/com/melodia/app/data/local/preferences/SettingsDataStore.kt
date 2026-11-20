package com.melodia.app.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "melodia_settings")

class SettingsDataStore(private val context: Context) {

    companion object {
        val KEY_THEME = stringPreferencesKey("theme_mode")
        val KEY_AUDIO_QUALITY = stringPreferencesKey("audio_quality")
        val KEY_WIFI_ONLY = booleanPreferencesKey("download_wifi_only")
        val KEY_OFFLINE_MODE = booleanPreferencesKey("offline_mode_only")
        val KEY_TEXT_SCALING = booleanPreferencesKey("text_scaling")
    }

    val themeMode: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_THEME] ?: "DARK"
    }

    val isOfflineModeOnly: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_OFFLINE_MODE] ?: false
    }

    val isWifiOnly: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_WIFI_ONLY] ?: true
    }

    val audioQuality: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_AUDIO_QUALITY] ?: "320 kbps"
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_THEME] = mode
        }
    }

    suspend fun setOfflineModeOnly(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_OFFLINE_MODE] = enabled
        }
    }

    suspend fun setWifiOnly(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_WIFI_ONLY] = enabled
        }
    }

    suspend fun setAudioQuality(quality: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_AUDIO_QUALITY] = quality
        }
    }
}
