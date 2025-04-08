package com.agora.agora.screens.authentication.signIn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agora.agora.model.data.User
import com.agora.agora.model.util.AccountAuthUtil
import com.agora.agora.model.util.getAuthErrorMessage
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
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
        onPending: (User) -> Unit,
        onError: (String) -> Unit
    ) {
        var emailValue = email.value.trim()
        var passwordValue = password.value.trim()

        viewModelScope.launch {
            try {
                if (emailValue.isEmpty() || passwordValue.isEmpty()) {
                    onError("Please enter email and password")
                    return@launch
                }
                // if login failed, auto throw error can will be caught!
                val currentUser = AccountAuthUtil.accountSignIn(auth, emailValue, passwordValue)
                if (currentUser.isEmailVerified) {
                    onSuccess()
                } else {
                    // prompt user to verify email
                    AccountAuthUtil.sendVerificationEmail(currentUser.email)
                    onPending(currentUser)
                }
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is FirebaseNetworkException ->
                        "Network error. Please check your connection and try again."
                    is FirebaseAuthException -> getAuthErrorMessage(e)
                    else -> "Unexpected error occurred!"
                }
                onError(errorMessage)
            }
        }
    }
}
