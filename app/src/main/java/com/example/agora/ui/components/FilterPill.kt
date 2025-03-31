package com.example.agora.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FilterPill(text: String, onClick: () -> Unit, selected: Boolean) {
    val buttonColors =
        if (selected)
            ButtonColors(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.background,
                MaterialTheme.colorScheme.surfaceVariant,
                MaterialTheme.colorScheme.background
            )
        else
            ButtonColors(
                MaterialTheme.colorScheme.tertiaryContainer,
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.surfaceVariant,
                MaterialTheme.colorScheme.background
            )

    Button(
        onClick = onClick,
        contentPadding = PaddingValues(16.dp, 0.dp),
        colors = buttonColors
    ) {
        Text(text = text, fontSize = 13.sp)
    }
}