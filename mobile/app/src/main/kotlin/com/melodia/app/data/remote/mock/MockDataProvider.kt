package com.melodia.app.data.remote.mock

import com.melodia.app.domain.model.Album
import com.melodia.app.domain.model.Artist
import com.melodia.app.domain.model.Playlist
import com.melodia.app.domain.model.Song

object MockDataProvider {

    val sampleLrcLyrics = """
        [00:00.00]Melodia Hi-Res Master Stream
        [00:04.00]Neon shadows falling on the empty boulevard...
        [00:15.00]We run faster than the speed of twilight dreams...
        [00:24.00]Electric city whispers through the shattered glass...
        [00:42.00]Lost in the echoes of a midnight frequency
        [01:02.00]Can you feel the pulse across the wire?
        [01:24.00]Tomorrow is only an electric memory...
        [01:45.00]Underneath the static waves we collide.
        [02:10.00]Reaching for constellations in the dark...
        [02:32.00]Until the morning washes all our colors out.
    """.trimIndent()

    // High quality royalty-free synthwave audio stream
    private const val DEFAULT_STREAM_URL = "https://commondatastorage.googleapis.com/codeskulptor-demos/DDR_assets/Sevish_-__nbsp_.mp3"
    private const val STREAM_URL_2 = "https://commondatastorage.googleapis.com/codeskulptor-assets/Epoq-Lepidoptera.ogg"
    private const val STREAM_URL_3 = "https://cdn.pixabay.com/download/audio/2022/05/27/audio_1808fbf07a.mp3?filename=synthwave-80s-110045.mp3"

    val songs = listOf(
        Song(
            id = "s_01",
            title = "Midnight Echoes",
            artist = "Aurora Waves",
            artistId = "a_01",
            album = "Neon Odyssey",
            albumId = "al_01",
            durationSec = 222,
            audioUrl = DEFAULT_STREAM_URL,
            coverUrl = "https://picsum.photos/seed/midnight/400/400",
            isLossless = true,
            isDownloaded = true,
            isFavorite = true,
            lyrics = sampleLrcLyrics,
            playCount = 18419204L
        ),
        Song(
            id = "s_02",
            title = "Starlight Drift",
            artist = "Aurora Waves",
            artistId = "a_01",
            album = "Neon Odyssey",
            albumId = "al_01",
            durationSec = 252,
            audioUrl = STREAM_URL_2,
            coverUrl = "https://picsum.photos/seed/starlight/400/400",
            isLossless = true,
            isDownloaded = true,
            isFavorite = false,
            lyrics = sampleLrcLyrics,
            playCount = 12108443L
        ),
        Song(
            id = "s_03",
            title = "Solar Flares",
            artist = "Aurora Waves",
            artistId = "a_01",
            album = "Neon Odyssey",
            albumId = "al_01",
            durationSec = 225,
            audioUrl = STREAM_URL_3,
            coverUrl = "https://picsum.photos/seed/solar/400/400",
            isLossless = true,
            isDownloaded = true,
            isFavorite = true,
            lyrics = sampleLrcLyrics,
            playCount = 9832110L
        ),
        Song(
            id = "s_04",
            title = "Neon Horizon (feat. Synth Kid)",
            artist = "Carpenter Brut",
            artistId = "a_02",
            album = "Trilogy",
            albumId = "al_02",
            durationSec = 252,
            audioUrl = DEFAULT_STREAM_URL,
            coverUrl = "https://picsum.photos/seed/horizon/400/400",
            isLossless = false,
            bitrateKbps = 320,
            isDownloaded = true,
            isFavorite = false,
            playCount = 6240900L
        ),
        Song(
            id = "s_05",
            title = "City of Neon Shadows",
            artist = "Gunship",
            artistId = "a_03",
            album = "UNICORN",
            albumId = "al_03",
            durationSec = 302,
            audioUrl = STREAM_URL_2,
            coverUrl = "https://picsum.photos/seed/gunship/400/400",
            isLossless = true,
            isDownloaded = true,
            isFavorite = true,
            playCount = 8904120L
        ),
        Song(
            id = "s_06",
            title = "Late Night Neon",
            artist = "Trevor Something",
            artistId = "a_04",
            album = "Souls",
            albumId = "al_04",
            durationSec = 208,
            audioUrl = STREAM_URL_3,
            coverUrl = "https://picsum.photos/seed/trevor/400/400",
            isLossless = false,
            bitrateKbps = 320,
            isDownloaded = false,
            isFavorite = false,
            playCount = 4210000L
        ),
        Song(
            id = "s_07",
            title = "Blinding Lights",
            artist = "The Weeknd",
            artistId = "a_05",
            album = "After Hours (Deluxe)",
            albumId = "al_05",
            durationSec = 200,
            audioUrl = DEFAULT_STREAM_URL,
            coverUrl = "https://picsum.photos/seed/weeknd/400/400",
            isLossless = true,
            isDownloaded = false,
            isFavorite = true,
            playCount = 3840291000L
        ),
        Song(
            id = "s_08",
            title = "Save Your Tears",
            artist = "The Weeknd",
            artistId = "a_05",
            album = "After Hours (Deluxe)",
            albumId = "al_05",
            durationSec = 215,
            audioUrl = STREAM_URL_2,
            coverUrl = "https://picsum.photos/seed/tears/400/400",
            isLossless = true,
            isDownloaded = false,
            isFavorite = true,
            playCount = 2100500000L
        ),
        Song(
            id = "s_09",
            title = "Cyber City Run",
            artist = "Dance With The Dead",
            artistId = "a_06",
            album = "Driven to Madness",
            albumId = "al_06",
            durationSec = 258,
            audioUrl = STREAM_URL_3,
            coverUrl = "https://picsum.photos/seed/dwtd/400/400",
            isLossless = true,
            isDownloaded = true,
            isFavorite = false,
            playCount = 5120000L
        )
    )

    val artists = listOf(
        Artist(
            id = "a_01",
            name = "Aurora Waves",
            bio = "Aurora Waves is a nocturnal electronic project blending analogue modular synthesis with cinematic dream-pop soundscapes. Driven by melancholic chord progressions and high-fidelity textures.",
            imageUrl = "https://picsum.photos/seed/aurora/400/400",
            monthlyListeners = "1.2M",
            isVerified = true,
            isFollowed = true,
            topSongs = songs.filter { it.artistId == "a_01" }
        ),
        Artist(
            id = "a_05",
            name = "The Weeknd",
            bio = "Canadian singer, songwriter, and record producer known for his sonic versatility and dark lyricism, exploring escapism, romance, and melancholia.",
            imageUrl = "https://picsum.photos/seed/weeknd_art/400/400",
            monthlyListeners = "108.4M",
            isVerified = true,
            isFollowed = true,
            topSongs = songs.filter { it.artistId == "a_05" }
        ),
        Artist(
            id = "a_02",
            name = "Carpenter Brut",
            bio = "French synthwave and darksynth artist combining 80s movie atmospheres, metal riffs, and industrial electronic beats.",
            imageUrl = "https://picsum.photos/seed/brut/400/400",
            monthlyListeners = "845K",
            isVerified = true,
            isFollowed = true,
            topSongs = songs.filter { it.artistId == "a_02" }
        ),
        Artist(
            id = "a_03",
            name = "Gunship",
            bio = "British synthwave band formed in 2014 by Dan Haigh, Alex Westaway, and Alex Gingell. Known for deep cinematic cyberpunk narratives.",
            imageUrl = "https://picsum.photos/seed/gunship_art/400/400",
            monthlyListeners = "912K",
            isVerified = true,
            isFollowed = true,
            topSongs = songs.filter { it.artistId == "a_03" }
        )
    )

    val albums = listOf(
        Album(
            id = "al_01",
            title = "Neon Odyssey",
            artist = "Aurora Waves",
            artistId = "a_01",
            coverUrl = "https://picsum.photos/seed/odyssey/400/400",
            releaseYear = 2024,
            trackCount = 12,
            durationSec = 2840,
            recordLabel = "Melodia Sound Studio",
            songs = songs.filter { it.albumId == "al_01" }
        ),
        Album(
            id = "al_05",
            title = "After Hours (Deluxe)",
            artist = "The Weeknd",
            artistId = "a_05",
            coverUrl = "https://picsum.photos/seed/afterhours/400/400",
            releaseYear = 2020,
            trackCount = 17,
            durationSec = 3360,
            recordLabel = "XO / Republic Records",
            songs = songs.filter { it.albumId == "al_05" }
        )
    )

    val playlists = listOf(
        Playlist(
            id = "pl_01",
            name = "Synthwave Dreams",
            description = "Curated by Alex Rivera • 48 songs • 3 hr 12 min of retro-futuristic synth lines and driving basslines.",
            coverUrl = "https://picsum.photos/seed/synthwave/400/400",
            creatorName = "Alex Rivera",
            isPublic = true,
            isAutoSync = true,
            songCount = 48,
            durationSec = 11520,
            songs = songs
        ),
        Playlist(
            id = "pl_02",
            name = "Cyberpunk Basslines",
            description = "Heavy distorted synth bass and dark outrun tracks for intense focus and coding sessions.",
            coverUrl = "https://picsum.photos/seed/cyber/400/400",
            creatorName = "Melodia Studio",
            isPublic = true,
            isAutoSync = true,
            songCount = 65,
            durationSec = 14200,
            songs = songs.reversed()
        ),
        Playlist(
            id = "pl_03",
            name = "Chill Lo-Fi Vibes",
            description = "Smooth ambient jazz chords and vinyl crackle to keep you company late at night.",
            coverUrl = "https://picsum.photos/seed/lofi/400/400",
            creatorName = "You",
            isPublic = false,
            isAutoSync = false,
            songCount = 35,
            durationSec = 6800,
            songs = songs.take(4)
        )
    )
}
