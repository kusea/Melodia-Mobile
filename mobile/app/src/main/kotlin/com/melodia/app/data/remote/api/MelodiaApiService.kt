package com.melodia.app.data.remote.api

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface MelodiaApiService {

    // --- Authentication ---
    @POST("v1/auth/register")
    suspend fun register(@Body body: RequestBody): Response<ResponseBody>

    @POST("v1/auth/login")
    suspend fun login(@Body body: RequestBody): Response<ResponseBody>

    @POST("v1/auth/refresh")
    suspend fun refreshToken(@Body body: RequestBody): Response<ResponseBody>

    @POST("v1/auth/logout")
    suspend fun logout(): Response<ResponseBody>

    // --- Discovery & Home ---
    @GET("v1/home")
    suspend fun getHome(): Response<ResponseBody>

    // --- Catalog ---
    @GET("v1/songs/{id}")
    suspend fun getSong(@Path("id") id: String): Response<ResponseBody>

    @GET("v1/songs/{id}/stream")
    suspend fun getSongStream(
        @Path("id") id: String,
        @Query("quality") quality: String = "lossless"
    ): Response<ResponseBody>

    @GET("v1/songs/{id}/lyrics")
    suspend fun getSongLyrics(@Path("id") id: String): Response<ResponseBody>

    @GET("v1/artists/{id}")
    suspend fun getArtist(@Path("id") id: String): Response<ResponseBody>

    @GET("v1/albums/{id}")
    suspend fun getAlbum(@Path("id") id: String): Response<ResponseBody>

    @GET("v1/search")
    suspend fun search(
        @Query("q") query: String,
        @Query("type") type: String = "all",
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ResponseBody>

    // --- Playlists ---
    @GET("v1/me/playlists")
    suspend fun getUserPlaylists(): Response<ResponseBody>

    @GET("v1/playlists/{id}")
    suspend fun getPlaylist(@Path("id") id: String): Response<ResponseBody>

    @POST("v1/playlists")
    suspend fun createPlaylist(@Body body: RequestBody): Response<ResponseBody>

    @PATCH("v1/playlists/{id}")
    suspend fun updatePlaylist(@Path("id") id: String, @Body body: RequestBody): Response<ResponseBody>

    @DELETE("v1/playlists/{id}")
    suspend fun deletePlaylist(@Path("id") id: String): Response<ResponseBody>

    @POST("v1/playlists/{id}/songs")
    suspend fun addSongToPlaylist(@Path("id") id: String, @Body body: RequestBody): Response<ResponseBody>

    @DELETE("v1/playlists/{id}/songs/{songId}")
    suspend fun removeSongFromPlaylist(@Path("id") id: String, @Path("songId") songId: String): Response<ResponseBody>

    // --- Library & Favorites ---
    @GET("v1/me/favorites")
    suspend fun getFavorites(): Response<ResponseBody>

    @PUT("v1/me/favorites/{songId}")
    suspend fun addFavorite(@Path("songId") songId: String): Response<ResponseBody>

    @DELETE("v1/me/favorites/{songId}")
    suspend fun removeFavorite(@Path("songId") songId: String): Response<ResponseBody>

    @GET("v1/me/history")
    suspend fun getHistory(): Response<ResponseBody>

    @POST("v1/me/history")
    suspend fun recordHistory(@Body body: RequestBody): Response<ResponseBody>

    // --- Offline Downloads ---
    @POST("v1/downloads")
    suspend fun requestDownloadLicense(@Body body: RequestBody): Response<ResponseBody>
}
