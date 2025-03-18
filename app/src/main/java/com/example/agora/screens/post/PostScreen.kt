package com.example.agora.screens.post

import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.agora.model.data.Post
import com.example.agora.model.data.PostStatus
import com.example.agora.screens.postDetail.PostDetailScreen
import com.example.agora.screens.postDetail.PostDetailViewModel
import com.example.agora.screens.postDetail.PostDetailViewModelFactory
import com.example.agora.screens.postEdit.PostEditScreen
import com.example.agora.screens.postEdit.PostEditViewModel
import com.example.agora.screens.postEdit.PostEditViewModelFactory
import com.example.agora.ui.components.BasicPostGrid
import com.example.agora.ui.components.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreen(
    parentNavController: NavController,
    viewModel: PostViewModel = viewModel(),
) {
    val activePosts by viewModel.activePosts.collectAsState()
    val resolvedPosts by viewModel.resolvedPosts.collectAsState()
    val isLoading by viewModel.isLoading.observeAsState(true)
    val isRefreshing by viewModel.isRefreshing.observeAsState(true)
    val nestedNavController = rememberNavController()

    NavHost(
        navController = nestedNavController, startDestination = "postList"
    ) {
        // Post List Screen
        composable("postList") {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.Center)
                        .padding(16.dp)
                )
            }
            Column(
                modifier = Modifier.padding(top = 21.dp, bottom = 0.dp, start = 21.dp, end = 21.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Box(modifier = Modifier.width(100.dp))

                    Text(
                        text = "My Posts",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )

                    TextButton(
                        onClick = { nestedNavController.navigate("post_edit/") },
                        modifier = Modifier.width(100.dp),
                        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp),
                    ) {
                        Text(
                            text = "Create Post",
                            fontSize = 15.sp,
                        )
                    }
                }

                Spacer(Modifier.size(20.dp))

                var selectedOption by remember { mutableStateOf(PostStatus.ACTIVE) }
                SegmentedControl(
                    options = listOf(PostStatus.ACTIVE, PostStatus.RESOLVED),
                    selectedOption = selectedOption,
                    onOptionSelected = { selectedOption = it })

                Spacer(Modifier.size(20.dp))

                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = {
                        viewModel.refreshPosts()
                    },
                ) {
                    // Post Grid
                    if (selectedOption == PostStatus.ACTIVE) {
                        if (activePosts.isEmpty()) {
                            EmptyState(
                                title = "Nothing here for now",
                                msg = "This is where you'll find your active posts",
                                icon = Icons.Default.Image
                            ) {
                                Button(
                                    onClick = { nestedNavController.navigate("post_edit/") }) {
                                    Text(text = "Create post")
                                }
                            }
                        } else {
                            BasicPostGrid(
                                activePosts, nestedNavController, { modifier, post ->
                                    PostMenu(
                                        modifier, post, nestedNavController, viewModel
                                    )
                                })
                        }
                    } else {
                        if (resolvedPosts.isEmpty()) {
                            EmptyState(
                                title = "Nothing here for now",
                                msg = "This is where you'll find your resolved posts",
                                icon = Icons.Default.Image
                            ) {
                                Button(
                                    onClick = { selectedOption = PostStatus.ACTIVE }) {
                                    Text(text = "See active posts")
                                }
                            }
                        } else {
                            BasicPostGrid(
                                resolvedPosts,
                                nestedNavController,
                            )
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
            val postDetailViewModel: PostDetailViewModel =
                viewModel(factory = PostDetailViewModelFactory(postId))
            PostDetailScreen(postDetailViewModel, nestedNavController)
        }
        // Edit Post Screen
        composable(
            route = "post_edit/{postId}"
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: "Unknown"
            val application = LocalContext.current.applicationContext as Application
            val postEditViewModel: PostEditViewModel =
                viewModel(factory = PostEditViewModelFactory(application, postId))
            PostEditScreen(nestedNavController, postEditViewModel)
        }
    }
}

@Composable
fun PostMenu(
    modifier: Modifier,
    post: Post,
    navController: NavController,
    postViewModel: PostViewModel,
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
    ) {
        IconButton(
            onClick = { expanded = !expanded }, modifier = Modifier.size(48.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(20.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.2f))
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More options",
                    modifier = Modifier.size(30.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            DropdownMenuItem(text = { Text("Edit") }, onClick = {
                expanded = false
                navController.navigate("post_edit/${post.postId}")
            })
            DropdownMenuItem(text = { Text("Mark Resolved") }, onClick = {
                expanded = false
                postViewModel.resolvePost(postId = post.postId, onSuccess = {
                    Toast.makeText(
                        context, "Post resolved successfully!", Toast.LENGTH_SHORT
                    ).show()
                }, onError = { errorMessage ->
                    Toast.makeText(context, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
                })
            })
            DropdownMenuItem(text = { Text("Delete") }, onClick = {
                expanded = false
                postViewModel.deletePost(postId = post.postId, onSuccess = {
                    Toast.makeText(
                        context, "Post deleted successfully!", Toast.LENGTH_SHORT
                    ).show()
                }, onError = { errorMessage ->
                    Toast.makeText(context, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
                })
            })
        }
    }
}

@Composable
fun SegmentedControl(
    options: List<PostStatus>, selectedOption: PostStatus, onOptionSelected: (PostStatus) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(20.dp))
            .padding(4.dp), horizontalArrangement = Arrangement.Center
    ) {
        options.forEach { option ->
            val isSelected = option == selectedOption
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.background else Color.Transparent
                    )
                    .clickable { onOptionSelected(option) }
                    .padding(vertical = 7.dp),
                contentAlignment = Alignment.Center) {
                Text(
                    text = option.value,
                    fontSize = 15.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) Color.Black else Color.Gray,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}
