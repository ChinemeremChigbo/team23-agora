package com.chinemerem.agora.model.util

import com.chinemerem.agora.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GeocodingClient {
    private const val BASE_URL = "https://maps.googleapis.com/maps/api/"
    const val API_KEY = BuildConfig.MAPS_API_KEY
    val instance: GeocodingService by lazy {
        Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
            .build().create(GeocodingService::class.java)
    }
}
