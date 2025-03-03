package com.example.agora.screens.post

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.agora.model.data.Category
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    navController: NavController,
    viewModel: PostViewModel = viewModel(),
    auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(Category.SELL) }
    var imageUrl by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Create Post", fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = false
        )

        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Price") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedCategory.name,
                onValueChange = {},
                label = { Text("Category") },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                Category.entries.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.name) },
                        onClick = {
                            selectedCategory = category
                            expanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = imageUrl,
            onValueChange = { imageUrl = it },
            label = { Text("Image URL") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
                Button(
                    onClick = {
                        val userId = auth.currentUser?.uid
                        if (userId != null) {
                            isLoading = true
                            viewModel.createPost(
                                title = title,
                                description = description,
                                price = price.toDoubleOrNull() ?: -1.0,
                                category = selectedCategory,
                                images = if (imageUrl.isNotBlank()) listOf(imageUrl) else emptyList(),
                                userId = userId
                            )

                        // Wait for validation result from ViewModel
                        if (viewModel.error.value == null) {
                            Toast.makeText(context, "Post Created Successfully!", Toast.LENGTH_SHORT).show()
                            isLoading = false
                            navController.popBackStack() // Navigate back to PostScreen
                        } else {
                            Toast.makeText(context, viewModel.error.value, Toast.LENGTH_SHORT).show()
                            isLoading = false
                        }
                    } else {
                        Toast.makeText(context, "User not logged in!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Submit Post", fontSize = 16.sp)
            }
        }
    }
}
