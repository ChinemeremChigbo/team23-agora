package com.example.agora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.agora.screens.authentication.sign_in.SignInScreen

class AuthActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Uncomment the following line if you want to run
        // against the Firebase Local Emulator Suite (FOR LOCAL TESTING!):
        // FirebaseTestUtil.configureFirebaseServices(resources)

        setContent {
            SignInScreen()
        }
    }
}
