package com.example.agora.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.agora.screens.explore.ExploreScreen
import com.example.agora.screens.explore.ExploreViewModel
import com.example.agora.screens.inbox.InboxScreen
import com.example.agora.screens.post.CreatePostScreen
import com.example.agora.screens.post.PostScreen
import com.example.agora.screens.post.PostViewModel
import com.example.agora.screens.settings.SettingsScreen
import com.example.agora.screens.wishlist.WishlistScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MainScreen(auth: FirebaseAuth) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomNavigation(navController)
        },
    ) {
            innerPadding ->
        NavigationHost(navController, auth, Modifier.padding(innerPadding))
    }
}

@Composable
fun NavigationHost(navController: NavHostController, auth: FirebaseAuth, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Explore.route,
        modifier = modifier
    ) {
        composable(BottomNavItem.Explore.route) {
            val exploreViewModel: ExploreViewModel = viewModel()
            ExploreScreen(exploreViewModel)
        }
        composable("createPost") {
            val postViewModel: PostViewModel = viewModel()
            CreatePostScreen(navController, postViewModel, auth)
        }
        composable(BottomNavItem.Post.route) { PostScreen(navController) }
        composable(BottomNavItem.Wishlist.route) { WishlistScreen() }
        composable(BottomNavItem.Inbox.route) { InboxScreen() }
        composable(BottomNavItem.Settings.route) { SettingsScreen(auth) }
    }
}