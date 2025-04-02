package com.example.agora.model.repository

import com.example.agora.model.data.Address
import com.example.agora.model.data.Address.Companion.convertDBEntryToAddress
import com.example.agora.model.util.GeocodingClient
import com.example.agora.model.util.GeocodingService
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
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
            val response = GeocodingClient.instance.getGeocoding(address, GeocodingClient.API_KEY)
            return response.results.firstOrNull()
        }

        suspend fun getLatLongForPostalCode(
            countryCode: String,
            postalCode: String
        ): Pair<Double, Double>? {
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

        suspend fun getUserAddress(userId: String): Address? = suspendCoroutine { continuation ->
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    val data = document.data

                    val addressMap = data?.get("address") as? Map<String, Any>
                    if (addressMap != null && addressMap.containsKey("lat") && addressMap.containsKey(
                            "lng"
                        )
                    ) {
                        val address = convertDBEntryToAddress(addressMap)
                        continuation.resume(address)
                    } else {
                        continuation.resume(null)
                    }
                }
                .addOnFailureListener {
                    continuation.resume(null)
                }
        }
    }
}
