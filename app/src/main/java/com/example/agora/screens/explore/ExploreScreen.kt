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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.agora.screens.postDetail.PostDetailScreen
import com.example.agora.screens.postDetail.PostDetailViewModel
import com.example.agora.screens.postDetail.PostDetailViewModelFactory
import com.example.agora.screens.search.SearchScreen
import com.example.agora.screens.search.SearchViewModel
import com.example.agora.screens.search.SearchViewModelFactory
import com.example.agora.ui.components.PostPreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(viewModel: ExploreViewModel = viewModel(), parentNavController: NavController) {
    val nestedNavController = rememberNavController()
    val searchText by viewModel.searchText.collectAsState()
    val isExpanded by viewModel.isExpanded.collectAsState()
    val postList by viewModel.postList.collectAsState()
    val isLoading by viewModel.isLoading.observeAsState(true)

    NavHost(
        navController = nestedNavController,
        startDestination = "exploreList"
    ) {
        // Post explore Screen
        composable("exploreList") {
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
                                nestedNavController.navigate("search/${searchText}")
                            },
                            expanded = isExpanded,
                            onExpandedChange = { viewModel.onExpandedChange(it) },
                            placeholder = { Text("Search") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Search"
                                )
                            },
                            trailingIcon = {
                                if (isExpanded) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Close search",
                                        modifier = Modifier
                                            .clickable(onClick = { viewModel.onExpandedChange((false)) })
                                    )
                                }
                            }
                        )
                    },
                    expanded = isExpanded,
                    onExpandedChange = { viewModel.onExpandedChange(it) }
                ) {}

                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(Alignment.Center)
                            .padding(16.dp)
                    )
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(40.dp),
                ) {
                    items(postList) { (title, posts) ->
                        Column(verticalArrangement = Arrangement.spacedBy(21.dp)) {
                            Text(title, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                items(posts) { post ->
                                    PostPreview(post, onClick = {
                                        nestedNavController.navigate("post_detail/${post.postId}")
                                    })
                                }
                            }
                        }
                    }
                }
            }
        }

        // Post Detail Screen
        composable(
            route = "post_detail/{postId}",
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: "Unknown"
            val postDetailViewModel: PostDetailViewModel = viewModel(factory = PostDetailViewModelFactory(postId))
            PostDetailScreen(postDetailViewModel, nestedNavController)
        }

        composable(
            route = "search/{searchText}",
        ) { backStackEntry ->
            val search = backStackEntry.arguments?.getString("searchText") ?: ""
            val searchViewModel: SearchViewModel = viewModel(factory = SearchViewModelFactory(search))
            SearchScreen(searchViewModel, parentNavController, nestedNavController)
        }
    }
    }

