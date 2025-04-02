package com.example.agora.model.repository

import com.example.agora.model.data.Address
import com.example.agora.model.data.Address.Companion.convertDBEntryToAddress
import com.example.agora.model.util.GeocodingClient
import com.example.agora.model.util.GeocodingService
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AddressUtils {
    companion object {
        suspend fun getGeocoding(address: String): GeocodingService.Result? {
            val response = GeocodingClient.instance.getGeocoding(address, GeocodingClient.API_KEY)
            return response.results.firstOrNull()
        }

        suspend fun getUserAddress(userId: String): Address? = suspendCoroutine { continuation ->
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(userId).get().addOnSuccessListener { document ->
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
            }.addOnFailureListener {
                continuation.resume(null)
            }
        }
    }
}
