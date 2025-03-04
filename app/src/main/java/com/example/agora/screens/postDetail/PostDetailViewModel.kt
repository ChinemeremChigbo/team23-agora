package com.example.agora.screens.postDetail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.agora.model.data.Post
import com.example.agora.model.data.User
import com.example.agora.model.repository.SearchFilterUtils
import com.example.agora.screens.authentication.sign_in.LoginImage
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
        // TODO (for eddie, from jennifer) - query the post based off postId
        // will also need a separate query for the user details to display in the user section
        _post.value = Post(postId = postId, title = "hehe")
        _user.value = User(
            fullName = "test user",
            bio = "Hi! I am a student at the University of Waterloo",
            email = "test@test.com",
            profileImage = "https://picsum.photos/200"
        )
    }

    fun checkIfPostInWishlist(postId: String) {
        Log.i(currentUser?.uid, "user id")
        currentUser?.uid?.let {
            _inWishlist.value = SearchFilterUtils.isPostInWishlist(currentUser.uid, postId)
            Log.i(_inWishlist.value.toString(), "in wishlist")
        }
    }
}

class PostDetailViewModelFactory(private val postId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PostDetailViewModel(postId) as T
    }
}