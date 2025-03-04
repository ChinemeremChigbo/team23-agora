package com.example.agora.screens.wishlist

import androidx.lifecycle.ViewModel
import com.example.agora.model.data.Post
import com.example.agora.model.repository.WishlistUtils
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class WishlistViewModel : ViewModel() {
    val currentUser = FirebaseAuth.getInstance().currentUser

    private val _posts = MutableStateFlow<List<Post>>(listOf(
        Post(title="Fridge", price=10.12),
        Post(title="Fridge 2", price=100.12),
        Post(title="Fridge 3", price=121.00)
    ))
    val posts = _posts.asStateFlow()

    init {
        currentUser?.uid?.let {
            WishlistUtils.getWishList(currentUser.uid) { posts ->

            }
        }
    }
}
