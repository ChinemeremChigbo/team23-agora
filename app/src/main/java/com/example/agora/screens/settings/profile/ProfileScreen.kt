package com.example.agora.screens.settings.profile

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    auth: FirebaseAuth, navController: NavController, viewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var isLoading by remember { mutableStateOf(false) }

    val fullName by viewModel.fullName.collectAsState()
    val phoneNumber by viewModel.phoneNumber.collectAsState()
    val bio by viewModel.bio.collectAsState()
    val country by viewModel.country.collectAsState()
    val state by viewModel.state.collectAsState()
    val city by viewModel.city.collectAsState()
    val street by viewModel.street.collectAsState()
    val postalCode by viewModel.postalCode.collectAsState()

    val bottomPadding = 8.dp
    val countries = viewModel.countries
    val provinces = viewModel.provinces
    val states = viewModel.states

    Scaffold { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(scrollState)
                .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(
                    onClick = { navController.popBackStack() },
                    contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp),
                    modifier = Modifier.width(50.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Back", color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                }

                Text(
                    text = "Profile",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )

                Box(modifier = Modifier.width(50.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            ProfileTextField("Full Name", fullName, viewModel::updateFullName)
            ProfileTextField(
                "Phone Number", phoneNumber, viewModel::updatePhoneNumber, KeyboardType.Phone
            )

            var expandedCountry by remember { mutableStateOf(false) }
            DropdownField(
                label = "Country",
                value = country,
                items = countries,
                expanded = expandedCountry,
                onExpandedChange = { expandedCountry = it },
                onItemClick = { viewModel.updateCountry(it) })

            var expandedState by remember { mutableStateOf(false) }
            DropdownField(
                label = "Province/State",
                value = state,
                items = if (country == countries[0]) provinces else states,
                expanded = expandedState,
                onExpandedChange = { expandedState = it },
                onItemClick = { viewModel.updateState(it) })

            ProfileTextField("City", city, viewModel::updateCity)
            ProfileTextField("Street Address", street, viewModel::updateStreet)
            ProfileTextField(
                "Postal/ZIP Code", postalCode, viewModel::updatePostalCode, KeyboardType.Number
            )

            OutlinedTextField(
                value = bio,
                onValueChange = { viewModel.updateBio(it) },
                label = { Text("Bio") },
                modifier = Modifier.fillMaxWidth().padding(bottom = bottomPadding).height(120.dp),
                shape = RoundedCornerShape(16.dp),
                maxLines = 5,
                singleLine = false,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Default)
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        isLoading = true
                        viewModel.saveProfile(onSuccess = {
                            isLoading = false
                            Toast.makeText(context, "Profile updated!", Toast.LENGTH_SHORT).show()
                        }, onError = { error ->
                            isLoading = false
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                        })
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                ) {
                    Text("Save Changes", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    label: String,
    value: String,
    items: List<String>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onItemClick: (String) -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = expanded, onExpandedChange = { onExpandedChange(!expanded) }) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
            readOnly = true,
            shape = RoundedCornerShape(16.dp),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            })
        ExposedDropdownMenu(
            expanded = expanded, onDismissRequest = { onExpandedChange(false) }) {
            items.forEach { item ->
                DropdownMenuItem(text = { Text(item) }, onClick = {
                    onItemClick(item)
                    onExpandedChange(false)
                })
            }
        }
    }
}

