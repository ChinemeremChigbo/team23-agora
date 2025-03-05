package com.example.agora.screens.authentication.sign_in

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agora.model.util.AccountAuthUtil
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SignInViewModel : ViewModel() {
    val email = MutableStateFlow("")
    val password = MutableStateFlow("")

    fun updateEmail(newEmail: String) {
        email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        password.value = newPassword
    }

    fun signIn(
        auth: FirebaseAuth,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        var emailValue = email.value.trim()
        var passwordValue = password.value.trim()

        viewModelScope.launch {
            try {
                if (emailValue.isEmpty() || passwordValue.isEmpty()) {
                    passwordValue = "123456"
                    emailValue = "j35zhan@uwaterloo.ca"// TODO: Remove Temporary bypass logic, enable onError
//                    onError("Please enter email and password")
                }
                AccountAuthUtil.accountSignIn(auth, emailValue, passwordValue)
                // if login failed, auto throw error can will be caught!
                onSuccess()
            }  catch (e: Exception) {
                onError(e.localizedMessage ?: "Login failed")
            }
        }
    }
}
