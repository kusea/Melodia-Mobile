package com.melodia.app.presentation.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.melodia.app.domain.model.LyricLine
import com.melodia.app.domain.model.Song
import com.melodia.app.presentation.theme.CyanAccent
import com.melodia.app.presentation.theme.DarkBackground
import com.melodia.app.presentation.theme.NeonViolet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LyricsSheet(
    song: Song?,
    currentPositionMs: Long,
    onDismiss: () -> Unit
) {
    if (song == null) return

    val lyricsList = remember(song.lyrics) {
        parseLrc(song.lyrics ?: "")
    }

    val activeIndex = remember(currentPositionMs, lyricsList) {
        lyricsList.indexOfLast { currentPositionMs >= it.timestampMs }
    }

    val listState = rememberLazyListState()

    LaunchedEffect(activeIndex) {
        if (activeIndex >= 0) {
            listState.animateScrollToItem(activeIndex)
        }
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "LYRICS SYNCED",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent
                        )
                    )
                    Text(
                        text = song.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${song.artist} • ${song.album}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Lyrics List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                itemsIndexed(lyricsList) { index, line ->
                    val isActive = index == activeIndex
                    val isPast = index < activeIndex

                    val textColor = when {
                        isActive -> Color.White
                        isPast -> MaterialTheme.colorScheme.onSurfaceVariant
                        else -> Color(0xFF555268)
                    }

                    val fontSize = if (isActive) 24.sp else 18.sp
                    val fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Medium

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 14.dp)
                    ) {
                        if (isActive) {
                            Text(
                                text = "NOW SINGING",
                                color = CyanAccent,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                modifier = Modifier
                                    .background(CyanAccent.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        Text(
                            text = line.text,
                            color = textColor,
                            fontSize = fontSize,
                            fontWeight = fontWeight,
                            lineHeight = if (isActive) 32.sp else 24.sp
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "Lyrics provided by Musixmatch Verified Sync\n${song.artist} • ${song.title}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
                    )
                }
            }
        }
    }
}

private fun parseLrc(lrcText: String): List<LyricLine> {
    val lines = mutableListOf<LyricLine>()
    val pattern = Regex("""\[(\d{2}):(\d{2})\.(\d{2})]""")

    lrcText.lines().forEach { line ->
        val match = pattern.find(line)
        if (match != null) {
            val min = match.groupValues[1].toLongOrNull() ?: 0L
            val sec = match.groupValues[2].toLongOrNull() ?: 0L
            val ms = (match.groupValues[3].toLongOrNull() ?: 0L) * 10
            val timestamp = (min * 60 + sec) * 1000 + ms
            val text = line.substring(match.range.last + 1).trim()
            if (text.isNotBlank()) {
                lines.add(LyricLine(timestamp, text))
            }
        }
    }
    return lines.sortedBy { it.timestampMs }
}
