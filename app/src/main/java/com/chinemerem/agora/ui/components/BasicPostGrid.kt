package com.chinemerem.agora.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.chinemerem.agora.model.data.Post
import com.chinemerem.agora.model.data.PostStatus

@Composable
fun BasicPostGrid(
    posts: List<Post>,
    navController: NavController,
    menuContent: @Composable ((Modifier, Post) -> Unit)? = null
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2), // 2 columns
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(posts) { post ->
            var postPreviewItem: PostPreview = DefaultPostPreview(
                post,
                onClick = {
                    navController.navigate("post_detail/${post.postId}")
                }
            )

            if (menuContent != null) {
                postPreviewItem = AddMenu(postPreviewItem, menuContent)
            }

            if (post.status == PostStatus.RESOLVED) {
                postPreviewItem = AddSoldBanner(postPreviewItem)
            }

            postPreviewItem.DisplayPreview()
        }
    }
}
