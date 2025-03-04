package com.example.agora.screens.post.viewmodel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.agora.screens.post.view.PostScreen

class PostViewModel : ViewModel() {
    // posts logic goes here
}


@Preview(showBackground = true)
@Composable
fun PostPreview() {
    val navController = rememberNavController()
    PostScreen(navController = navController)
}
