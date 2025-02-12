package com.example.agora.screens.explore


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
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
    val recentSearches by viewModel.recentSearches.collectAsState()
    val sections by viewModel.sections.collectAsState()

    Column (
        modifier = Modifier.padding(21.dp),
        verticalArrangement = Arrangement.spacedBy(40.dp),
    ) {
        SearchBar(
            modifier = Modifier.clip(RoundedCornerShape(10.dp)),
            colors = SearchBarDefaults.colors(containerColor = MaterialTheme.colorScheme.surface),
            inputField = {
                SearchBarDefaults.InputField(
                    query = searchText,
                    onQueryChange = viewModel::onSearchTextChange,
                    onSearch = { viewModel.onSearchSubmitted(searchText) },
                    expanded = isExpanded,
                    onExpandedChange = { viewModel.onExpandedChange(it) },
                    placeholder = { Text("Find what you're looking for...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    trailingIcon = { if(isExpanded) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close search",
                            modifier = Modifier
                                .clickable(onClick={viewModel.onExpandedChange((false))}))
                    } }
                )
            },
            expanded = isExpanded,
            onExpandedChange = { viewModel.onExpandedChange(it) }
        ) {
            if (recentSearches.isNotEmpty()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "RECENT SEARCHES",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    recentSearches.forEach() { search ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(70.dp)
                                .padding(vertical = 4.dp)
                                .clickable { viewModel.onSearchSubmitted(search) },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(search, modifier = Modifier.weight(1f).padding(4.dp))
                            // TODO (jennifer): add ability to clear later
                        }
                    }
                }
            }
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
