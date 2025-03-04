package com.example.agora.screens.post
import com.example.agora.model.data.Post
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.agora.model.data.Category

class PostViewModel: ViewModel() {

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> get() = _posts

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    /** fetch all posts from Firestore */
    private fun fetchPosts() {
        Post.fetchPosts(
            onSuccess = { postList -> _posts.value = postList },
            onFailure = { e -> _error.value = "Error fetching posts: ${e.message}" }
        )
    }

    /** create new post */
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
        val newPost = Post()
        Post.createPost(
            title, description, price, category, images, userId,
            onSuccess = { fetchPosts() }, // Refresh posts after adding
            onFailure = { e -> _error.value = "Failed to create post: ${e.message}" }
        )
    }

    /** validate user input before creating post */
    private fun validatePost(title: String, description: String, price: Double): String? {
        return when {
            title.isBlank() -> "Title cannot be empty"
            description.isBlank() -> "Description cannot be empty"
            price < 0.0 -> "Price must be greater than zero"
            else -> null
        }
    }
}
