package com.example.agora.screens.post

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    navController: NavController,
    viewModel: CreatePostViewModel = viewModel(),
) {
    val context = LocalContext.current
    val editing = viewModel.editing
    var isLoading by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    var imageUris = viewModel.images.collectAsState()

    var title = viewModel.title.collectAsState()
    var description = viewModel.description.collectAsState()
    var price = viewModel.price.collectAsState()
    var selectedCategory = viewModel.category.collectAsState()

    Column(
        modifier = Modifier
            .padding(top=40.dp, bottom=0.dp, start=21.dp, end=21.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            TextButton(
                onClick = { navController.popBackStack() },
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
                text = "${ if(editing) "Edit" else "Create"} Post",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Box(modifier = Modifier.width(60.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Photos

        // Image Picker Launcher
        val imagePickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetMultipleContents(),
            onResult = { uris ->
                if (uris.size <= 3) {
                    viewModel.updateImages(uris)
                } else {
                    Toast.makeText(context, "You can only upload up to 3 images", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        )

        // todo cindy: add static carousel
        Text(
            text = "Add Photos",
            fontWeight = FontWeight.Black,
            fontSize = 19.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth(),
            color = MaterialTheme.colorScheme.onBackground,
        )

        Text(
            text = "Upload up to 3 photos directly from your phone",
            fontSize = 16.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth(),
            color = MaterialTheme.colorScheme.onBackground,
        )

        // Show selected images in a horizontal scroll
        LazyRow {
            items(imageUris.value) { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = "Selected Image",
                    modifier = Modifier
                        .size(100.dp)
                        .padding(4.dp)
                        .fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { imagePickerLauncher.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
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
            modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth(),
            color = MaterialTheme.colorScheme.onBackground,
        )

        Text(
            text = "Be as descriptive as possible",
            fontSize = 16.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth(),
            color = MaterialTheme.colorScheme.onBackground,
        )

        val bottomPadding = 8.dp

        OutlinedTextField(
            value = title.value,
            onValueChange = { viewModel.updateTitle(it) },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth().padding(bottom = bottomPadding),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
        )

        OutlinedTextField(
            value = price.value,
            onValueChange = { newValue ->
                // Price validation
                if (newValue.matches(Regex("^\\d*(\\.\\d{0,2})?\$"))) {
                    viewModel.updatePrice(newValue)
                }
            },
            label = { Text("Price") },
            modifier = Modifier.fillMaxWidth().padding(bottom = bottomPadding),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(16.dp),
        )

        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedCategory.value,
                onValueChange = { viewModel.updateCategory(it) },
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
                        text = { Text(category.value) },
                        onClick = {
                            viewModel.updateCategory(category.value)
                            expanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = description.value,
            onValueChange = { viewModel.updateDescription(it) },
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
                    isLoading = true
                    viewModel.createEditPost(
                        onSuccess = {
                            isLoading = false
                            Toast.makeText(context, "Post created successfully!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                            // todo: refresh post/explore screen?
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
                    .height(50.dp)
            ) {
                Text(if (editing) "Update" else "Post", fontSize = 16.sp)
            }
        }
    }
}
