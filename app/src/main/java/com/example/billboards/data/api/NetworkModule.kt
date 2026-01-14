package com.example.billboards.data.api

import com.example.billboards.data.storage.TokenStore
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {

    fun createApi(baseUrl: String, tokenStore: TokenStore): BillboardApi {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val authInterceptor = Interceptor { chain ->
            val token = tokenStore.getTokenBlocking()
            val req = chain.request().newBuilder()
                .apply {
                    if (!token.isNullOrBlank()) {
                        header("Authorization", "Bearer $token")
                    }
                }
                .build()
            chain.proceed(req)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl) // ex: "https://YOUR_MOCKAPI_BASE_URL/"
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofit.create(BillboardApi::class.java)
    }
}
