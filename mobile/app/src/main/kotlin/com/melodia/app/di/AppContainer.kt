package com.melodia.app.di

import android.content.Context
import com.melodia.app.data.local.preferences.SettingsDataStore
import com.melodia.app.data.local.security.TokenStore
import com.melodia.app.data.repository.AuthRepositoryImpl
import com.melodia.app.data.repository.CatalogRepositoryImpl
import com.melodia.app.data.repository.DownloadRepositoryImpl
import com.melodia.app.data.repository.PlayerRepositoryImpl
import com.melodia.app.data.repository.PlaylistRepositoryImpl
import com.melodia.app.domain.repository.AuthRepository
import com.melodia.app.domain.repository.CatalogRepository
import com.melodia.app.domain.repository.DownloadRepository
import com.melodia.app.domain.repository.PlayerRepository
import com.melodia.app.domain.repository.PlaylistRepository

interface AppContainer {
    val tokenStore: TokenStore
    val settingsDataStore: SettingsDataStore
    val catalogRepository: CatalogRepository
    val playlistRepository: PlaylistRepository
    val authRepository: AuthRepository
    val downloadRepository: DownloadRepository
    val playerRepository: PlayerRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    override val tokenStore: TokenStore by lazy { TokenStore(context) }
    override val settingsDataStore: SettingsDataStore by lazy { SettingsDataStore(context) }
    override val catalogRepository: CatalogRepository by lazy { CatalogRepositoryImpl() }
    override val playlistRepository: PlaylistRepository by lazy { PlaylistRepositoryImpl() }
    override val authRepository: AuthRepository by lazy { AuthRepositoryImpl(tokenStore) }
    override val downloadRepository: DownloadRepository by lazy { DownloadRepositoryImpl(context) }
    override val playerRepository: PlayerRepository by lazy { PlayerRepositoryImpl(context, authRepository) }
}
