package com.example.agora
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.agora.model.util.FirebaseTestUtil
import com.example.agora.screens.MainScreen
import com.example.agora.ui.theme.AgoraTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.rpc.context.AttributeContext.Auth

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("main activity started!")
        // Uncomment the following line if you want to run
        // against the Firebase Local Emulator Suite (FOR LOCAL TESTING!):
        // FirebaseTestUtil.configureFirebaseServices(resources)

        // Initialize Firebase Auth
        auth = Firebase.auth

        setContent {
            AgoraTheme {
                MainScreen()
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


}