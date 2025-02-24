import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.agora.screens.authentication.sign_up.RegisterViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun RegisterScreen(navController: NavController, auth: FirebaseAuth, viewModel: RegisterViewModel = viewModel()) {
    val context = LocalContext.current
    val scrollState = rememberScrollState() // Enables scrolling
    var isLoading by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    var fullName = viewModel.fullName.collectAsState()
    var email = viewModel.email.collectAsState()
    var country = viewModel.country.collectAsState()
    var state = viewModel.state.collectAsState()
    var city = viewModel.city.collectAsState()
    var address = viewModel.address.collectAsState()
    var postalCode = viewModel.postalCode.collectAsState()
    var password = viewModel.password.collectAsState()
    var confirmPassword = viewModel.confirmPassword.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "Sign up",
            fontSize = 24.sp,
            color = Color.Black
        )

        Text(
            text = "Create an account to get started",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = fullName.value,
            onValueChange = { viewModel.updateFullName(it) },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email.value,
            onValueChange = { viewModel.updateEmail(it) },
            label = { Text("School Email Address") },
            modifier = Modifier.fillMaxWidth()
        )

        // Country Dropdown
        var expandedCountry by remember { mutableStateOf(false) }
        val countries = listOf("USA", "Canada", "UK")
        Box(modifier = Modifier.fillMaxWidth().clickable { expandedCountry = true }) {
            OutlinedTextField(
                value = country.value,
                onValueChange = { viewModel.updateCountry(it) },
                label = { Text("Country") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    Icon(
                        if (expandedCountry) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Select Country",
                        modifier = Modifier.clickable { expandedCountry = !expandedCountry }
                    )
                }
            )
            DropdownMenu(
                expanded = expandedCountry,
                onDismissRequest = { expandedCountry = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                countries.forEach { countryOption ->
                    DropdownMenuItem(
                        text = { Text(countryOption) },
                        onClick = {
                            viewModel.updateCountry(countryOption)
                            expandedCountry = false
                        }
                    )
                }
            }
        }


        OutlinedTextField(
            value = password.value,
            onValueChange = { viewModel.updatePassword(it) },
            label = { Text("Create password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle password visibility"
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator() // ✅ Show Loading Indicator
        } else {
            Button(
                onClick = {
                    isLoading = true
                    viewModel.register(
                        auth,
                        onSuccess = {
                            isLoading = false
                            showDialog = true
                        },
                        onError = { errorMessage ->
                            isLoading = false
                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Continue", color = Color.White, fontSize = 16.sp)
            }
            RegistrationSuccessDialog(showDialog) {
                showDialog = false // Close dialog when dismissed
                navController.navigate("login")
            }
        }
    }
}

@Composable
fun RegistrationSuccessDialog(showDialog: Boolean, onDismiss: () -> Unit) {
    if (showDialog) {
        AlertDialog(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            onDismissRequest = onDismiss, // Allows dismissing by tapping outside
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("OK", color = MaterialTheme.colorScheme.primary)
                }
            },
            title = { Text("Registration Success!", color = MaterialTheme.colorScheme.primary) },
            text = { Text("One last step: check your email to verify your account", color = MaterialTheme.colorScheme.onPrimaryContainer) }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    val fakeAuth = remember { FirebaseAuth.getInstance() }
    RegisterScreen(navController = rememberNavController(), fakeAuth)
}
