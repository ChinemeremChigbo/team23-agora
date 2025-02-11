package com.example.agora.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.agora.R
import com.example.agora.screens.explore.ExploreFragment
import com.example.agora.screens.explore.ExploreScreen

@Composable
fun BottomNavigation(
    navController: NavController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val items = listOf(
        BottomNavItem.Explore,
        BottomNavItem.Post,
        BottomNavItem.Wishlist,
        BottomNavItem.Inbox,
        BottomNavItem.Setting
    )

    NavigationBar {
        items.forEach { item ->
            AddItem(
                screen = item,
                currentRoute = currentRoute ?: "/explore",
                navController = navController,
            )
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: BottomNavItem,
    currentRoute: String,
    navController: NavController
) {
    NavigationBarItem(
        // Text that shows bellow the icon
        label = {
            Text(text = screen.title)
        },

        // The icon resource
        icon = {
            Icon(
                painterResource(id = screen.icon),
                contentDescription = screen.title
            )
        },

        // Display if the icon it is select or not
        selected = currentRoute == screen.route,

        // Always show the label bellow the icon or not
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
        colors = NavigationBarItemDefaults.colors()
    )
}

sealed class BottomNavItem(
    var title: String,
    var icon: Int,
    var route: String
) {
    object Explore :
        BottomNavItem(
            "Explore",
            R.drawable.ic_explore,
            "/explore"
        )

    object Post :
        BottomNavItem(
            "Post",
            R.drawable.ic_post,
            "/post"
        )

    object Wishlist :
        BottomNavItem(
            "Wishlist",
            R.drawable.ic_wishlist,
            "/wishlist"
        )

    object Inbox :
        BottomNavItem(
            "Inbox",
            R.drawable.ic_comments,
            "/inbox"
        )

    object Setting :
        BottomNavItem(
            "Setting",
            R.drawable.ic_settings,
            "/setting"
        )
}