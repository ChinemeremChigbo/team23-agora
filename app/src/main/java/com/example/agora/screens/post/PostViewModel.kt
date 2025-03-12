package com.example.agora.screens.post

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agora.model.data.Post
import com.example.agora.model.repository.PostUtils
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PostViewModel : ViewModel() {

    private val _userPosts = MutableStateFlow<List<Post>>(emptyList())
    val userPosts: StateFlow<List<Post>> = _userPosts.asStateFlow()

    private val _isLoading = MutableLiveData<Boolean>(true)
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        viewModelScope.launch {
            try {
                getPostsByUser()
            } catch (e: Exception) {
                Log.e("PostViewModel", "Error fetching user posts", e)
                _isLoading.value = false
            }
        }
    }

    private fun getPostsByUser() {
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid ?: return
        PostUtils.getPostsByUser(userId) { posts ->
            _userPosts.value = posts
            _isLoading.value = false
        }
    }

    fun deletePost(postId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            PostUtils.deletePost(
                postId = postId,
                onSuccess = {
                    val updatedPosts = _userPosts.value.filter { it.postId != postId }
                    _userPosts.value = updatedPosts
                    onSuccess()
                },
                onFailure = { e ->
                    Log.e("PostViewModel", "Failed to delete post: ${e.localizedMessage}")
                    onError(e.localizedMessage ?: "Failed to delete post")
                }
            )
        }
    }

    fun resolvePost(postId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            PostUtils.resolvePost(
                postId = postId,
                onSuccess = {
                    onSuccess()
                },
                onFailure = { e ->
                    onError(e.localizedMessage ?: "Failed to resolve post")
                }
            )
        }
    }

}
