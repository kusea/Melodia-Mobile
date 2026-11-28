package com.melodia.app.data.remote.api

import com.melodia.app.domain.model.Album
import com.melodia.app.domain.model.Artist
import com.melodia.app.domain.model.Playlist
import com.melodia.app.domain.model.Song
import com.melodia.app.domain.model.User
import org.json.JSONArray
import org.json.JSONObject

object NetworkJsonParser {

    fun parseUser(json: JSONObject): User {
        val userObj = if (json.has("user")) json.getJSONObject("user") else json
        val genres = mutableListOf<String>()
        if (userObj.has("selectedGenres")) {
            val arr = userObj.getJSONArray("selectedGenres")
            for (i in 0 until arr.length()) {
                genres.add(arr.getString(i))
            }
        }
        return User(
            id = userObj.optString("id", "u_unknown"),
            email = userObj.optString("email", ""),
            displayName = userObj.optString("displayName", userObj.optString("display_name", "User")),
            avatarUrl = userObj.optString("avatarUrl", userObj.optString("avatar_url", "")),
            isGuest = userObj.optBoolean("isGuest", userObj.optBoolean("is_guest", false)),
            preferredGenres = genres
        )
    }

    fun parseSong(obj: JSONObject): Song {
        return Song(
            id = obj.optString("id", ""),
            title = obj.optString("title", "Unknown Title"),
            artist = obj.optString("artist_name", obj.optString("artist", "Unknown Artist")),
            artistId = obj.optString("artist_id", "art_unknown"),
            album = obj.optString("album_title", obj.optString("album", "Single")),
            albumId = obj.optString("album_id", "alb_unknown"),
            durationSec = obj.optInt("duration_sec", obj.optInt("durationSec", 180)),
            audioUrl = obj.optString("stream_url", obj.optString("audioUrl", "")),
            coverUrl = obj.optString("cover_url", obj.optString("coverUrl", "")),
            isLossless = obj.optBoolean("is_lossless", obj.optBoolean("isLossless", true)),
            bitrateKbps = obj.optInt("bitrate_kbps", obj.optInt("bitrateKbps", 320)),
            isDownloaded = obj.optBoolean("is_downloaded", false),
            isFavorite = obj.optBoolean("is_favorite", obj.optBoolean("isFavorite", false)),
            lyrics = obj.optString("lyrics_lrc", null),
            playCount = obj.optLong("play_count", 0L)
        )
    }

    fun parseSongList(arr: JSONArray): List<Song> {
        val list = mutableListOf<Song>()
        for (i in 0 until arr.length()) {
            list.add(parseSong(arr.getJSONObject(i)))
        }
        return list
    }

    fun parseArtist(obj: JSONObject): Artist {
        return Artist(
            id = obj.optString("id", ""),
            name = obj.optString("name", "Unknown Artist"),
            bio = obj.optString("bio", ""),
            imageUrl = obj.optString("image_url", obj.optString("imageUrl", "")),
            monthlyListeners = obj.optString("monthly_listeners", obj.optString("monthlyListeners", "100K")),
            isVerified = obj.optBoolean("is_verified", obj.optBoolean("isVerified", true))
        )
    }

    fun parseArtistList(arr: JSONArray): List<Artist> {
        val list = mutableListOf<Artist>()
        for (i in 0 until arr.length()) {
            list.add(parseArtist(arr.getJSONObject(i)))
        }
        return list
    }

    fun parseAlbum(obj: JSONObject): Album {
        val yearFallback = obj.optString("release_date", "2026").take(4).toIntOrNull() ?: 2026
        return Album(
            id = obj.optString("id", ""),
            title = obj.optString("title", "Untitled Album"),
            artist = obj.optString("artist_name", obj.optString("artist", "Various Artists")),
            artistId = obj.optString("artist_id", ""),
            coverUrl = obj.optString("cover_url", obj.optString("coverUrl", "")),
            releaseYear = obj.optInt("release_year", yearFallback),
            trackCount = obj.optInt("track_count", obj.optInt("song_count", 0)),
            durationSec = obj.optInt("duration_sec", obj.optInt("durationSec", 0)),
            recordLabel = obj.optString("record_label", obj.optString("recordLabel", "Melodia Records"))
        )
    }

    fun parseAlbumList(arr: JSONArray): List<Album> {
        val list = mutableListOf<Album>()
        for (i in 0 until arr.length()) {
            list.add(parseAlbum(arr.getJSONObject(i)))
        }
        return list
    }

    fun parsePlaylist(obj: JSONObject): Playlist {
        val songs = if (obj.has("songs")) parseSongList(obj.getJSONArray("songs")) else emptyList()
        return Playlist(
            id = obj.optString("id", ""),
            name = obj.optString("name", "New Playlist"),
            description = obj.optString("description", ""),
            coverUrl = obj.optString("cover_url", obj.optString("coverUrl", "")),
            creatorName = obj.optString("creatorName", "You"),
            isPublic = obj.optBoolean("is_public", obj.optBoolean("isPublic", true)),
            songCount = obj.optInt("song_count", songs.size),
            songs = songs
        )
    }

    fun parsePlaylistList(arr: JSONArray): List<Playlist> {
        val list = mutableListOf<Playlist>()
        for (i in 0 until arr.length()) {
            list.add(parsePlaylist(arr.getJSONObject(i)))
        }
        return list
    }
}
