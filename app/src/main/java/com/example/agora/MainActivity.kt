package com.example.agora

import AppearanceViewModel
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.agora.model.util.FirebaseTestUtil
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.agora.screens.MainScreen
import com.example.agora.screens.settings.appearance.AppearanceViewModelFactory
import com.example.agora.ui.theme.AgoraTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.rpc.context.AttributeContext.Auth

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavHostController
    private val processedDeepLinks = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("main activity started!")
        // Uncomment the following line if you want to run
        // against the Firebase Local Emulator Suite (FOR LOCAL TESTING!):
        // FirebaseTestUtil.configureFirebaseServices(resources)

        // Initialize Firebase Auth
        auth = Firebase.auth

        setContent {
            val appearanceViewModel: AppearanceViewModel =
                viewModel(factory = AppearanceViewModelFactory(this))
            val themeMode by appearanceViewModel.themeMode.collectAsState()

            navController = rememberNavController()

            LaunchedEffect(navController) {
                navController.currentBackStackEntryFlow.collect { entry ->
                    if (entry != null) {
                        handleDeepLink(intent)
                    }
                }
            }

            AgoraTheme(themeMode = themeMode) {
                MainScreen(navController, auth)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is logged in, redirect to AuthActivity if not
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Login expired, redirecting...", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        val data: Uri? = intent?.data
        if (data != null) {
            val deepLink = data.toString()
            val postId = data.getQueryParameter("post_id")


            if (!postId.isNullOrEmpty() && !processedDeepLinks.contains(deepLink)) {
                processedDeepLinks.add(deepLink)
                navController.navigate("post_detail/$postId")
            } else {
                Log.e("DeepLink", "post_id is missing or already processed!")
            }
        }
    }
}