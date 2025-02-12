package com.example.agora.model.data

import java.util.Locale
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.*
import org.json.JSONObject

class Address private constructor(
    private var street: String = "",
    private var city: String = "",
    private var state: String = "",
    private var postalCode: String = "",
    private var country: String = ""
) {

    // Getters and Setters
    fun getStreet(): String = street
    fun setStreet(value: String) { street = value }

    fun getCity(): String = city
    fun setCity(value: String) { city = value }

    fun getState(): String = state
    fun setState(value: String) { state = value }

    fun getPostalCode(): String = postalCode
    fun setPostalCode(value: String) { postalCode = value }

    fun getCountry(): String = country
    fun setCountry(value: String) { country = value }



    companion object {
        // returns either the successfully made address, or a string error
        fun create(street: String, city: String, state: String, postalCode: String, country: String): Any {
            val countryCode: String? = getCountryCode(country) ?: return "Invalid Country"

            if (countryCode != "US" && countryCode != "CA") {
                return "Country must be United States or Canada"
            }

            if (!isValidPostalCode(postalCode, countryCode)) {
                return "Invalid postal or zip code"
            }

            return Address(street, city, state, postalCode, countryCode)
        }

        private fun isValidPostalCode(postalCode: String, countryCode: String): Boolean {
            val pattern = postalCodePatterns[countryCode] ?: return false
            return postalCode.matches(Regex(pattern))
        }

        private val postalCodePatterns = mapOf(
            "US" to "\\d{5}(-\\d{4})?",  // USA: 12345 or 12345-6789
            "CA" to "[A-Z]\\d[A-Z] \\d[A-Z]\\d", // Canada: A1A 1A1
        )

        private fun getCountryCode(countryName: String): String? {
            for (countryCode in Locale.getISOCountries()) {
                val locale = Locale("", countryCode)
                if (locale.displayCountry.equals(countryName, ignoreCase = true)) {
                    return countryCode
                }
            }
            return null // no country name found
        }
    }

    // Methods
    fun validateAddress(): Boolean? {
        // TODO: some of this is done in the companion object upon declaration, but we can add other
        // checks if desired
        return null
    }

    fun getFormattedAddress(): String? {
        return "$street, $city, $state, $country, $postalCode"
    }

    private fun getLatLng(postalCode: String, countryCode: String): Pair<Double, Double>? {
        val url = "http://api.zippopotam.us/$countryCode/$postalCode"
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.requestMethod = "GET"

        return if (connection.responseCode == 200) {
            val response = connection.inputStream.bufferedReader().readText()
            val json = JSONObject(response)
            val place = json.getJSONArray("places").getJSONObject(0)

            Pair(place.getDouble("latitude"), place.getDouble("longitude"))
        } else {
            null
        }
    }

    private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371 // Earth's radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return R * c // Distance in km
    }

    fun distanceTo(address: Address): Double {
        val selfLoc = getLatLng(postalCode, country)
        val otherLoc = getLatLng(address.getPostalCode(), address.getCountry())

        if (selfLoc != null && otherLoc != null) {
            return haversine(selfLoc.first, selfLoc.second, otherLoc.first, otherLoc.second)
        }

        return -1.0
    }
}