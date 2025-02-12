package com.example.agora.screens.authentication.sign_in

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agora.MainActivity
import com.example.agora.model.util.AccountAuthUtil
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
                AccountAuthUtil.accountSignIn(emailValue, passwordValue)
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
