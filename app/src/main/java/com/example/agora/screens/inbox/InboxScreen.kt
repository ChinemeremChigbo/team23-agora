package com.example.agora.screens.inbox

import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.agora.model.data.Notification

@Composable
fun InboxScreen() {
    val notif = Notification(
        targetUser = "user123",
        eventInfo = "bicycle 12345"
    )
    val notifications: List<Notification> = listOf(notif, notif, notif, notif)

    Column(
        modifier = Modifier.padding(top=21.dp, bottom=0.dp, start=21.dp, end=21.dp).fillMaxSize(),
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
                Notification(notification)
            }
        }
    }
}

@Composable
fun Notification(details: Notification) {
    Row(horizontalArrangement = Arrangement.spacedBy(21.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(21.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        AsyncImage(
            model = "https://picsum.photos/200",
            contentDescription = "Preview image",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .height(40.dp)
                .width(40.dp)
                .clip(RoundedCornerShape(12.dp))
        )

        Text(
            text = "New comment on post " + details.getEventInfo() + " from " + details.getTargetUser(),
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.surfaceVariant,
            lineHeight = 21.sp
        )
    }
}

