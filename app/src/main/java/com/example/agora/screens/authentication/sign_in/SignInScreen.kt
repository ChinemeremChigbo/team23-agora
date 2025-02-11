package com.example.agora.screens.authentication.sign_in

import android.content.Intent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agora.MainActivity
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SignInScreen() {

    // todo: update theme/colours

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        // todo: image header
        Text("image header placeholder",
            modifier = Modifier.weight(1f).fillMaxWidth())

        // login options
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            // welcome text
            Text(
                text = "Welcome!",
                fontSize = 32.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(16.dp)
            )

            // email address text field
            val emailAddressState = remember { mutableStateOf(TextFieldValue("")) }
            OutlinedTextField(
                value = emailAddressState.value,
                onValueChange = { newValue -> emailAddressState.value = newValue },
                label = { Text("School Email Address") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            // password text field
            // todo: set password field to hidden
            val passwordState = remember { mutableStateOf(TextFieldValue("")) }
            OutlinedTextField(
                value = passwordState.value,
                onValueChange = { newValue -> passwordState.value = newValue },
                label = { Text("Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            // login button
            val context = LocalContext.current
            Button(
                // todo: move onclick logic to mv
                onClick = {
                    val auth = FirebaseAuth.getInstance()
                    val email = emailAddressState.value.text
                    val password = passwordState.value.text

                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    context.startActivity(Intent(context, MainActivity()::class.java))
                                    (context as? ComponentActivity)?.finish()
                                } else {
                                    Toast.makeText(context, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Toast.makeText(context, "Please enter email and password", Toast.LENGTH_SHORT).show()

                        // todo: remove temporary bypass
                        context.startActivity(Intent(context, MainActivity()::class.java))
                        (context as? ComponentActivity)?.finish()
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(88.dp)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Login",
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Preview
@Composable
fun SignInPreview() {
    SignInScreen()
}
