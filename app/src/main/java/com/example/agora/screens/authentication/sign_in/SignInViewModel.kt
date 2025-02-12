package com.example.agora.screens.authentication.sign_in

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import androidx.activity.ComponentActivity
import com.example.agora.MainActivity
import com.example.agora.model.data.AccountService
import com.example.agora.screens.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    application: Application,
    private val accountService: AccountService,
) : BaseViewModel() {
    @SuppressLint("StaticFieldLeak")
    private val context = application.applicationContext

    val email = MutableStateFlow("")
    val password = MutableStateFlow("")

    fun updateEmail(newEmail: String) {
        email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        password.value = newPassword
    }

    fun onSignInClick(openAndPopUp: (String, String) -> Unit) {
        launchCatching {
            val isSuccess = accountService.signIn(email.value, password.value)
            if(isSuccess){
                val activity = context as? ComponentActivity
                if (activity != null) {
                    activity.startActivity(Intent(context, MainActivity::class.java))
                    activity.finish()
                }
            }


        }
    }

    fun onSignUpClick(openAndPopUp: (String, String) -> Unit) {
        openAndPopUp(SIGN_UP_SCREEN, SIGN_IN_SCREEN)
    }
}