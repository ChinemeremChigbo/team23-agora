package com.example.agora.screens.settings

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.agora.model.data.User
import com.example.agora.model.util.AccountAuthUtil
import com.example.agora.model.util.UserManager
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SettingsScreen(auth: FirebaseAuth) {
    val context = LocalContext.current
    var currentUser by remember { mutableStateOf(User()) }
    // Refetch user object everytime it navigate to this page
    LaunchedEffect(key1 = Unit) {
        UserManager.fetchUser(auth.uid!!){
            if (it != null) {
                currentUser = it
            }
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Settings Screen ${currentUser.fullName}",
            fontSize = 40.sp,
            textAlign = TextAlign.Center
        )
        Button(
            onClick = {
                AccountAuthUtil.signOut(auth)
                val activity = context as? Activity
                activity?.let {
                    it.finish() // Finish the current activity
                    it.startActivity(it.intent) // Restart the activity
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("Sign Out", color = Color.White)
        }
    }
}
