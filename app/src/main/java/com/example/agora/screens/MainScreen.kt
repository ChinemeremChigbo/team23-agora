package com.example.agora.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.agora.screens.explore.ExploreScreen
import com.example.agora.screens.explore.ExploreViewModel
import com.example.agora.screens.inbox.InboxScreen
import com.example.agora.screens.inbox.InboxViewModel
import com.example.agora.screens.post.PostScreen
import com.example.agora.screens.post.PostViewModel
import com.example.agora.screens.postDetail.PostDetailScreen
import com.example.agora.screens.postDetail.PostDetailViewModel
import com.example.agora.screens.postDetail.PostDetailViewModelFactory
import com.example.agora.screens.settings.SettingsScreen
import com.example.agora.screens.wishlist.WishlistScreen
import com.example.agora.screens.wishlist.WishlistViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MainScreen(navController: NavHostController, auth: FirebaseAuth) {

    Scaffold(
        bottomBar = {
            BottomNavigation(navController)
        },
    ) {
            innerPadding ->
        Box(Modifier.fillMaxSize()) {
            NavigationHost(navController, auth, Modifier.padding(innerPadding))
        }
    }
}

@Composable
fun NavigationHost(
    navController: NavHostController,
    auth: FirebaseAuth,
    modifier: Modifier = Modifier
) {
    // This is NavHost for main screen navigation only
    //  DONOT use it for navigation inside each screen!!!
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Explore.route,
        modifier = modifier
    ) {
        composable(BottomNavItem.Explore.route) {
            val exploreViewModel: ExploreViewModel = viewModel()
            ExploreScreen(exploreViewModel, navController)
        }
        composable(BottomNavItem.Post.route) {
            val postViewModel: PostViewModel = viewModel()
            PostScreen(navController, postViewModel)
        }

        composable("post_detail/{postId}") { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: "Unknown"
            val postDetailViewModel: PostDetailViewModel =
                viewModel(factory = PostDetailViewModelFactory(postId))
            PostDetailScreen(postDetailViewModel, navController)
        }
        composable(BottomNavItem.Wishlist.route) {
            val wishlistViewModel: WishlistViewModel = viewModel()
            WishlistScreen(wishlistViewModel, navController)
        }
        composable(BottomNavItem.Inbox.route) {
            val inboxViewModel: InboxViewModel = viewModel()
            InboxScreen(inboxViewModel, navController)
        }
        composable(BottomNavItem.Settings.route) { SettingsScreen(auth, navController) }

    }
}