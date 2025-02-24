package com.example.agora.screens.authentication.sign_up

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agora.MainActivity
import com.example.agora.model.util.AccountAuthUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    var fullName = MutableStateFlow("")
    var email = MutableStateFlow("")
    var country = MutableStateFlow("")
    var state = MutableStateFlow("")
    var city = MutableStateFlow("")
    var address = MutableStateFlow("")
    var postalCode = MutableStateFlow("")
    var password = MutableStateFlow("")
    var confirmPassword = MutableStateFlow("")

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

    fun signIn(
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val emailValue = email.value
        val passwordValue = password.value

        if (emailValue.isEmpty() || passwordValue.isEmpty()) {
            onError("Please enter email and password")
            navigateToMainActivity(context) // TODO: Remove Temporary bypass logic
            return
        }

        viewModelScope.launch {
            try {
//                AccountAuthUtil.accountSignIn(emailValue, passwordValue)
                // if login failed, auto throw error can will be caught!
                context.startActivity(Intent(context, MainActivity::class.java))
                onSuccess()
            }  catch (e: Exception) {
                onError(e.localizedMessage ?: "Login failed")
            }
        }
    }

    private fun navigateToMainActivity(context: Context) {
        context.startActivity(Intent(context, MainActivity::class.java))
        (context as? ComponentActivity)?.finish()
    }
}
