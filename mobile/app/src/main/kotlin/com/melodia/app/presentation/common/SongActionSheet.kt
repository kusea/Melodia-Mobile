package com.melodia.app.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.melodia.app.R
import com.melodia.app.domain.model.Song
import com.melodia.app.presentation.theme.DarkSurface
import com.melodia.app.presentation.theme.HiResGold
import com.melodia.app.presentation.theme.NeonViolet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongActionSheet(
    song: Song?,
    onDismiss: () -> Unit,
    onToggleFavorite: (String) -> Unit,
    onAddToPlaylist: (String) -> Unit,
    onPlayNext: (Song) -> Unit,
    onDownload: (Song) -> Unit,
    onOpenSleepTimer: () -> Unit,
    onViewArtist: (String) -> Unit,
    onViewAlbum: (String) -> Unit
) {
    if (song == null) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                AsyncImage(
                    model = song.coverUrl,
                    contentDescription = song.title,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = song.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${song.artist} • ${song.album}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Hi-Res FLAC 24-bit • 192kHz",
                        color = HiResGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))

            // Action Items
            ActionRowItem(
                icon = if (song.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                title = stringResource(R.string.action_like),
                iconTint = if (song.isFavorite) NeonViolet else MaterialTheme.colorScheme.onSurfaceVariant,
                onClick = {
                    onToggleFavorite(song.id)
                    onDismiss()
                }
            )

            ActionRowItem(
                icon = Icons.AutoMirrored.Filled.PlaylistAdd,
                title = stringResource(R.string.action_add_playlist),
                onClick = {
                    onAddToPlaylist(song.id)
                    onDismiss()
                }
            )

            ActionRowItem(
                icon = Icons.AutoMirrored.Filled.QueueMusic,
                title = stringResource(R.string.action_play_next),
                onClick = {
                    onPlayNext(song)
                    onDismiss()
                }
            )

            ActionRowItem(
                icon = Icons.Default.Download,
                title = stringResource(R.string.action_download),
                onClick = {
                    onDownload(song)
                    onDismiss()
                }
            )

            ActionRowItem(
                icon = Icons.Default.Album,
                title = stringResource(R.string.action_view_album),
                onClick = {
                    onViewAlbum(song.albumId)
                    onDismiss()
                }
            )

            ActionRowItem(
                icon = Icons.Default.Person,
                title = stringResource(R.string.action_view_artist),
                onClick = {
                    onViewArtist(song.artistId)
                    onDismiss()
                }
            )

            ActionRowItem(
                icon = Icons.Default.Timer,
                title = stringResource(R.string.sleep_timer),
                onClick = {
                    onDismiss()
                    onOpenSleepTimer()
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ActionRowItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    iconTint: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
