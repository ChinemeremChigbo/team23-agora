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

// TODO (jennifer) - replace w correct type
@Composable
fun PostPreview(title: String) {
    Column (
        Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.background)
            .clickable(onClick={})
    ) {
        Image(
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
            Text(title, fontSize = 16.sp)
            Text("$10.00", fontSize = 19.sp, fontWeight = FontWeight.Bold)
        }
    }
}