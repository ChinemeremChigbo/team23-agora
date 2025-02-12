package com.example.agora.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agora.R
import com.example.agora.model.data.Post

@Composable
fun PostPreview(post: Post) {
    Column (
        Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)
            .width(221.dp)
            .clickable(onClick={})
    ) {
        Image(
            // TODO (jennifer) pull image from Post object
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "My Image",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .background(Color.LightGray)
                .height(160.dp)
                .fillMaxWidth()
        )
        Column (
            verticalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier.padding(21.dp)
        ) {
            Text(post.title, fontSize = 16.sp)
            Text("$" + String.format("%.2f", post.price), fontSize = 19.sp, fontWeight = FontWeight.Bold)
        }
    }
}