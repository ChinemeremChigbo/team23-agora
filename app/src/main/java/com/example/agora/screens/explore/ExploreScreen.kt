package com.example.agora.screens.explore

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState


@Composable
fun ExploreScreen() {
    val exploreViewModel: ExploreViewModel = viewModel()
    val postItems by exploreViewModel.editorsChoiceItems.observeAsState(emptyList())
    Scaffold(
        topBar = {
            OutlinedTextField(
                value = "",
                onValueChange = { /* handle search query changes */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 60.dp),
                placeholder = { Text("Find what you're looking for") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search Icon"
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer, // Soft fill color
                    unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                shape = RoundedCornerShape(50.dp),
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Column {
                PostSection("Editor's choice", postItems)
                PostSection("New arrivals", postItems)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Explore Screen",
                        fontSize = 40.sp,
                        textAlign = TextAlign.Center
                    )
                }

            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ExplorePreview() {
    ExploreScreen()
}
