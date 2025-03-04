package com.example.agora.screens.explore


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.agora.ui.components.PostPreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(viewModel: ExploreViewModel = viewModel(), navController: NavController) {
    val searchText by viewModel.searchText.collectAsState()
    val isExpanded by viewModel.isExpanded.collectAsState()
    val postList by viewModel.postList.collectAsState()
    val isLoading by viewModel.isLoading.observeAsState(true)

    Column (
        modifier = Modifier.padding(top=21.dp, bottom=0.dp, start=21.dp, end=21.dp),
        verticalArrangement = Arrangement.spacedBy(40.dp),
    ) {
        SearchBar(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)),
            colors = SearchBarDefaults.colors(containerColor = MaterialTheme.colorScheme.surface),
            inputField = {
                SearchBarDefaults.InputField(
                    query = searchText,
                    onQueryChange = viewModel::onSearchTextChange,
                    onSearch = {
                        navController.navigate("search/${searchText}")
                    },
                    expanded = isExpanded,
                    onExpandedChange = { viewModel.onExpandedChange(it) },
                    placeholder = { Text("Search") },
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
        ) {}

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(Alignment.Center)
                .padding(16.dp))
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(40.dp),
        ) {
            items(postList) { (title, posts) ->
                Column(verticalArrangement = Arrangement.spacedBy(21.dp)) {
                    Text(title, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(posts) { post ->
                            PostPreview(post, onClick = {navController.navigate("post_detail/${post.postId}")})
                        }
                    }
                }
            }
        }
    }
}
