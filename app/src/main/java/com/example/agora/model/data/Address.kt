package com.example.agora.model.data

import com.example.agora.model.repository.AddressRepository
import com.google.android.gms.maps.model.LatLng
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class Address constructor(
    private var street: String = "",
    private var city: String = "",
    private var state: String = "",
    private var postalCode: String = "",
    private var country: String = "",
    private var latLng: LatLng = LatLng(-1.0, -1.0)
) {

    // Getters and Setters
    fun getStreet(): String = street
    fun setStreet(value: String) {
        street = value
    }

    fun getCity(): String = city
    fun setCity(value: String) {
        city = value
    }

    fun getState(): String = state
    fun setState(value: String) {
        state = value
    }

    fun getPostalCode(): String = postalCode
    fun setPostalCode(value: String) {
        postalCode = value
    }

    fun getCountry(): String = country
    fun setCountry(value: String) {
        country = value
    }

    fun getLatLng(): LatLng = latLng

    suspend fun validateAndParseAddress(): Boolean {
        val result = AddressRepository.getGeocoding("$street, $city, $state")
        val location = result?.geometry?.location ?: return false
        // validate address and populate lat & lng info
        latLng = LatLng(location.lat, location.lng)
        val parsedPostalCode =
            result.address_components.find { "postal_code" in it.types }?.long_name
        // validate correct postal code
        return postalCode == parsedPostalCode
    }

    companion object {
        // returns either the successfully made address, or a string error
        fun create(
            street: String,
            city: String,
            state: String,
            postalCode: String,
            country: String,
            latLng: LatLng
        ): Address? {
            val countryCode: String? = getCountryCode(country)

            if (countryCode != "US" && countryCode != "CA") {
                println("invalid country code $countryCode from $country")
                return null
            }

            if (!isValidPostalCode(postalCode, countryCode)) {
                println("invalid postal code $postalCode")
                return null
            }
            return Address(street, city, state, postalCode, country, latLng)
        }

        suspend fun createAndValidate(
            street: String,
            city: String,
            state: String,
            postalCode: String,
            country: String
        ): Address? {
            val currAddress = Address(street, city, state, postalCode, country)
            val result = currAddress.validateAndParseAddress()
            if (!result) {
                return null
            }
            return currAddress
        }

        private fun isValidPostalCode(postalCode: String, countryCode: String): Boolean {
            val pattern = postalCodePatterns[countryCode] ?: return false
            return postalCode.matches(Regex(pattern))
        }

        private val postalCodePatterns = mapOf(
            "US" to "\\d{5}(-\\d{4})?", // USA: 12345 or 12345-6789
            "CA" to "[A-Z]\\d[A-Z] \\d[A-Z]\\d" // Canada: A1A 1A1
        )

        fun getCountryCode(countryName: String): String? {
            for (countryCode in Locale.getISOCountries()) {
                val locale = Locale("", countryCode)
                if (locale.displayCountry.equals(countryName, ignoreCase = true)) {
                    return countryCode
                }
            }
            return null // no country name found
        }

        fun convertDBEntryToAddress(entry: Map<String, Any>): Address? {
            return create(
                country = entry["country"].toString(),
                city = entry["city"].toString(),
                state = entry["state"].toString(),
                street = entry["address"].toString(),
                postalCode = entry["postalCode"].toString(),
                latLng = LatLng(
                    entry["lat"]?.toString()?.toDouble() ?: -1.0,
                    entry["lng"]?.toString()?.toDouble() ?: -1.0
                )
            )
        }
    }

    // Methods
    fun getFormattedAddress(): String? {
        return "$street, $city, $state, $country, $postalCode"
    }

    fun distanceTo(address: Address): Double {
        val selfLoc = Pair(latLng.latitude, latLng.longitude)
        val otherLoc = Pair(address.latLng.latitude, address.latLng.longitude)

        return haversine(selfLoc.first, selfLoc.second, otherLoc.first, otherLoc.second)

        return -1.0
    }

    private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371 // Earth's radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(
            dLon / 2
        ).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return R * c // Distance in km
    }
}
