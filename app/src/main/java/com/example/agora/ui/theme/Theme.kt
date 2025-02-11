package com.example.agora.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun AgoraTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        typography = AgoraTypography,
        content = content
    )
}
