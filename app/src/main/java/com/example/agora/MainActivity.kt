package com.example.agora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.agora.screens.explore.ExploreScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Uncomment the following line if you want to run
        // against the Firebase Local Emulator Suite (FOR LOCAL TESTING!):
        // configureFirebaseServices()

        // Check if user is logged in, redirect to LoginActivity if not
//        val auth = FirebaseAuth.getInstance()
//        if (auth.currentUser == null) {
//            startActivity(Intent(this, LoginActivity::class.java))
//            finish()
//            return
//        }

        setContent { ExploreScreen() }
    }

    private fun configureFirebaseServices() {
        val r = resources
        val LOCALHOST = r.getString(R.string.localhost)
        val AUTH_PORT = r.getInteger(R.integer.auth_port)
        val FIRESTORE_PORT =  r.getInteger(R.integer.firestore_port)
        // port might be available: run `lsof -i :<port-number>` to get process PID and `kill -9 <PID>` to kill process
        FirebaseAuth.getInstance().useEmulator(LOCALHOST, AUTH_PORT)
        FirebaseFirestore.getInstance().useEmulator(LOCALHOST, FIRESTORE_PORT)
    }
}