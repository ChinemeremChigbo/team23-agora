package com.chinemerem.agora.screens.search

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.chinemerem.agora.model.data.Category
import com.chinemerem.agora.model.repository.SortOptions
import com.chinemerem.agora.screens.BottomNavItem
import com.chinemerem.agora.ui.components.BasicPostGrid
import com.chinemerem.agora.ui.components.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = viewModel(),
    parentNavController: NavController,
    screenNavController: NavController
) {
    val searchText by viewModel.searchText.collectAsState()
    val posts by viewModel.posts.collectAsState()
    val isLoading by viewModel.isLoading.observeAsState(true)
    val isExpanded by viewModel.isExpanded.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val sortBy by viewModel.sortBy.collectAsState()

    val tabs = listOf("All") + Category.entries.map { it.value }
    val initialIndex = tabs.indexOf(selectedCategory?.value)

    var sortDropdownExpanded by remember { mutableStateOf(false) }
    var editingFilters by remember { mutableStateOf(false) }
    var tabIndex by remember {
        mutableStateOf(
            if (initialIndex >= 0) initialIndex else 0
        )
    }
    val context = LocalContext.current

    if (editingFilters) {
        FilterScreen(viewModel, { editingFilters = false })
    } else {
        Column(
            modifier = Modifier.padding(top = 21.dp, bottom = 0.dp, start = 21.dp, end = 21.dp)
        ) {
            SearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp)),
                colors = SearchBarDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                inputField = {
                    SearchBarDefaults.InputField(
                        query = searchText,
                        onQueryChange = viewModel::onSearchTextChange,
                        onSearch = {
                            if (searchText.trim().isEmpty()) {
                                Toast.makeText(
                                    context,
                                    "Search text can't be empty!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                viewModel.onSearchSubmitted(searchText)
                            }
                        },
                        expanded = isExpanded,
                        onExpandedChange = { viewModel.onExpandedChange(it) },
                        placeholder = { Text("Search") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        trailingIcon = {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close search",
                                modifier = Modifier.clickable(
                                    onClick = {
                                        if (isExpanded) {
                                            viewModel.onExpandedChange((false))
                                        } else {
                                            // clear navigation path so that swiping back do not return to search result
                                            screenNavController.popBackStack(
                                                screenNavController.graph.startDestinationId,
                                                inclusive = true
                                            )
                                            parentNavController.navigate(
                                                BottomNavItem.Explore.route
                                            ) {
                                                popUpTo(
                                                    parentNavController.graph.startDestinationId
                                                ) { inclusive = false }
                                            }
                                        }
                                    }
                                )
                            )
                        }
                    )
                },
                expanded = isExpanded,
                onExpandedChange = { viewModel.onExpandedChange(it) }
            ) {}

            ScrollableTabRow(
                selectedTabIndex = tabIndex,
                contentColor = MaterialTheme.colorScheme.surfaceVariant,
                containerColor = MaterialTheme.colorScheme.background,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[tabIndex]),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        height = 2.dp
                    )
                },
                edgePadding = 0.dp,
                modifier = Modifier.height(80.dp)
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(selected = tabIndex == index, onClick = {
                        tabIndex = index
                        viewModel.changeCategory(Category.entries.find { it.value == title })
                    }, text = { Text(title, Modifier.padding(horizontal = 4.dp)) })
                }
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box {
                    OutlinedButton(
                        onClick = { sortDropdownExpanded = true },
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(15.dp, 10.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Icon(
                                Icons.Default.SwapVert,
                                tint = MaterialTheme.colorScheme.surfaceVariant,
                                contentDescription = "Sort arrow"
                            )
                            Text(text = "Sort: ", color = MaterialTheme.colorScheme.onBackground)
                            Text(
                                text = sortBy.value,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                    DropdownMenu(
                        expanded = sortDropdownExpanded,
                        onDismissRequest = { sortDropdownExpanded = false },
                        containerColor = MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        SortOptions.entries.forEach { entry ->
                            DropdownMenuItem(
                                text = { Text(entry.value) },
                                onClick = {
                                    viewModel.changeSort(entry)
                                    sortDropdownExpanded = false
                                },
                                contentPadding = PaddingValues(15.dp, 10.dp)
                            )
                            HorizontalDivider()
                        }
                    }
                }

                OutlinedButton(
                    onClick = { editingFilters = true },
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(15.dp, 10.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(
                            Icons.AutoMirrored.Default.Sort,
                            tint = MaterialTheme.colorScheme.surfaceVariant,
                            contentDescription = "Sort arrow"
                        )
                        Text(text = "Filter", color = MaterialTheme.colorScheme.onBackground)
                    }
                }
            }

            Spacer(Modifier.size(16.dp))

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.Center)
                        .padding(16.dp)
                )
            } else if (posts.isEmpty()) {
                EmptyState(title = null, msg = "No results found", icon = Icons.Default.Search)
            } else {
                BasicPostGrid(posts, screenNavController)
            }
        }
    }
}
