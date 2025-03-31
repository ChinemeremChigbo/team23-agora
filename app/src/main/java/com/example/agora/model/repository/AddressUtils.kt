package com.example.agora.model.repository

import android.util.Log
import com.example.agora.model.util.GeocodingClient
import com.example.agora.model.util.GeocodingService

class AddressUtils {
    companion object {
        suspend fun getGeocoding(address: String): GeocodingService.Result? {
            return try {
                val response = GeocodingClient.instance.getGeocoding(
                    address,
                    GeocodingClient.API_KEY
                )
                response.results.firstOrNull()
            } catch (e: Exception) {
                Log.e("Geocoding", "Error: ${e.message}")
                null
            }
        }
    }
}
