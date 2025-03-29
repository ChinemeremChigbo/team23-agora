package com.example.agora.model.repository

import android.util.Log
import com.example.agora.model.util.GeocodingClient
import com.example.agora.model.util.GeocodingService
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import okhttp3.OkHttpClient
import okhttp3.Request

@Serializable
data class ZippopotamResponse(
    val places: List<Place>
)

@Serializable
data class Place(
    @SerialName("latitude") val latitude: String,
    @SerialName("longitude") val longitude: String
)


class AddressUtils {
    companion object {
        suspend fun getGeocoding(address: String): GeocodingService.Result? {
            return try {
                val response = GeocodingClient.instance.getGeocoding(address, GeocodingClient.API_KEY)
                response.results.firstOrNull()

            } catch (e: Exception) {
                Log.e("Geocoding", "Error: ${e.message}")
                null
            }
        }

        suspend fun getLatLongForPostalCode(countryCode: String, postalCode: String): Pair<Double, Double>? {
            val client = OkHttpClient()
            val url = "http://api.zippopotam.us/$countryCode/$postalCode"
            val request = Request.Builder().url(url).build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    println("Request failed: ${response.code}")
                    return null
                }

                val body = response.body?.string() ?: return null
                val json = Json.decodeFromString<ZippopotamResponse>(body)

                val place = json.places.firstOrNull() ?: return null
                return place.latitude.toDouble() to place.longitude.toDouble()
            }
        }
    }
}