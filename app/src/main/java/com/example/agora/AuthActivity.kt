package com.example.agora

import AppearanceViewModel
import RegisterScreen
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.agora.model.util.FirebaseTestUtil
import com.example.agora.screens.authentication.sign_in.SignInScreen
import com.example.agora.screens.settings.appearance.AppearanceViewModelFactory
import com.example.agora.ui.theme.AgoraTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class AuthActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("auth activity started!")
        // Uncomment the following line if you want to run
        // against the Firebase Local Emulator Suite (FOR LOCAL TESTING!):
//         FirebaseTestUtil.configureFirebaseServices(resources)

        // Initialize Firebase Auth
        auth = Firebase.auth

        setContent {
            val appearanceViewModel: AppearanceViewModel =
                viewModel(factory = AppearanceViewModelFactory(this))
            val themeMode by appearanceViewModel.themeMode.collectAsState()

            AgoraTheme(themeMode = themeMode) {
                AuthNavHost()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is logged in, redirect to MainActivity if yes
        val currentUser = auth.currentUser
        if (currentUser != null && currentUser.isEmailVerified) {
            Toast.makeText(this, "Already logged in ${currentUser.email}, redirecting...", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    @Composable
    fun AuthNavHost() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "login") {
            composable("login") { SignInScreen(navController, auth) }
            composable("register") { RegisterScreen(navController, auth) }
        }
    }
}
