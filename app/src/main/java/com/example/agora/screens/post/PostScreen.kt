package com.example.agora.screens.post

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun PostScreen(
    parentNavController: NavController,
    viewModel: PostViewModel = viewModel(),
) {
    val userPosts by viewModel.userPosts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val nestedNavController = rememberNavController()
    NavHost(
        navController = nestedNavController,
        startDestination = "postList"
    ) {
        // Post List Screen
        composable("postList") {
            Column(
                modifier = Modifier.padding(top = 21.dp, bottom = 0.dp, start = 21.dp, end = 21.dp),
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Box(modifier = Modifier.width(120.dp))

                    Text(
                        text = "Post",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )

                    TextButton(
                        onClick = { nestedNavController.navigate("createPost") },
                        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp),
                        modifier = Modifier.width(120.dp)
                    ) {
                        Text(
                            text = "Create Post",
                            fontSize = 15.sp,
                        )
                    }
                }

            }
        }
        composable("createPost") {
            val createPostViewModel: CreatePostViewModel = viewModel()
            CreatePostScreen(nestedNavController, createPostViewModel)
        }
    }
}
