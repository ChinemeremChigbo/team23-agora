package com.chinemerem.agora.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chinemerem.agora.model.util.EmailTemplate

@Composable
fun EmailVerificationDialog(onSuccess: () -> Unit, onDismiss: (() -> Unit)? = null) {
    var verificationCode by remember { mutableStateOf(TextFieldValue("")) }
    var isCodeValid by remember { mutableStateOf(true) }

    AlertDialog(
        shape = RoundedCornerShape(21.dp),
        onDismissRequest = { onDismiss?.invoke() },
        containerColor = MaterialTheme.colorScheme.surface,
        confirmButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = { onDismiss?.invoke() },
                    border = BorderStroke(1.dp, Color(0xFF006FFD)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel", color = Color(0xFF006FFD))
                }
                Button(
                    onClick = {
                        if (verificationCode.text.length == 6 &&
                            verificationCode.text == EmailTemplate.verificationCode
                        ) {
                            onSuccess()
                        } else {
                            isCodeValid = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF006FFD),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Verify")
                }
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Email Verification",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Enter the 6-digit verification code sent to your email.",
                    textAlign = TextAlign.Center
                )
                TextField(
                    value = verificationCode,
                    onValueChange = {
                        if (it.text.length <= 6 && it.text.all(Char::isDigit)) {
                            verificationCode = it
                            isCodeValid = true
                        }
                    },
                    label = { Text("Verification Code") },
                    placeholder = { Text("Enter 6 digits") },
                    singleLine = true,
                    isError = !isCodeValid,
                    modifier = Modifier.fillMaxWidth()

                )

                if (!isCodeValid) {
                    Text(
                        text = "Invalid code!",
                        color = Color.Red,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    EmailVerificationDialog(onSuccess = {})
}
