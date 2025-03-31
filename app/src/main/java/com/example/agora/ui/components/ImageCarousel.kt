package com.example.agora.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun ImageCarousel(images: List<String>) {
    val pagerState = rememberPagerState(pageCount = { images.size })

    if (images.size == 1) {
        AsyncImage(
            model = images[0],
            contentDescription = "Product image",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .background(Color.LightGray)
                .height(240.dp)
                .fillMaxWidth()
        )
    } else {
        Column {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .background(Color.LightGray)
                    .height(240.dp)
                    .fillMaxWidth()
            ) { page ->
                AsyncImage(
                    model = images[page],
                    contentDescription = "Carousel Image",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                repeat(images.size) { index ->
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(
                                if (pagerState.currentPage == index) Color.Black else Color.Gray
                            )
                    )
                }
            }
        }
    }
}
