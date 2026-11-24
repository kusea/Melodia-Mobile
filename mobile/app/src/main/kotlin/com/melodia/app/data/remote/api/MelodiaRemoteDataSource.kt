package com.melodia.app.data.remote.api

import android.util.Log
import com.melodia.app.domain.model.Album
import com.melodia.app.domain.model.Artist
import com.melodia.app.domain.model.Playlist
import com.melodia.app.domain.model.Song
import com.melodia.app.domain.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class MelodiaRemoteDataSource(
    private val api: MelodiaApiService = MelodiaApiClient.apiService
) {

    private val tag = "MelodiaRemoteDataSource"

    suspend fun getHome(): Result<Triple<List<Song>, List<Artist>, List<Playlist>>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getHome()
            if (response.isSuccessful) {
                val bodyStr = response.body()?.string() ?: return@withContext Result.failure(Exception("Empty body"))
                val json = JSONObject(bodyStr)
                val songs = if (json.has("quickPicks")) NetworkJsonParser.parseSongList(json.getJSONArray("quickPicks")) else emptyList()
                val artists = if (json.has("popularArtists")) NetworkJsonParser.parseArtistList(json.getJSONArray("popularArtists")) else emptyList()
                val playlists = if (json.has("curatedPlaylists")) NetworkJsonParser.parsePlaylistList(json.getJSONArray("curatedPlaylists")) else emptyList()
                Result.success(Triple(songs, artists, playlists))
            } else {
                Result.failure(Exception("API Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.w(tag, "Failed to connect to backend: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getSong(id: String): Result<Song> = withContext(Dispatchers.IO) {
        try {
            val response = api.getSong(id)
            if (response.isSuccessful) {
                val bodyStr = response.body()?.string() ?: return@withContext Result.failure(Exception("Empty body"))
                Result.success(NetworkJsonParser.parseSong(JSONObject(bodyStr)))
            } else {
                Result.failure(Exception("Song not found: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun search(query: String): Result<List<Song>> = withContext(Dispatchers.IO) {
        try {
            val response = api.search(query = query, type = "song")
            if (response.isSuccessful) {
                val bodyStr = response.body()?.string() ?: return@withContext Result.failure(Exception("Empty body"))
                val json = JSONObject(bodyStr)
                val items = json.optJSONArray("items") ?: JSONArray()
                Result.success(NetworkJsonParser.parseSongList(items))
            } else {
                Result.failure(Exception("Search error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserPlaylists(): Result<List<Playlist>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getUserPlaylists()
            if (response.isSuccessful) {
                val bodyStr = response.body()?.string() ?: return@withContext Result.failure(Exception("Empty body"))
                Result.success(NetworkJsonParser.parsePlaylistList(JSONArray(bodyStr)))
            } else {
                Result.failure(Exception("Playlists error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, pass: String): Result<Pair<User, String>> = withContext(Dispatchers.IO) {
        try {
            val payload = JSONObject().apply {
                put("email", email)
                put("password", pass)
            }.toString()
            val response = api.login(MelodiaApiClient.jsonRequestBody(payload))
            if (response.isSuccessful) {
                val bodyStr = response.body()?.string() ?: return@withContext Result.failure(Exception("Empty response"))
                val json = JSONObject(bodyStr)
                val user = NetworkJsonParser.parseUser(json)
                val accessToken = json.optString("accessToken", "")
                Result.success(Pair(user, accessToken))
            } else {
                Result.failure(Exception("Invalid credentials: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleFavorite(songId: String, isFavorite: Boolean): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = if (isFavorite) {
                api.addFavorite(songId)
            } else {
                api.removeFavorite(songId)
            }
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}
