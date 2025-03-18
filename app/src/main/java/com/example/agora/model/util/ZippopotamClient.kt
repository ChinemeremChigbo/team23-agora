package com.example.agora.model.util

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ZippopotamClient {
    private const val BASE_URL = "http://api.zippopotam.us/"

    val instance: ZippopotamApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ZippopotamApi::class.java)
    }
}