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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
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
        modifier = Modifier.padding(top=40.dp, bottom=0.dp, start=21.dp, end=21.dp),
    ) {
        // Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            TextButton(
                onClick = { navController.navigate("/post") },
                contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp),
                modifier = Modifier.width(60.dp),
                ) {
                Text(
                    text = "Cancel",
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                )
            }

            Text(
                text = "Create Post",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Box(modifier = Modifier.width(60.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Photos

        // todo cindy: add static carousel
        Text(
            text = "Add Photos",
            fontWeight = FontWeight.Black,
            fontSize = 19.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(bottom = 8.dp),
            color = MaterialTheme.colorScheme.onBackground,
        )

        Text(
            text = "Upload up to 3 photos directly from your phone",
            fontSize = 16.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(bottom = 8.dp),
            color = MaterialTheme.colorScheme.onBackground,
        )

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

        // Form Fields
        Text(
            text = "Required",
            fontWeight = FontWeight.Black,
            fontSize = 19.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(bottom = 8.dp),
            color = MaterialTheme.colorScheme.onBackground,
        )

        Text(
            text = "Be as descriptive as possible",
            fontSize = 16.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(bottom = 8.dp),
            color = MaterialTheme.colorScheme.onBackground,
        )

        val bottomPadding = 8.dp

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth().padding(bottom = bottomPadding),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
        )

        OutlinedTextField(
            value = price,
            onValueChange = { newValue ->
                // Price validation
                if (newValue.matches(Regex("^\\d*(\\.\\d{0,2})?\$"))) {
                    price = newValue
                }
            },
            label = { Text("Price") },
            modifier = Modifier.fillMaxWidth().padding(bottom = bottomPadding),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(16.dp),
        )

        // todo cindy: start as empty category
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
                    .padding(bottom = bottomPadding)
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                shape = RoundedCornerShape(16.dp),
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
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = bottomPadding),
            singleLine = false,
            minLines = 6,
            maxLines = 6,
            shape = RoundedCornerShape(16.dp),
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
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Post", fontSize = 16.sp)
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
