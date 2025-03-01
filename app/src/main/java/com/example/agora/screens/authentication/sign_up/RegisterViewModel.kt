package com.example.agora.screens.authentication.sign_up

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agora.model.data.User
import com.example.agora.model.data.UserStatus
import com.example.agora.model.util.AccountAuthUtil
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    val countries = listOf("Canada", "United States of America")
    val provinces = listOf(
        "Alberta",
        "British Columbia",
        "Manitoba", "New Brunswick",
        "Newfoundland and Labrador",
        "Nova Scotia", "Ontario",
        "Prince Edward Island",
        "Quebec",
        "Saskatchewan",
        "Northwest Territories",
        "Nunavut",
        "Yukon"
    )
    val states = listOf(
        "Alabama",
        "Alaska",
        "Arizona",
        "Arkansas",
        "California",
        "Colorado",
        "Connecticut",
        "Delaware",
        "Florida",
        "Georgia",
        "Hawaii",
        "Idaho",
        "Illinois",
        "Indiana",
        "Iowa",
        "Kansas",
        "Kentucky",
        "Louisiana",
        "Maine",
        "Maryland",
        "Massachusetts",
        "Michigan",
        "Minnesota",
        "Mississippi",
        "Missouri",
        "Montana",
        "Nebraska",
        "Nevada",
        "New Hampshire",
        "New Jersey",
        "New Mexico",
        "New York",
        "North Carolina",
        "North Dakota",
        "Ohio",
        "Oklahoma",
        "Oregon",
        "Pennsylvania",
        "Rhode Island",
        "South Carolina",
        "South Dakota",
        "Tennessee",
        "Texas",
        "Utah",
        "Vermont",
        "Virginia",
        "Washington",
        "West Virginia",
        "Wisconsin",
        "Wyoming",
    )

    var fullName = MutableStateFlow("")
    var email = MutableStateFlow("")
    var country = MutableStateFlow("")
    var state = MutableStateFlow("")
    var city = MutableStateFlow("")
    var address = MutableStateFlow("")
    var postalCode = MutableStateFlow("")
    var password = MutableStateFlow("")
    var confirmPassword = MutableStateFlow("")
    var userId = ""

    fun updateEmail(newEmail: String) {
        email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        password.value = newPassword
    }

    fun updateConfirmPassword(newPassword: String) {
        confirmPassword.value = newPassword
    }

    fun updateFullName(newVal: String) {
        fullName.value = newVal
    }

    fun updateCountry(newVal: String) {
        country.value = newVal
    }

    fun updateState(newVal: String) {
        state.value = newVal
    }

    fun updateCity(newVal: String) {
        city.value = newVal
    }

    fun updateAddress(newVal: String) {
        address.value = newVal
    }

    fun updatePostalCode(newVal: String) {
        postalCode.value = newVal
    }

    fun register(
        auth: FirebaseAuth,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        // Step 0-1: check all required fields are non empty
        if (!checkRequiredFields{ errorMessage -> onError(errorMessage) }) return

        // Step 0-2: confirm password fields are the same
        if (password.value != confirmPassword.value) { onError("Passwords do not match"); return }

        val emailValue = email.value
        val passwordValue = password.value

        // Step 0-3: confirm email is uwaterloo school email
        if(!isValidEmail(emailValue)) { onError("Only uwaterloo email allowed!"); return }

        viewModelScope.launch {
            try {
                // step 1: register user with firebase auth + send verification email
                userId = AccountAuthUtil.accountSignUp(auth, emailValue, passwordValue)
                // TODO - step 2: register user with our database
                val newUser = createUser()
                newUser.register()
                onSuccess()
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Registration failed")
            }
        }
    }

    private fun createUser(): User {
        val user = User(
            username = email.value.substringBefore("@uwaterloo.ca"),
            fullName = fullName.value,
            bio = "", // to be updated in user setting
            profileImage = "", // to be updated in user setting
            email = email.value,
            phoneNumber = "123456789"
        )
        user.setUserId(userId)
        user.setStatus(UserStatus.ACTIVATED)
        return user
    }

    private fun isValidEmail(email: String): Boolean {
        return email.endsWith("@uwaterloo.ca")
    }

    private fun checkRequiredFields(onError: (String) -> Unit): Boolean {
        val fields = mapOf(
            "Full Name" to fullName.value,
            "Email" to email.value,
            "Country" to country.value,
            "Province/State" to state.value,
            "City" to city.value,
            "Address" to address.value,
            "Postal/Zip Code" to postalCode.value,
            "Password" to password.value,
            "Password Confirmation" to confirmPassword.value
        )

        for ((key, value) in fields) {
            if (value.isEmpty()) {
                onError("$key field cannot be empty")
                return false
            }
        }
        return true
    }
}
