package com.example.agora.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.agora.model.data.Post

@Composable
fun BasicPostGrid(posts: List<Post>, navController: NavController, route: String, additionalContent: @Composable ((Modifier) -> Unit)? = null) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2), // 2 columns
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(posts) { post ->
            PostPreview(
                post,
                onClick = {
                    navController.navigate("${route}/${post.postId}")
                },
                additionalContent
            )
        }
    }
}
