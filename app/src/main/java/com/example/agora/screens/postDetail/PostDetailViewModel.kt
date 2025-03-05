package com.example.agora.screens.postDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.agora.model.data.Post
import com.example.agora.model.data.User
import com.example.agora.model.repository.PostUtils
import com.example.agora.model.repository.ProfileSettingUtils
import com.example.agora.model.repository.WishlistUtils
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PostDetailViewModel (
   postId: String
): ViewModel() {
    val currentUser = FirebaseAuth.getInstance().currentUser

    private val _post = MutableStateFlow<Post?>(null)
    val post = _post.asStateFlow()

    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    private val _inWishlist = MutableStateFlow(false)
    val inWishlist = _inWishlist.asStateFlow()

    init {
        fetchPostDetails(postId)
        checkIfPostInWishlist(postId)
    }

    private fun fetchPostDetails(postId: String) {
        PostUtils.getPost(postId, { post ->
            _post.value = post
            ProfileSettingUtils.getUserById(post!!.userId, { user -> _user.value = user })
        })
    }

    fun checkIfPostInWishlist(postId: String) {
        currentUser?.uid?.let {
            WishlistUtils.isPostInWishlist(currentUser.uid, postId) { inWishlist ->
                _inWishlist.value = inWishlist
            }
        }
    }
}

class PostDetailViewModelFactory(private val postId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PostDetailViewModel(postId) as T
    }
}