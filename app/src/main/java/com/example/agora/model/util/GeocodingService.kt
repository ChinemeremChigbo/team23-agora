package com.example.agora.model.util

import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingService {
    @GET("geocode/json")
    suspend fun getGeocoding(
        @Query("address") address: String,
        @Query("key") apiKey: String
    ): GeocodingResponse

    data class GeocodingResponse(
        val results: List<Result>,
        val status: String
    )

    data class Result(
        val formatted_address: String,
        val geometry: Geometry,
        val address_components: List<AddressComponent> // Add this field
    )

    data class Geometry(
        val location: Location
    )

    data class Location(
        val lat: Double,
        val lng: Double
    )

    data class AddressComponent(
        val long_name: String,
        val short_name: String,
        val types: List<String>
    )
}
