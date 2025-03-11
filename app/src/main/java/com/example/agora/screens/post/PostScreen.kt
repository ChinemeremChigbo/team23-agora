package com.example.agora.screens.post

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.agora.screens.postDetail.PostDetailScreen
import com.example.agora.screens.postDetail.PostDetailViewModel
import com.example.agora.screens.postDetail.PostDetailViewModelFactory
import com.example.agora.screens.postEdit.PostEditScreen
import com.example.agora.screens.postEdit.PostEditViewModel
import com.example.agora.screens.postEdit.PostEditViewModelFactory
import com.example.agora.ui.components.BasicPostGrid

@Composable
fun PostScreen(
    parentNavController: NavController,
    viewModel: PostViewModel = viewModel(),
) {
    val userPosts by viewModel.userPosts.collectAsState()
    val isLoading by viewModel.isLoading.observeAsState(true)
    val nestedNavController = rememberNavController()

    NavHost(
        navController = nestedNavController,
        startDestination = "postList"
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
                    modifier = Modifier.fillMaxWidth().height(30.dp),
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

                Spacer(Modifier.size(40.dp))

                BasicPostGrid(
                    userPosts,
                    nestedNavController,
                    { modifier, post -> PostMenu(modifier, post, nestedNavController) }
                )
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
        // Edit Post Screen
        composable(
            route = "post_edit/{postId}"
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: "Unknown"
            val application = LocalContext.current.applicationContext as Application
            val postEditViewModel: PostEditViewModel = viewModel(factory = PostEditViewModelFactory(application, postId))
            PostEditScreen(nestedNavController, postEditViewModel)
        }
    }
}


@Composable
fun PostMenu(modifier: Modifier, post: Post, navController: NavController) {
    var expanded by remember { mutableStateOf(false) }
    Box (
        modifier = modifier
    ) {
        IconButton(
            onClick = { expanded = !expanded },
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                )
                .size(35.dp)
                .padding(3.dp)
                .align(Alignment.TopEnd)
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Edit button",
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            DropdownMenuItem(
                text = { Text("Edit") },
                onClick = {
                    expanded = false
                    navController.navigate("post_edit/${post.postId}")
                }
            )
            DropdownMenuItem(
                text = { Text("Mark Resolved") },
                onClick = {
                    expanded = false
                    // todo: add dialogue, functionality
                }
            )
            DropdownMenuItem(
                text = { Text("Delete") },
                onClick = {
                    expanded = false
                    // todo: add dialogue, functionality
                }
            )
        }
    }
}
