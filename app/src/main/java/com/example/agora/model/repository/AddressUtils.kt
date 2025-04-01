package com.example.agora.model.repository

import android.util.Log
import com.example.agora.model.util.GeocodingClient
import com.example.agora.model.util.GeocodingService

class AddressUtils {
    companion object {
        suspend fun getGeocoding(address: String): GeocodingService.Result? {
            val response = GeocodingClient.instance.getGeocoding(address, GeocodingClient.API_KEY)
            return response.results.firstOrNull()
        }
    }
}
