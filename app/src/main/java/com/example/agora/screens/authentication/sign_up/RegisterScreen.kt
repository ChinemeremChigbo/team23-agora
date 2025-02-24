import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.agora.screens.authentication.sign_up.RegisterViewModel

@Composable
fun RegisterScreen(navController: NavController, viewModel: RegisterViewModel = viewModel()) {
    val context = LocalContext.current
    val scrollState = rememberScrollState() // Enables scrolling

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
                        if(expandedCountry) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
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

//        // State Dropdown
//        var expandedState by remember { mutableStateOf(false) }
//        Box(modifier = Modifier.fillMaxWidth()) {
//            OutlinedTextField(
//                value = state,
//                onValueChange = { },
//                label = { Text("Province/State") },
//                modifier = Modifier.fillMaxWidth(),
//                readOnly = true,
//                trailingIcon = {
//                    Icon(Icons.Default.Visibility, contentDescription = "Select State")
//                }
//            )
//            DropdownMenu(
//                expanded = expandedState,
//                onDismissRequest = { expandedState = false }
//            ) {
//                listOf("California", "New York", "Texas").forEach { stateOption ->
//                    DropdownMenuItem(
//                        text = { Text(stateOption) },
//                        onClick = {
//                            state = stateOption
//                            expandedState = false
//                        }
//                    )
//                }
//            }
//        }
//
//        OutlinedTextField(
//            value = city,
//            onValueChange = { city = it },
//            label = { Text("City") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        OutlinedTextField(
//            value = address,
//            onValueChange = { address = it },
//            label = { Text("Address") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        OutlinedTextField(
//            value = postalCode,
//            onValueChange = { postalCode = it },
//            label = { Text("Postal/Zip Code") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        // Password Fields
//        OutlinedTextField(
//            value = password,
//            onValueChange = { password = it },
//            label = { Text("Create password") },
//            modifier = Modifier.fillMaxWidth(),
//            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
//            trailingIcon = {
//                IconButton(onClick = { passwordVisible = !passwordVisible }) {
//                    Icon(
//                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
//                        contentDescription = "Toggle password visibility"
//                    )
//                }
//            }
//        )
//
//        OutlinedTextField(
//            value = confirmPassword,
//            onValueChange = { confirmPassword = it },
//            label = { Text("Confirm password") },
//            modifier = Modifier.fillMaxWidth(),
//            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
//            trailingIcon = {
//                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
//                    Icon(
//                        imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
//                        contentDescription = "Toggle password visibility"
//                    )
//                }
//            }
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
        Button(
            onClick = {
                Toast.makeText(context, "Registering...", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Continue", color = Color.White, fontSize = 16.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(navController = rememberNavController())
}
