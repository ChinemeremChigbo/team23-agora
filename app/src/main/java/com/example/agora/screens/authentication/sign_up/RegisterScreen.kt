import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.agora.screens.authentication.sign_up.RegisterViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
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
        Spacer(modifier = Modifier.height(32.dp))

        // Title
        Text(
            text = "Sign Up",
            fontWeight = FontWeight.Black,
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 8.dp),
            color = MaterialTheme.colorScheme.onBackground,
        )

        Text(
            text = "Create an account to get started",
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 16.dp),
            color = MaterialTheme.colorScheme.onBackground,
        )

        // Form Fields
        val bottomPadding = 8.dp

        OutlinedTextField(
            value = fullName.value,
            onValueChange = { viewModel.updateFullName(it) },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth().padding(bottom = bottomPadding),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
        )

        OutlinedTextField(
            value = email.value,
            onValueChange = { viewModel.updateEmail(it) },
            label = { Text("School Email Address") },
            modifier = Modifier.fillMaxWidth().padding(bottom = bottomPadding),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
        )

        var expandedCountry by remember { mutableStateOf(false) }
        val countries = listOf("Canada", "United States of America") // todo cindy
        ExposedDropdownMenuBox(
            expanded = expandedCountry,
            onExpandedChange = { expandedCountry = !expandedCountry }
        ) {
            OutlinedTextField(
                value = country.value,
                onValueChange = { viewModel.updateCountry(it) },
                label = { Text("Country") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = bottomPadding)
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCountry)
                }
            )
            ExposedDropdownMenu(
                expanded = expandedCountry,
                onDismissRequest = { expandedCountry = false }
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

        var expandedState by remember { mutableStateOf(false) }
        val provinces = listOf("Ontario", "British Columbia")   // todo cindy
        val states = listOf("CA", "NY")
        ExposedDropdownMenuBox(
            expanded = expandedState,
            onExpandedChange = { expandedState = !expandedState }
        ) {
            OutlinedTextField(
                value = state.value,
                onValueChange = { viewModel.updateState(it) },
                label = { Text("Province/State")},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = bottomPadding)
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedState)
                }
            )
            ExposedDropdownMenu(
                expanded = expandedState,
                onDismissRequest = { expandedState = false }
            ) {
                if (country.value == "Canada") {
                    provinces.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                viewModel.updateState(option)
                                expandedState = false
                            }
                        )
                    }
                } else {
                    states.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                viewModel.updateState(option)
                                expandedState = false
                            }
                        )
                    }
                }
            }
        }

        OutlinedTextField(
            value = city.value,
            onValueChange = { viewModel.updateCity(it) },
            label = { Text("City") },
            modifier = Modifier.fillMaxWidth().padding(bottom = bottomPadding),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
        )

        OutlinedTextField(
            value = address.value,
            onValueChange = { viewModel.updateAddress(it) },
            label = { Text("Address") },
            modifier = Modifier.fillMaxWidth().padding(bottom = bottomPadding),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
        )

        OutlinedTextField(
            value = postalCode.value,
            onValueChange = { viewModel.updatePostalCode(it) },
            label = { Text("Postal/Zip Code") },
            modifier = Modifier.fillMaxWidth().padding(bottom = bottomPadding),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
        )

        OutlinedTextField(
            value = password.value,
            onValueChange = { viewModel.updatePassword(it) },
            label = { Text("Create password") },
            modifier = Modifier.fillMaxWidth().padding(bottom = bottomPadding),
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle password visibility"
                    )
                }
            },
            shape = RoundedCornerShape(16.dp),
        )

        OutlinedTextField(
            value = confirmPassword.value,
            onValueChange = { viewModel.updateConfirmPassword(it) },
            label = { Text("Confirm password") },
            modifier = Modifier.fillMaxWidth().padding(bottom = bottomPadding),
            singleLine = true,
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(
                        imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle password visibility"
                    )
                }
            },
            shape = RoundedCornerShape(16.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Continue button
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
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            ) {
                Text(
                    text = "Continue",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
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
