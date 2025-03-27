import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdatePasswordScreen(
    navController: NavController,
    viewModel: UpdatePasswordViewModel = viewModel()
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val passwordUpdated by viewModel.passwordUpdated
    val errorMessage by viewModel.errorMessage

    if (passwordUpdated) {
        LaunchedEffect(Unit) {
            if (navController.previousBackStackEntry != null) { navController.popBackStack() }
        }
    }

    Scaffold(topBar = {}) { padding ->
        Column(
            modifier = Modifier
                .padding(top = 21.dp, bottom = 0.dp, start = 21.dp, end = 21.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(
                    onClick = { if (navController.previousBackStackEntry != null) { navController.popBackStack() }},
                    modifier = Modifier.width(60.dp),
                    contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp)
                ) {
                    Text(
                        text = "Back",
                        fontSize = 15.sp
                    )
                }
                Text(
                    text = "Update Password",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Box(modifier = Modifier.width(60.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            PasswordField("Current Password", currentPassword) { currentPassword = it }
            Spacer(modifier = Modifier.height(12.dp))

            PasswordField("New Password", newPassword) { newPassword = it }
            Spacer(modifier = Modifier.height(12.dp))

            PasswordField("Confirm New Password", confirmPassword) { confirmPassword = it }
            Spacer(modifier = Modifier.height(20.dp))

            errorMessage?.let {
                Text(it, color = Color.Red, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(10.dp))
            }

            Button(
                onClick = {
                    if (newPassword.isEmpty() && confirmPassword.isEmpty()) {
                        viewModel.errorMessage.value = "Password fields cannot be empty!"
                    } else if (newPassword != confirmPassword) {
                        viewModel.errorMessage.value = "Passwords do not match!"
                    } else {
                        viewModel.updatePassword(currentPassword, newPassword)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    "Update Password",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun PasswordField(label: String, value: String, onValueChange: (String) -> Unit) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        visualTransformation = if (passwordVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    imageVector = if (passwordVisible) {
                        Icons.Default.Visibility
                    } else {
                        Icons.Default.VisibilityOff
                    },
                    contentDescription = "Toggle password visibility"
                )
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}
