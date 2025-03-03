package com.example.agora.screens.postDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.agora.model.data.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PostDetailViewModel (
   postId: String
): ViewModel() {

    private val _post = MutableStateFlow<Post?>(null)
    val post = _post.asStateFlow()

    init {
        fetchPostDetails(postId)
    }
    private fun fetchPostDetails(postId: String) {
        // TODO (for eddie, from jennifer) - query the post based off postId
        // will also need a separate query for the user details to display in the user section
        _post.value = Post(postId = postId, title = "hehe")
    }
}

class PostDetailViewModelFactory(private val postId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PostDetailViewModel(postId) as T
    }
}