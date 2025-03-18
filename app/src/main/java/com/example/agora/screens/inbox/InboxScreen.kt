package com.example.agora.screens.inbox

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.agora.model.data.Notification
import com.example.agora.screens.postDetail.PostDetailScreen
import com.example.agora.screens.postDetail.PostDetailViewModel
import com.example.agora.screens.postDetail.PostDetailViewModelFactory

@Composable
fun InboxScreen(viewModel: InboxViewModel = viewModel(), parentNavController: NavController) {
    val nestedNavController = rememberNavController()
    val notifications by viewModel.notifications.collectAsState()

    NavHost(
        navController = nestedNavController,
        startDestination = "inboxList"
    ) {
        composable("inboxList") {
            LaunchedEffect(nestedNavController.currentBackStackEntry) {
                viewModel.getNotifications()
            }

            Column(
                modifier = Modifier.padding(top = 21.dp, bottom = 0.dp, start = 21.dp, end = 21.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier.height(30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Inbox",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.size(40.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(notifications) { notification ->
                        NotificationItem(
                            notification,
                            { viewModel.viewNotification(notification, nestedNavController) })
                    }
                }
            }
        }

        composable(
            route = "post_detail/{postId}",
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: "Unknown"
            val postDetailViewModel: PostDetailViewModel = viewModel(factory = PostDetailViewModelFactory(postId))
            PostDetailScreen(postDetailViewModel, nestedNavController)
        }
    }
}

@Composable
fun NotificationItem(details: Notification, onClick: () -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(21.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(21.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            // TODO: have BE include this img in notif response
            model = "https://picsum.photos/200",
            contentDescription = "Preview image",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .height(40.dp)
                .width(40.dp)
                .clip(RoundedCornerShape(12.dp))
        )

        Text(
            text = details.message,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.surfaceVariant,
            lineHeight = 21.sp
        )
    }
}

