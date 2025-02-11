package com.example.agora.screens.authentication.sign_in

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.agora.MainActivity

@Composable
fun SignInScreen() {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Sign In Screen",
            fontSize = 40.sp,
        )

        val context = LocalContext.current
        Button(onClick = {
            context.startActivity(Intent(context, MainActivity()::class.java))
            (context as? ComponentActivity)?.finish()
        }) {
            Text(
                text = "Login",
                fontSize = 40.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@Composable
fun SignInPreview() {
    SignInScreen()
}
