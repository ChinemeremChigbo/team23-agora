package com.example.agora.screens.settings.profile

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.agora.R
import com.example.agora.model.data.Address
import com.example.agora.model.data.User
import com.example.agora.model.repository.ProfileSettingUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val context = application.applicationContext

    val countries = context.resources.getStringArray(R.array.countries).toList()
    val provinces = context.resources.getStringArray(R.array.provinces).toList()
    val states = context.resources.getStringArray(R.array.states).toList()

    var fullName = MutableStateFlow("")
    var phoneNumber = MutableStateFlow("")
    var bio = MutableStateFlow("")
    var country = MutableStateFlow("")
    var state = MutableStateFlow("")
    var city = MutableStateFlow("")
    var street = MutableStateFlow("")
    var postalCode = MutableStateFlow("")
    var userId = ""

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId).get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.data?.let { User.convertDBEntryToUser(it) }
                    if (user != null) {
                        this.userId = user.userId
                        fullName.value = user.fullName
                        phoneNumber.value = user.phoneNumber
                        bio.value = user.bio
                        country.value = user.address.getCountry()
                        state.value = user.address.getState()
                        city.value = user.address.getCity()
                        street.value = user.address.getStreet()
                        postalCode.value = user.address.getPostalCode()
                    }
                }
            }.addOnFailureListener { e ->
                Log.e("ProfileViewModel", "Error loading profile: ${e.message}")
            }
        }
    }

    fun updateFullName(newVal: String) {
        fullName.value = newVal
    }

    fun updatePhoneNumber(newVal: String) {
        phoneNumber.value = newVal
    }

    fun updateBio(newVal: String) {
        bio.value = newVal
    }

    fun updateCountry(newVal: String) {
        country.value = newVal
        state.value = "" // Reset state when country changes
    }

    fun updateState(newVal: String) {
        state.value = newVal
    }

    fun updateCity(newVal: String) {
        city.value = newVal
    }

    fun updateStreet(newVal: String) {
        street.value = newVal
    }

    fun updatePostalCode(newVal: String) {
        postalCode.value = newVal
    }

    fun saveProfile(onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (userId.isEmpty()) {
            onError("User not found")
            return
        }

        println("WOOO")
        println(country.value)

        if (!ProfileSettingUtils.isValidPhoneNumber(phoneNumber.value)) {
            onError("Invalid phone number!")
            return
        }

        viewModelScope.launch {
            var userAddress: Address? = null
            try {
                userAddress = Address.createAndValidate(
                    street.value,
                    city.value,
                    state.value,
                    postalCode.value,
                    country.value
                )
            } catch (e: Exception) {
                e.localizedMessage?.let {
                    onError("Unexpected error occurred!")
                    return@launch
                }
            }
            if (userAddress == null) {
                onError("Invalid address!")
                return@launch
            }
            val updatedData = mapOf(
                "fullName" to fullName.value,
                "phoneNumber" to phoneNumber.value,
                "bio" to bio.value,
                "address.country" to userAddress.getCountry(),
                "address.state" to userAddress.getState(),
                "address.city" to userAddress.getCity(),
                "address.street" to userAddress.getStreet(),
                "address.postalCode" to userAddress.getPostalCode(),
                "address.lat" to userAddress.getLatLng().latitude,
                "address.lng" to userAddress.getLatLng().longitude
            )
            db.collection("users").document(userId).update(updatedData).addOnSuccessListener {
                Log.d("ProfileViewModel", "Profile updated successfully!")
                loadUserProfile()
                onSuccess()
            }.addOnFailureListener { e ->
                Log.e("ProfileViewModel", "Error updating profile: ${e.message}")
                onError(e.localizedMessage ?: "Profile update failed")
            }
        }
    }
}
