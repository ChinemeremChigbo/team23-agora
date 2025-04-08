package com.agora.agora.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.agora.agora.model.data.Post

abstract class PostPreview(
    val post: Post,
    val onClick: () -> Unit
) {
    @Composable
    abstract fun DisplayPreview()
}

class DefaultPostPreview(
    post: Post,
    onClick: () -> Unit
) : PostPreview(post, onClick) {
    @Composable
    override fun DisplayPreview() {
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
    }
}

abstract class PostPreviewDecorator(protected val component: PostPreview) : PostPreview(
    component.post,
    component.onClick
) {
    @Composable
    override fun DisplayPreview() {
        component.DisplayPreview()
    }
}

class AddMenu(
    component: PostPreview,
    private val menu: @Composable (Modifier, Post) -> Unit
) : PostPreviewDecorator(component) {
    @Composable
    override fun DisplayPreview() {
        Box {
            super.DisplayPreview()
            menu(
                Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (10).dp, y = (-5).dp),
                component.post
            )
        }
    }
}

class AddSoldBanner(
    component: PostPreview
) : PostPreviewDecorator(component) {
    @Composable
    override fun DisplayPreview() {
        Box {
            super.DisplayPreview()
            Text(
                text = "SOLD",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}
