package com.example.agora
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.agora.model.util.FirebaseTestUtil
import com.example.agora.screens.MainScreen
import com.example.agora.ui.theme.AgoraTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Uncomment the following line if you want to run
        // against the Firebase Local Emulator Suite (FOR LOCAL TESTING!):
        // FirebaseTestUtil.configureFirebaseServices(resources)
        // Check if user is logged in, redirect to LoginActivity if not
//        val auth = FirebaseAuth.getInstance()
//        if (auth.currentUser == null) {
//            startActivity(Intent(this, LoginActivity::class.java))
//            finish()
//            return
//        }

        setContent {
            AgoraTheme {
                MainScreen()
            }
        }
    }


}