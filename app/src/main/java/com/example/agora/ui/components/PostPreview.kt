package com.example.agora.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.agora.model.data.Post

@Composable
fun PostPreview(
    post: Post,
    onClick: () -> Unit,
    additionalContent: @Composable ((Modifier, Post) -> Unit)? = null
) {
    Box {
        Column(
            Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surface)
                .width(221.dp)
                .clickable(onClick = { onClick() })
        ) {
            AsyncImage(
                model = post.images[0],
                contentDescription = "Post Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 10.dp,
                            topEnd = 10.dp
                        )
                    )
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier.padding(21.dp)
            ) {
                Text(
                    post.title,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.width(200.dp)
                )
                Text(
                    "$" + String.format("%.2f", post.price),
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (additionalContent != null) {
            additionalContent(
                Modifier.align(Alignment.TopEnd).offset(x = (-5).dp, y = 5.dp),
                post
            )
        }
    }
}
