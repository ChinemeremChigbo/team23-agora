package com.example.agora.screens.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.agora.model.data.Post
import com.example.agora.model.data.Category
import com.example.agora.model.data.PostUtils

class PostViewModel: ViewModel() {

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> get() = _posts

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    /** Fetch all posts from Firestore */
    private fun fetchPosts() {
        PostUtils.fetchPosts(
            onSuccess = { postList -> _posts.value = postList },
            onFailure = { e -> _error.value = "Error fetching posts: ${e.message}" }
        )
    }

    /** Create new post */
    fun createPost(
        title: String,
        description: String,
        price: Double,
        category: Category,
        images: List<String>,
        userId: String
    ) {
        val validationError = validatePost(title, description, price)
        if (validationError != null) {
            _error.value = validationError
            return
        }
        _error.value = null

        PostUtils.createPost(
            title, description, price, category, images, userId,
            onSuccess = { fetchPosts() }, // Refresh posts after adding
            onFailure = { e -> _error.value = "Failed to create post: ${e.message}" }
        )
    }

    /** Validate user input before creating post */
    private fun validatePost(title: String, description: String, price: Double): String? {
        return when {
            title.isBlank() -> "Title cannot be empty"
            description.isBlank() -> "Description cannot be empty"
            price < 0.0 -> "Price must be greater than zero"
            else -> null
        }
    }
}
