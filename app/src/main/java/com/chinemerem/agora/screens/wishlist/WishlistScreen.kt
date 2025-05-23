package com.chinemerem.agora.screens.wishlist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chinemerem.agora.screens.BottomNavItem
import com.chinemerem.agora.screens.postDetail.PostDetailScreen
import com.chinemerem.agora.screens.postDetail.PostDetailViewModel
import com.chinemerem.agora.screens.postDetail.PostDetailViewModelFactory
import com.chinemerem.agora.ui.components.BasicPostGrid
import com.chinemerem.agora.ui.components.EmptyState

@Composable
fun WishlistScreen(viewModel: WishlistViewModel = viewModel(), parentNavController: NavController) {
    val posts by viewModel.posts.collectAsState()
    val nestedNavController = rememberNavController()

    NavHost(
        navController = nestedNavController,
        startDestination = "wishlist"
    ) {
        // Post wishlist Screen
        composable("wishlist") {
            LaunchedEffect(nestedNavController.currentBackStackEntry) {
                viewModel.fetchWishlist()
            }

            Column(
                modifier = Modifier
                    .padding(top = 21.dp, bottom = 0.dp, start = 21.dp, end = 21.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.height(30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Wishlist",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.size(40.dp))

                if (posts.isEmpty()) {
                    EmptyState(
                        title = "Your wishlist is empty",
                        msg = "Like a post to add it to your wishlist",
                        icon = Icons.Default.Image
                    ) {
                        Button(
                            onClick = { parentNavController.navigate(BottomNavItem.Explore.route) },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(text = "Explore posts")
                        }
                    }
                } else {
                    BasicPostGrid(posts, nestedNavController)
                }
            }
        }
        // Post Detail Screen
        composable(
            route = "post_detail/{postId}"
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: "Unknown"
            val postDetailViewModel: PostDetailViewModel = viewModel(
                factory = PostDetailViewModelFactory(postId)
            )
            PostDetailScreen(postDetailViewModel, nestedNavController)
        }
    }
}
