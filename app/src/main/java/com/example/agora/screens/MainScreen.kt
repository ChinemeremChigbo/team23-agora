package com.example.agora.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.agora.screens.explore.ExploreScreen
import com.example.agora.screens.notifications.NotificationScreen
import com.example.agora.screens.post.PostScreen
import com.example.agora.screens.settings.SettingScreen
import com.example.agora.screens.wishlist.WishlistScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomNavigation(navController)
        },
    ) {
            innerPadding ->
        NavigationHost(navController, Modifier.padding(innerPadding))
    }
}

@Composable
fun NavigationHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Explore.route,
        modifier = modifier
    ) {
        composable(BottomNavItem.Explore.route) { ExploreScreen() }
        composable(BottomNavItem.Post.route) { PostScreen() }
        composable(BottomNavItem.Wishlist.route) { WishlistScreen() }
        composable(BottomNavItem.Inbox.route) { NotificationScreen() }
        composable(BottomNavItem.Setting.route) { SettingScreen() }
    }
}