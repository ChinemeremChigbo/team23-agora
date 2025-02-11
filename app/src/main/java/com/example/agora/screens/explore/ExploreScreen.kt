package com.example.agora.screens.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.agora.ui.components.PostPreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(viewModel: ExploreViewModel = viewModel()) {
    val searchText by viewModel.searchText.collectAsState()
    val isExpanded by viewModel.isExpanded.collectAsState()

    val titles1 = listOf("Fridge", "Fridge 2", "Fridge 3")
    val titles2 = listOf("Book", "Book 2", "Book 3")
    val titles3 = listOf("Plates")
    val sections = listOf(titles1, titles2, titles3)

    Column (
        modifier = Modifier.padding(21.dp),
        verticalArrangement = Arrangement.spacedBy(40.dp),
    ) {
        SearchBar(
            modifier = Modifier.height(59.dp),
            colors = SearchBarDefaults.colors(containerColor = MaterialTheme.colorScheme.surface),
            inputField = {
                SearchBarDefaults.InputField(
                    query = searchText,
                    onQueryChange = viewModel::onSearchTextChange,
                    onSearch = viewModel::onSearchTextChange,
                    expanded = isExpanded,
                    onExpandedChange = { viewModel.onExpandedChange() },
                    placeholder = { Text("Find what you're looking for...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                )
            },
            expanded = isExpanded,
            onExpandedChange = {  },
        ){

        }
        LazyColumn (
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(sections) { section ->
                Column (verticalArrangement = Arrangement.spacedBy(21.dp)) {
                    Text("Section header", fontSize=19.sp, fontWeight = FontWeight.Bold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(section) { title ->
                            PostPreview(title)
                        }
                    }
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

