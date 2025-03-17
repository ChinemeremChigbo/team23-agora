package com.example.agora.model.util

import retrofit2.http.GET
import retrofit2.http.Path

interface ZippopotamApi {
    @GET("{countryCode}/{postalCode}")
    suspend fun getLocation(
        @Path("countryCode") countryCode: String,
        @Path("postalCode") postalCode: String
    ): LocationResponse
}

data class LocationResponse(
    val country: String,
    val places: List<Place>
)

data class Place(
    val latitude: String,
    val longitude: String
)