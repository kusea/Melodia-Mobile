package com.melodia.app.data.remote.api

import com.melodia.app.data.local.security.TokenStoreInterface
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

object MelodiaApiClient {

    // 10.0.2.2 points to host localhost in Android Emulator
    var baseUrl: String = "http://10.0.2.2:3000/"

    private var tokenStore: TokenStoreInterface? = null

    fun initialize(tokenStore: TokenStoreInterface, customBaseUrl: String? = null) {
        this.tokenStore = tokenStore
        if (!customBaseUrl.isNullOrBlank()) {
            this.baseUrl = customBaseUrl
        }
    }

    private val authInterceptor = Interceptor { chain ->
        val requestBuilder = chain.request().newBuilder()
        val token = tokenStore?.accessToken
        if (!token.isNullOrBlank()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }
        requestBuilder.addHeader("Accept", "application/json")
        chain.proceed(requestBuilder.build())
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    val apiService: MelodiaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .build()
            .create(MelodiaApiService::class.java)
    }

    fun jsonRequestBody(jsonString: String): RequestBody {
        return jsonString.toRequestBody("application/json; charset=utf-8".toMediaType())
    }
}
