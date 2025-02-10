package com.example.agora.model.data

class Address(
    private var street: String = "",
    private var city: String = "",
    private var state: String = "",
    private var postalCode: String = "", // there is probably an existing type for this
    private var country: String = "" // and this
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

    // Methods
    fun validateAddress(): Boolean? {
        return null // Placeholder
    }

    fun getFormattedComment(): Boolean? {
        return null // Placeholder
    }

    fun distanceTo(address: Address): Float {
        return 0.0f // Placeholder
    }
}