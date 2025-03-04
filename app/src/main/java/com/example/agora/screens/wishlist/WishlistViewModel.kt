package com.example.agora.screens.wishlist

import androidx.lifecycle.ViewModel
import com.example.agora.model.data.Post
import com.example.agora.model.repository.WishlistUtils
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class WishlistViewModel : ViewModel() {
    val currentUser = FirebaseAuth.getInstance().currentUser

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts = _posts.asStateFlow()

    init {
        fetchWishlist()
    }

    fun fetchWishlist() {
        currentUser?.uid?.let {
            WishlistUtils.getWishList(currentUser.uid) { posts ->
                _posts.value = posts.map { post -> Post.convertDBEntryToPostDetail(post)}
            }
        }
    }
}
