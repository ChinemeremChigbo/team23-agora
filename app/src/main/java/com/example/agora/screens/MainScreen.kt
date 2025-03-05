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
import androidx.navigation.compose.rememberNavController
import com.example.agora.screens.explore.ExploreScreen
import com.example.agora.screens.explore.ExploreViewModel
import com.example.agora.screens.inbox.InboxScreen
import com.example.agora.screens.post.CreatePostScreen
import com.example.agora.screens.post.PostScreen
import com.example.agora.screens.post.CreatePostViewModel
import com.example.agora.screens.postDetail.PostDetailScreen
import com.example.agora.screens.postDetail.PostDetailViewModel
import com.example.agora.screens.postDetail.PostDetailViewModelFactory
import com.example.agora.screens.search.SearchScreen
import com.example.agora.screens.search.SearchViewModel
import com.example.agora.screens.search.SearchViewModelFactory
import com.example.agora.screens.settings.SettingsScreen
import com.example.agora.screens.wishlist.WishlistScreen
import com.example.agora.screens.wishlist.WishlistViewModel
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
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Explore.route,
        modifier = modifier
    ) {
        composable(BottomNavItem.Explore.route) {
            val exploreViewModel: ExploreViewModel = viewModel()
            ExploreScreen(exploreViewModel, navController)
        }
        composable(
            route = "search/{searchText}",
        ) { backStackEntry ->
            val search = backStackEntry.arguments?.getString("searchText") ?: ""
            val searchViewModel: SearchViewModel = viewModel(factory = SearchViewModelFactory(search))
            SearchScreen(searchViewModel, navController)
        }
        composable(
            route = "post_detail/{postId}",
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: "Unknown"
            val postDetailViewModel: PostDetailViewModel = viewModel(factory = PostDetailViewModelFactory(postId))
            PostDetailScreen(postDetailViewModel, navController)
        }
        composable(BottomNavItem.Post.route) { PostScreen(navController) }
        composable("createPost") {
            val createPostViewModel: CreatePostViewModel = viewModel()
            CreatePostScreen(navController, createPostViewModel)
        }
        composable(BottomNavItem.Wishlist.route) {
            val wishlistViewModel: WishlistViewModel = viewModel()
            WishlistScreen(wishlistViewModel, navController)
        }
        composable(BottomNavItem.Inbox.route) { InboxScreen() }
        composable(BottomNavItem.Settings.route) { SettingsScreen(auth) }
    }
}