package com.example.agora.screens.post


import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Upload
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
import coil.compose.rememberAsyncImagePainter
import com.example.agora.model.data.Category
import com.example.agora.util.uploadImageToS3
import com.google.firebase.auth.FirebaseAuth


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    navController: NavController,
    viewModel: CreatePostViewModel = viewModel(),
    auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(Category.SELL) }
    var isLoading by remember { mutableStateOf(false) }


    // Allow up to 3 images
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }


    // Image Picker Launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        onResult = { uris ->
            if (uris.size <= 3) {
                imageUris = uris
            } else {
                Toast.makeText(context, "You can only upload up to 3 images", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    )


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


        Spacer(modifier = Modifier.height(16.dp))


        // Show selected images in a horizontal scroll
        LazyRow {
            items(imageUris) { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = "Selected Image",
                    modifier = Modifier
                        .size(100.dp)
                        .padding(4.dp)
                )
            }
        }


        Spacer(modifier = Modifier.height(8.dp))


        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
            Icon(Icons.Default.Upload, contentDescription = "Upload")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Pick Images (Up to 3)")
        }


        Spacer(modifier = Modifier.height(16.dp))


        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        isLoading = true
                        if (imageUris.isNotEmpty()) {
                            val uploadedUrls = mutableListOf<String>()
                            imageUris.forEachIndexed { _, uri ->
                                uploadImageToS3(context, uri, onSuccess = { uploadedImageUrl ->
                                    uploadedUrls.add(uploadedImageUrl)
                                    if (uploadedUrls.size == imageUris.size) { // Wait for all uploads to finish
                                        createPost(
                                            navController,
                                            viewModel,
                                            userId,
                                            title,
                                            description,
                                            price,
                                            selectedCategory,
                                            uploadedUrls,
                                            context
                                        )
                                    }
                                }, onFailure = { errorMessage ->
                                    Toast.makeText(
                                        context,
                                        "Image upload failed: $errorMessage",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    isLoading = false
                                })
                            }
                        } else {
                            createPost(
                                navController,
                                viewModel,
                                userId,
                                title,
                                description,
                                price,
                                selectedCategory,
                                emptyList(),
                                context
                            )
                            isLoading = false
                        }
                    } else {
                        Toast.makeText(context, "User not logged in!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Submit Post", fontSize = 16.sp)
            }
        }
    }
}


/** Create Post in Firestore */
fun createPost(
    navController: NavController,
    viewModel: CreatePostViewModel,
    userId: String,
    title: String,
    description: String,
    price: String,
    category: Category,
    imageUrls: List<String>,
    context: android.content.Context
) {
    viewModel.createPost(
        title = title,
        description = description,
        price = price.toDoubleOrNull() ?: -1.0,
        category = category,
        images = imageUrls,
        userId = userId
    )


    if (viewModel.error.value == null) {
        Toast.makeText(context, "Post Created Successfully!", Toast.LENGTH_SHORT).show()
        navController.popBackStack()
    } else {
        Toast.makeText(context, viewModel.error.value, Toast.LENGTH_SHORT).show()
    }
}
