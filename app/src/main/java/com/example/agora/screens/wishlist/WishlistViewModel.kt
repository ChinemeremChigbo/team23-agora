package com.example.agora.screens.wishlist

import androidx.lifecycle.ViewModel
import com.example.agora.model.data.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class WishlistViewModel : ViewModel() {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts = _posts.asStateFlow()
}
