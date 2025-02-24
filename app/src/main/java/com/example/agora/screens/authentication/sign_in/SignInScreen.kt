package com.example.agora.screens.authentication.sign_in

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.agora.MainActivity
import com.example.agora.R
import com.example.agora.ui.theme.AgoraTheme
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginImage() {
    val imageResId = if (isSystemInDarkTheme()) {
        R.drawable.login_dark
    } else {
        R.drawable.login
    }

    Image(
        painter = painterResource(id = imageResId),
        contentDescription = "Login Image",
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun SignInScreen(navController: NavController, auth: FirebaseAuth, viewModel: SignInViewModel = viewModel()) {
    var isLoading by remember { mutableStateOf(false) }
    AgoraTheme {
        val context = LocalContext.current
        val emailState = viewModel.email.collectAsState()
        val passwordState = viewModel.password.collectAsState()
        var passwordVisible by remember { mutableStateOf(false) }

        Box(modifier = Modifier.fillMaxSize()) {
            LoginImage()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .padding(top = 310.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Welcome!",
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onBackground,
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Email input field
                OutlinedTextField(
                    value = emailState.value,
                    onValueChange = { viewModel.updateEmail(it) },
                    label = { Text("School Email Address") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Password input field with toggle visibility
                OutlinedTextField(
                    value = passwordState.value,
                    onValueChange = {  viewModel.updatePassword(it) },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image =
                            if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = image,
                                contentDescription = "Toggle Password Visibility"
                            )
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                )
                Spacer(modifier = Modifier.height(2.dp))

                TextButton(
                    onClick = { /* TODO: Handle forgot password here */ },
                    modifier = Modifier.align(Alignment.Start),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                ) {
                    Text(
                        text = "Forgot Password?",
                        fontSize = 14.sp,
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                if (isLoading) {
                    CircularProgressIndicator() // ✅ Show Loading Indicator
                } else {
                    // Login button
                    Button(
                        onClick = {
                            isLoading = true
                            viewModel.signIn(
                                auth,
                                onSuccess = {
                                    isLoading = false
                                    navigateToMainActivity(context)
                                },
                                onError = { errorMessage ->
                                    isLoading = false
                                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                                }
                            )
                        },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text(
                            text = "Login",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Not a member?",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                    )

                    TextButton(
                        onClick = { navController.navigate("register") },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                    ) {
                        Text(
                            text = "Sign up",
                            fontSize = 14.sp,
                        )
                    }
                }
            }
        }
    }
}

private fun navigateToMainActivity(context: Context) {
    context.startActivity(Intent(context, MainActivity::class.java))
    (context as? ComponentActivity)?.finish()
}

@Preview(showBackground = true)
@Composable
fun SignInPreview() {
    val fakeAuth = remember { FirebaseAuth.getInstance() }
    // placeholder navController
    SignInScreen(navController = rememberNavController(), fakeAuth)
}
