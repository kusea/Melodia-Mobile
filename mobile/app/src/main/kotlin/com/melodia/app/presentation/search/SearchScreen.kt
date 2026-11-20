package com.melodia.app.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.melodia.app.R
import com.melodia.app.domain.model.Album
import com.melodia.app.domain.model.Artist
import com.melodia.app.domain.model.Playlist
import com.melodia.app.domain.model.Song
import com.melodia.app.presentation.common.SongItemRow
import com.melodia.app.presentation.theme.CyanAccent
import com.melodia.app.presentation.theme.DarkBackground
import com.melodia.app.presentation.theme.DarkSurfaceElevated
import com.melodia.app.presentation.theme.HiResGold
import com.melodia.app.presentation.theme.NeonViolet
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onSongClick: (Song) -> Unit,
    onSongMoreClick: (Song) -> Unit,
    onArtistClick: (String) -> Unit,
    onAlbumClick: (String) -> Unit,
    searchSongs: (String) -> List<Song>,
    searchArtists: (String) -> List<Artist>,
    searchAlbums: (String) -> List<Album>,
    searchPlaylists: (String) -> List<Playlist>,
    modifier: Modifier = Modifier
) {
    var rawQuery by remember { mutableStateOf("") }
    var debouncedQuery by remember { mutableStateOf("") }
    var selectedFilterTab by remember { mutableStateOf("Top") }

    // 300ms Debounce search as specified in NFR-02 & UC-04
    LaunchedEffect(rawQuery) {
        delay(300)
        debouncedQuery = rawQuery
    }

    val matchedSongs = remember(debouncedQuery) { searchSongs(debouncedQuery) }
    val matchedArtists = remember(debouncedQuery) { searchArtists(debouncedQuery) }
    val matchedAlbums = remember(debouncedQuery) { searchAlbums(debouncedQuery) }

    val recentSearches = remember {
        listOf("Neon Nights", "Indie Acoustic", "Billie Eilish", "Kavinsky", "Aurora Waves")
    }

    val categories = listOf(
        "Pop" to Color(0xFF673AB7),
        "Hip-Hop" to Color(0xFFE91E63),
        "Lo-Fi Chill" to Color(0xFF009688),
        "Electronic & Dance" to Color(0xFF3F51B5),
        "Chill & Study" to Color(0xFF4CAF50),
        "R&B & Soul" to Color(0xFFFF9800),
        "Rock & Alternative" to Color(0xFFF44336),
        "Indie" to Color(0xFF9C27B0)
    )

    Surface(
        modifier = modifier.fillMaxSize(),
        color = DarkBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)
        ) {
            // Search Input Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    text = stringResource(R.string.nav_search),
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = rawQuery,
                    onValueChange = { rawQuery = it },
                    placeholder = { Text(stringResource(R.string.search_placeholder)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        if (rawQuery.isNotEmpty()) {
                            IconButton(onClick = { rawQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkSurfaceElevated,
                        unfocusedContainerColor = DarkSurfaceElevated,
                        focusedBorderColor = NeonViolet,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    singleLine = true
                )
            }

            // Results vs Discovery Content
            if (rawQuery.isBlank()) {
                // Browse & Recent Searches (Figma Page 4)
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.recent_searches),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    letterSpacing = 1.2.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(recentSearches) { term ->
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = DarkSurfaceElevated,
                                    modifier = Modifier.clickable { rawQuery = term }
                                ) {
                                    Text(
                                        text = term,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(28.dp))
                    }

                    item {
                        Text(
                            text = stringResource(R.string.browse_all),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    items(categories.chunked(2)) { pair ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            pair.forEach { (title, color) ->
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(90.dp)
                                        .clickable { rawQuery = title },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.85f))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(12.dp),
                                        contentAlignment = Alignment.BottomStart
                                    ) {
                                        Text(
                                            text = title,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (matchedSongs.isEmpty() && matchedArtists.isEmpty() && matchedAlbums.isEmpty()) {
                // Empty Search State (Figma Page 11)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_results_found, rawQuery),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.no_results_tips),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    OutlinedButton(
                        onClick = { rawQuery = "" },
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("Clear Search")
                    }
                }
            } else {
                // Search Results Active (Figma Page 7)
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    // Filter tabs
                    item {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(listOf("Top", "Songs", "Artists", "Albums")) { tab ->
                                val isSelected = selectedFilterTab == tab
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedFilterTab = tab },
                                    label = { Text(tab) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = NeonViolet,
                                        selectedLabelColor = Color.White,
                                        containerColor = DarkSurfaceElevated
                                    ),
                                    shape = RoundedCornerShape(20.dp)
                                )
                            }
                        }
                    }

                    // Best Match card
                    if (matchedSongs.isNotEmpty() && selectedFilterTab in listOf("Top", "Songs")) {
                        item {
                            val topSong = matchedSongs.first()
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 8.dp)
                                    .clickable { onSongClick(topSong) },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = topSong.coverUrl,
                                        contentDescription = topSong.title,
                                        modifier = Modifier
                                            .size(72.dp)
                                            .clip(RoundedCornerShape(12.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "BEST MATCH",
                                            color = CyanAccent,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = topSong.title,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${topSong.artist} • Song",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    // Songs Results
                    if (selectedFilterTab in listOf("Top", "Songs")) {
                        items(matchedSongs) { song ->
                            SongItemRow(
                                song = song,
                                onSongClick = { onSongClick(song) },
                                onMoreClick = { onSongMoreClick(song) }
                            )
                        }
                    }

                    // Artists Results
                    if (matchedArtists.isNotEmpty() && selectedFilterTab in listOf("Top", "Artists")) {
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Artists",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                            )
                        }
                        items(matchedArtists) { artist ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onArtistClick(artist.id) }
                                    .padding(horizontal = 20.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = artist.imageUrl,
                                    contentDescription = artist.name,
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(RoundedCornerShape(26.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = artist.name,
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${artist.monthlyListeners} monthly listeners",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
