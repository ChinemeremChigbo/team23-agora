package com.example.agora.screens

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.agora.R

@Composable
fun BottomNavigation(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val items = listOf(
        BottomNavItem.Explore,
        BottomNavItem.Post,
        BottomNavItem.Wishlist,
        BottomNavItem.Inbox,
        BottomNavItem.Settings
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        items.forEach { item ->
            AddItem(
                screen = item,
                currentRoute = currentRoute ?: "/explore",
                navController = navController
            )
        }
    }
}

@Composable
fun RowScope.AddItem(screen: BottomNavItem, currentRoute: String, navController: NavController) {
    val settingsSubRoutes = listOf("update_password", "appearance", "profile")

    val isSettingsSelected =
        screen is BottomNavItem.Settings && (
            currentRoute == screen.route || settingsSubRoutes.contains(
                currentRoute
            )
            )

    NavigationBarItem(
        // Text that shows below the icon
        label = {
            Text(text = screen.title)
        },

        // The icon resource
        icon = {
            Icon(
                painterResource(id = screen.icon),
                contentDescription = screen.title,
                modifier = Modifier.size(30.dp)
            )
        },

        // Display if the icon it is select or not

        selected = if (screen is BottomNavItem.Settings) {
            isSettingsSelected
        } else {
            currentRoute == screen.route
        },
        // Always show the label below the icon or not
        alwaysShowLabel = false,

        // Click listener for the icon
        onClick = {
            if (currentRoute != screen.route) {
                navController.navigate(screen.route) {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        },

        // Control all the colors of the icon
        colors = NavigationBarItemDefaults.colors(
            indicatorColor = Color.Transparent
        )
    )
}

sealed class BottomNavItem(
    var title: String,
    var icon: Int,
    var route: String
) {
    data object Explore : BottomNavItem(
        "Explore",
        R.drawable.ic_explore,
        "/explore"
    )

    data object Post : BottomNavItem(
        "Post",
        R.drawable.ic_post,
        "/post"
    )

    data object Wishlist : BottomNavItem(
        "Wishlist",
        R.drawable.ic_wishlist,
        "/wishlist"
    )

    data object Inbox : BottomNavItem(
        "Inbox",
        R.drawable.ic_comments,
        "/inbox"
    )

    data object Settings : BottomNavItem(
        "Settings",
        R.drawable.ic_settings,
        "/settings"
    )
}
