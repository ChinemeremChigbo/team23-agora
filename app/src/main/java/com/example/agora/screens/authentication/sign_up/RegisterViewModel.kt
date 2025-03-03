package com.example.agora.screens.authentication.sign_up

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agora.R
import com.example.agora.model.data.User
import com.example.agora.model.data.UserStatus
import com.example.agora.model.util.AccountAuthUtil
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext
    val countries = context.resources.getStringArray(R.array.countries).toList()
    val provinces = context.resources.getStringArray(R.array.provinces).toList()
    val states = context.resources.getStringArray(R.array.states).toList()

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

        // TODO: Step 0-4: confirm address + phone number are valid

        viewModelScope.launch {
            try {
                // step 1: register user with firebase auth + send verification email
                userId = AccountAuthUtil.accountSignUp(auth, emailValue, passwordValue)
                // step 2: register user with our database
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
            userId = userId,
            username = email.value.substringBefore("@uwaterloo.ca"),
            fullName = fullName.value,
            email = email.value,
            phoneNumber = "123456789",
            country = country.value,
            city = city.value,
            state = state.value,
            address = address.value,
            postalCode = postalCode.value,
        )
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
