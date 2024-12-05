package com.example.eventsproject.network

import android.content.Context
import com.example.eventsproject.utils.PreferenceManager
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private var _apiService: ApiService? = null

    fun initialize(context: Context) {
        val tokenInterceptor = Interceptor { chain ->
            val originalRequest: Request = chain.request()

            // Retrieve token from PreferenceManager
            val token = PreferenceManager.getLoginResponse(context)?.let { json ->
                Gson().fromJson(json, LoginResponse::class.java)?.token
            }

            // Add Authorization header if token is available
            val requestBuilder = originalRequest.newBuilder()
            if (token != null) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }

            val requestWithHeaders = requestBuilder.build()
            chain.proceed(requestWithHeaders)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(tokenInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/api/") // Replace with your backend base URL
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        _apiService = retrofit.create(ApiService::class.java)
    }

    val apiService: ApiService
        get() = _apiService ?: throw IllegalStateException("RetrofitClient is not initialized. Call initialize(context) first.")
}
