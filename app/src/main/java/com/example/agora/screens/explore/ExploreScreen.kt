package com.example.agora.screens.explore

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agora.ui.components.PostPreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen() {
    val textFieldState = rememberTextFieldState()
    var expanded: Boolean by rememberSaveable { mutableStateOf(false) }

    val titles = listOf("Fridge", "Fridge 2", "Fridge 3")
    Column (
        modifier = Modifier.padding(21.dp),
        verticalArrangement = Arrangement.spacedBy(40.dp),
    ) {
        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    //state = textFieldState,
                    query = textFieldState.toString(),
                    onQueryChange = {},
                    onSearch = { expanded = false },
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    placeholder = { Text("Hinted search text") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = { Icon(Icons.Default.MoreVert, contentDescription = null) },
                )
            },
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ){

        }
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 128.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(titles) { title ->
                PostPreview(title)
            }
        }
    }
//    Column(
//        modifier = Modifier.fillMaxSize(),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally,
//    ) {
//        Text(
//            text = "Explore Screen",
//            fontSize = 40.sp,
//            textAlign = TextAlign.Center
//        )
//    }
}

@Preview(showBackground = true)
@Composable
fun ExplorePreview() {
    ExploreScreen()
}

