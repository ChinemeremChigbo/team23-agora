package com.example.agora.screens.postDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.agora.model.data.Post
import com.example.agora.model.data.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PostDetailViewModel (
   postId: String
): ViewModel() {

    private val _post = MutableStateFlow<Post?>(null)
    val post = _post.asStateFlow()

    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    init {
        fetchPostDetails(postId)
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
}

class PostDetailViewModelFactory(private val postId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PostDetailViewModel(postId) as T
    }
}