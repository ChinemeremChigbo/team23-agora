package com.example.agora.model.util

import com.example.agora.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object BrevoClient {
    private const val API_KEY = BuildConfig.BREVO_API_KEY

    private val client = OkHttpClient.Builder().addInterceptor { chain: Interceptor.Chain ->
        val original: Request = chain.request()
        val request =
            original.newBuilder().header("api-key", API_KEY).method(original.method, original.body)
                .build()
        chain.proceed(request)
    }.build()

    private val retrofit = Retrofit.Builder().baseUrl("https://api.brevo.com/v3/").client(client)
        .addConverterFactory(GsonConverterFactory.create()).build()

    val service: BrevoApiService = retrofit.create(BrevoApiService::class.java)
}
