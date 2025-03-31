package com.example.agora.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.agora.model.util.EmailTemplate

@Composable
fun EmailVerificationDialog(onSuccess: () -> Unit, onDismiss: (() -> Unit)? = null) {
    var verificationCode by remember { mutableStateOf(TextFieldValue("")) }
    var isCodeValid by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = {
            if (onDismiss != null) {
                onDismiss()
            }
        },
        title = { Text("Enter Verification Code") },
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Please enter the 6-digit verification code sent to your email.",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = verificationCode,
                    onValueChange = {
                        // Ensure only digits are entered and it's a 6-digit code
                        if (it.text.length <= 6 && it.text.all { char -> char.isDigit() }) {
                            verificationCode = it
                        }
                    },
                    label = { Text("Verification Code") },
                    placeholder = { Text("Enter 6 digits") },
                    singleLine = true,
                    keyboardActions = KeyboardActions.Default,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (!isCodeValid) {
                    Text("Invalid code!", color = Color.Red)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (verificationCode.text.length == 6 && verificationCode.text.equals(
                            EmailTemplate.verificationCode
                        )
                    ) {
                        // Handle successful verification here
                        onSuccess()
                    } else {
                        isCodeValid = false // Show error if the code is not valid
                    }
                }
            ) {
                Text("Verify")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    if (onDismiss != null) {
                        onDismiss()
                    }
                }
            ) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    EmailVerificationDialog(onSuccess = {})
}
