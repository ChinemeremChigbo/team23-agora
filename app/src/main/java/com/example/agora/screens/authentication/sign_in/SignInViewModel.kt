package com.example.agora.screens.authentication.sign_in

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agora.MainActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class SignInViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun signIn(
        email: String,
        password: String,
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            viewModelScope.launch {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            context.startActivity(Intent(context, MainActivity::class.java))
                            onSuccess()
                        } else {
                            onError(task.exception?.message ?: "Login failed")
                        }
                    }
            }
        } else {
            onError("Please enter email and password")
            // TODO: Remove temporary bypass
            context.startActivity(Intent(context, MainActivity()::class.java))
            (context as? ComponentActivity)?.finish()
        }
    }
}
