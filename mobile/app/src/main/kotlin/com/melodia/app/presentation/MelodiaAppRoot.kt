package com.melodia.app.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.melodia.app.R
import com.melodia.app.di.AppContainer
import com.melodia.app.domain.model.Album
import com.melodia.app.domain.model.Artist
import com.melodia.app.domain.model.Playlist
import com.melodia.app.domain.model.Song
import com.melodia.app.presentation.artist.AlbumDetailScreen
import com.melodia.app.presentation.artist.ArtistDetailScreen
import com.melodia.app.presentation.auth.AuthScreen
import com.melodia.app.presentation.common.MiniPlayer
import com.melodia.app.presentation.common.SongActionSheet
import com.melodia.app.presentation.downloads.DownloadsScreen
import com.melodia.app.presentation.home.HomeScreen
import com.melodia.app.presentation.library.LibraryScreen
import com.melodia.app.presentation.library.PlaylistDetailScreen
import com.melodia.app.presentation.player.LyricsSheet
import com.melodia.app.presentation.player.NowPlayingScreen
import com.melodia.app.presentation.player.QueueSheet
import com.melodia.app.presentation.player.SleepTimerSheet
import com.melodia.app.presentation.search.SearchScreen
import com.melodia.app.presentation.settings.SettingsScreen
import com.melodia.app.presentation.theme.DarkBackground
import com.melodia.app.presentation.theme.DarkSurface
import com.melodia.app.presentation.theme.NeonViolet
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

sealed class Screen {
    object Home : Screen()
    object Search : Screen()
    object Library : Screen()
    object Downloads : Screen()
    data class PlaylistDetail(val playlistId: String) : Screen()
    data class ArtistDetail(val artistId: String) : Screen()
    data class AlbumDetail(val albumId: String) : Screen()
    object Settings : Screen()
    object Auth : Screen()
}

@Composable
fun MelodiaAppRoot(container: AppContainer) {
    val scope = rememberCoroutineScope()

    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    var isNowPlayingExpanded by remember { mutableStateOf(false) }

    // Sheets
    var actionSheetSong by remember { mutableStateOf<Song?>(null) }
    var showLyricsSheet by remember { mutableStateOf(false) }
    var showSleepTimerSheet by remember { mutableStateOf(false) }
    var showQueueSheet by remember { mutableStateOf(false) }

    // State from Repositories
    val currentSong by container.playerRepository.currentSong.collectAsState()
    val isPlaying by container.playerRepository.isPlaying.collectAsState()
    val currentPositionMs by container.playerRepository.currentPositionMs.collectAsState()
    val durationMs by container.playerRepository.durationMs.collectAsState()
    val isShuffle by container.playerRepository.isShuffleEnabled.collectAsState()
    val isRepeat by container.playerRepository.isRepeatEnabled.collectAsState()
    val currentQueue by container.playerRepository.currentQueue.collectAsState()
    val sleepTimerConfig by container.playerRepository.sleepTimerConfig.collectAsState()

    val quickPicks by container.catalogRepository.getQuickPicks().collectAsState(initial = emptyList())
    val jumpBackIn by container.catalogRepository.getJumpBackIn().collectAsState(initial = emptyList())
    val popularArtists by container.catalogRepository.getPopularArtists().collectAsState(initial = emptyList())
    val curatedPlaylists by container.catalogRepository.getCuratedMixes().collectAsState(initial = emptyList())
    val favoriteSongs by container.catalogRepository.getFavorites().collectAsState(initial = emptyList())

    val userPlaylists by container.playlistRepository.getUserPlaylists().collectAsState(initial = emptyList())
    val downloadedSongs by container.downloadRepository.getDownloadedSongs().collectAsState(initial = emptyList())
    val activeTasks by container.downloadRepository.getActiveTasks().collectAsState(initial = emptyList())
    val storageInfo by container.downloadRepository.getStorageInfo().collectAsState(
        initial = com.melodia.app.domain.repository.StorageInfo(0, 0, 0)
    )
    val currentUser by container.authRepository.getCurrentUser().collectAsState(initial = null)

    // Start with a song ready for instant music play
    remember {
        if (currentSong == null && quickPicks.isNotEmpty()) {
            container.playerRepository.playSong(quickPicks.first(), quickPicks)
        }
    }

    Scaffold(
        bottomBar = {
            if (currentScreen in listOf(Screen.Home, Screen.Search, Screen.Library, Screen.Downloads)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkBackground)
                        .navigationBarsPadding()
                ) {
                    // Floating MiniPlayer (76dp)
                    MiniPlayer(
                        song = currentSong,
                        isPlaying = isPlaying,
                        progressMs = currentPositionMs,
                        durationMs = durationMs,
                        onTogglePlayPause = { container.playerRepository.togglePlayPause() },
                        onSkipNext = { container.playerRepository.skipToNext() },
                        onExpandNowPlaying = { isNowPlayingExpanded = true }
                    )

                    // Navigation Bar (80dp)
                    NavigationBar(
                        containerColor = DarkSurface,
                        tonalElevation = 8.dp,
                        modifier = Modifier.height(80.dp)
                    ) {
                        NavigationBarItem(
                            selected = currentScreen == Screen.Home,
                            onClick = { currentScreen = Screen.Home },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                            label = { Text(stringResource(R.string.nav_home)) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = NeonViolet,
                                selectedTextColor = NeonViolet,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = NeonViolet.copy(alpha = 0.15f)
                            )
                        )

                        NavigationBarItem(
                            selected = currentScreen == Screen.Search,
                            onClick = { currentScreen = Screen.Search },
                            icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                            label = { Text(stringResource(R.string.nav_search)) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = NeonViolet,
                                selectedTextColor = NeonViolet,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = NeonViolet.copy(alpha = 0.15f)
                            )
                        )

                        NavigationBarItem(
                            selected = currentScreen == Screen.Library,
                            onClick = { currentScreen = Screen.Library },
                            icon = { Icon(Icons.Default.LibraryMusic, contentDescription = "Library") },
                            label = { Text(stringResource(R.string.nav_library)) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = NeonViolet,
                                selectedTextColor = NeonViolet,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = NeonViolet.copy(alpha = 0.15f)
                            )
                        )

                        NavigationBarItem(
                            selected = currentScreen == Screen.Downloads,
                            onClick = { currentScreen = Screen.Downloads },
                            icon = { Icon(Icons.Default.Download, contentDescription = "Downloads") },
                            label = { Text(stringResource(R.string.nav_downloads)) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = NeonViolet,
                                selectedTextColor = NeonViolet,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = NeonViolet.copy(alpha = 0.15f)
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val screen = currentScreen) {
                is Screen.Home -> {
                    HomeScreen(
                        quickPicks = quickPicks,
                        jumpBackIn = jumpBackIn,
                        popularArtists = popularArtists,
                        curatedPlaylists = curatedPlaylists,
                        onSongClick = { song ->
                            container.playerRepository.playSong(song, quickPicks)
                        },
                        onArtistClick = { artistId -> currentScreen = Screen.ArtistDetail(artistId) },
                        onPlaylistClick = { playlistId -> currentScreen = Screen.PlaylistDetail(playlistId) },
                        onOpenSettings = { currentScreen = Screen.Settings }
                    )
                }

                is Screen.Search -> {
                    SearchScreen(
                        onSongClick = { song ->
                            container.playerRepository.playSong(song, listOf(song))
                        },
                        onSongMoreClick = { song -> actionSheetSong = song },
                        onArtistClick = { artistId -> currentScreen = Screen.ArtistDetail(artistId) },
                        onAlbumClick = { albumId -> currentScreen = Screen.AlbumDetail(albumId) },
                        searchSongs = { q ->
                            quickPicks.filter { it.title.contains(q, true) || it.artist.contains(q, true) }
                        },
                        searchArtists = { q ->
                            popularArtists.filter { it.name.contains(q, true) }
                        },
                        searchAlbums = { q ->
                            emptyList()
                        },
                        searchPlaylists = { q ->
                            curatedPlaylists.filter { it.name.contains(q, true) }
                        }
                    )
                }

                is Screen.Library -> {
                    LibraryScreen(
                        playlists = userPlaylists,
                        favoriteSongs = favoriteSongs,
                        onPlaylistClick = { id -> currentScreen = Screen.PlaylistDetail(id) },
                        onLikedSongsClick = {
                            if (userPlaylists.isNotEmpty()) {
                                currentScreen = Screen.PlaylistDetail(userPlaylists.first().id)
                            }
                        },
                        onCreatePlaylist = { name, desc, isPublic ->
                            scope.launch {
                                container.playlistRepository.createPlaylist(name, desc, isPublic)
                            }
                        }
                    )
                }

                is Screen.Downloads -> {
                    DownloadsScreen(
                        downloadedSongs = downloadedSongs,
                        activeTasks = activeTasks,
                        storageInfo = storageInfo,
                        onSongClick = { song ->
                            container.playerRepository.playSong(song, downloadedSongs)
                        },
                        onSongMoreClick = { song -> actionSheetSong = song },
                        onShuffleAll = {
                            if (downloadedSongs.isNotEmpty()) {
                                container.playerRepository.playSong(downloadedSongs.shuffled().first(), downloadedSongs.shuffled())
                            }
                        }
                    )
                }

                is Screen.PlaylistDetail -> {
                    val playlist = userPlaylists.find { it.id == screen.playlistId }
                        ?: curatedPlaylists.find { it.id == screen.playlistId }
                    PlaylistDetailScreen(
                        playlist = playlist,
                        onBack = { currentScreen = Screen.Library },
                        onSongClick = { song ->
                            container.playerRepository.playSong(song, playlist?.songs ?: listOf(song))
                        },
                        onSongMoreClick = { song -> actionSheetSong = song },
                        onPlayAll = {
                            playlist?.songs?.firstOrNull()?.let { song ->
                                container.playerRepository.playSong(song, playlist.songs)
                            }
                        },
                        onShuffleAll = {
                            playlist?.songs?.shuffled()?.firstOrNull()?.let { song ->
                                container.playerRepository.playSong(song, playlist.songs.shuffled())
                            }
                        }
                    )
                }

                is Screen.ArtistDetail -> {
                    val artist = popularArtists.find { it.id == screen.artistId }
                    ArtistDetailScreen(
                        artist = artist,
                        onBack = { currentScreen = Screen.Home },
                        onSongClick = { song ->
                            container.playerRepository.playSong(song, artist?.topSongs ?: listOf(song))
                        },
                        onSongMoreClick = { song -> actionSheetSong = song }
                    )
                }

                is Screen.AlbumDetail -> {
                    AlbumDetailScreen(
                        album = null,
                        onBack = { currentScreen = Screen.Home },
                        onSongClick = { song -> container.playerRepository.playSong(song) },
                        onSongMoreClick = { song -> actionSheetSong = song }
                    )
                }

                is Screen.Settings -> {
                    SettingsScreen(
                        currentUser = currentUser,
                        onBack = { currentScreen = Screen.Home },
                        onLogout = {
                            scope.launch {
                                container.authRepository.logout()
                                currentScreen = Screen.Auth
                            }
                        },
                        onOpenAuth = { currentScreen = Screen.Auth }
                    )
                }

                is Screen.Auth -> {
                    AuthScreen(
                        onLoginSuccess = { email, pass ->
                            scope.launch {
                                container.authRepository.login(email, pass)
                                currentScreen = Screen.Home
                            }
                        },
                        onRegisterSuccess = { user, email, pass ->
                            scope.launch {
                                container.authRepository.register(user, email, pass)
                            }
                        },
                        onGuestLogin = {
                            scope.launch {
                                container.authRepository.loginAsGuest()
                                currentScreen = Screen.Home
                            }
                        },
                        onFinishGenrePicker = { genres ->
                            scope.launch {
                                container.authRepository.savePreferredGenres(genres)
                                currentScreen = Screen.Home
                            }
                        }
                    )
                }
            }

            // Full-Screen NowPlaying Overlay with Slide Animation
            AnimatedVisibility(
                visible = isNowPlayingExpanded,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                NowPlayingScreen(
                    song = currentSong,
                    isPlaying = isPlaying,
                    currentPositionMs = currentPositionMs,
                    durationMs = durationMs,
                    isShuffle = isShuffle,
                    isRepeat = isRepeat,
                    onClose = { isNowPlayingExpanded = false },
                    onTogglePlayPause = { container.playerRepository.togglePlayPause() },
                    onSeekTo = { pos -> container.playerRepository.seekTo(pos) },
                    onSkipNext = { container.playerRepository.skipToNext() },
                    onSkipPrevious = { container.playerRepository.skipToPrevious() },
                    onToggleShuffle = { container.playerRepository.toggleShuffle() },
                    onToggleRepeat = { container.playerRepository.toggleRepeat() },
                    onToggleFavorite = { id ->
                        scope.launch { container.catalogRepository.toggleFavorite(id) }
                    },
                    onOpenQueue = { showQueueSheet = true },
                    onOpenSleepTimer = { showSleepTimerSheet = true },
                    onOpenLyrics = { showLyricsSheet = true }
                )
            }
        }
    }

    // Modal Sheets
    if (actionSheetSong != null) {
        SongActionSheet(
            song = actionSheetSong,
            onDismiss = { actionSheetSong = null },
            onToggleFavorite = { id -> scope.launch { container.catalogRepository.toggleFavorite(id) } },
            onAddToPlaylist = { id ->
                if (userPlaylists.isNotEmpty()) {
                    scope.launch { container.playlistRepository.addSongToPlaylist(userPlaylists.first().id, id) }
                }
            },
            onPlayNext = { song -> container.playerRepository.playSong(song) },
            onDownload = { song -> scope.launch { container.downloadRepository.enqueueDownload(song) } },
            onOpenSleepTimer = { showSleepTimerSheet = true },
            onViewArtist = { artistId -> currentScreen = Screen.ArtistDetail(artistId) },
            onViewAlbum = { albumId -> currentScreen = Screen.AlbumDetail(albumId) }
        )
    }

    if (showLyricsSheet) {
        LyricsSheet(
            song = currentSong,
            currentPositionMs = currentPositionMs,
            onDismiss = { showLyricsSheet = false }
        )
    }

    if (showSleepTimerSheet) {
        SleepTimerSheet(
            song = currentSong,
            config = sleepTimerConfig,
            onDismiss = { showSleepTimerSheet = false },
            onStartTimer = { mins, smooth -> container.playerRepository.setSleepTimer(mins, smooth) },
            onCancelTimer = { container.playerRepository.cancelSleepTimer() }
        )
    }

    if (showQueueSheet) {
        QueueSheet(
            currentSong = currentSong,
            queue = currentQueue,
            onDismiss = { showQueueSheet = false },
            onSongClick = { song -> container.playerRepository.playSong(song, currentQueue) },
            onRemoveFromQueue = { index -> container.playerRepository.removeFromQueue(index) },
            onClearQueue = { container.playerRepository.clearQueue() },
            onShuffleRemaining = { container.playerRepository.toggleShuffle() }
        )
    }
}
