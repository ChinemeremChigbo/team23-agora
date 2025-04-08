package com.agora.agora.screens.post

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agora.agora.model.data.Post
import com.agora.agora.model.data.PostStatus
import com.agora.agora.model.repository.PostRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PostViewModel : ViewModel() {

    private val _activePosts = MutableStateFlow<List<Post>>(emptyList())
    val activePosts: StateFlow<List<Post>> = _activePosts.asStateFlow()

    private val _resolvedPosts = MutableStateFlow<List<Post>>(emptyList())
    val resolvedPosts: StateFlow<List<Post>> = _resolvedPosts.asStateFlow()

    private val _isLoading = MutableLiveData<Boolean>(true)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isRefreshing = MutableLiveData<Boolean>(false)
    val isRefreshing: LiveData<Boolean> get() = _isRefreshing

    init {
        viewModelScope.launch {
            try {
                getPostsByUser()
                _isLoading.value = false
            } catch (e: Exception) {
                Log.e("PostViewModel", "Error fetching user posts", e)
                _isLoading.value = false
            }
        }
    }

    fun getPostsByUser() {
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid ?: return
        PostRepository.getPostsByUser(userId) { posts ->
            _activePosts.value = posts.filter { it.status == PostStatus.ACTIVE }
            _resolvedPosts.value = posts.filter { it.status == PostStatus.RESOLVED }
        }
    }

    fun deletePost(postId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            PostRepository.deletePost(postId = postId, onSuccess = {
//                    val updatedPosts = _activePosts.value.filter { it.postId != postId }
//                    _activePosts.value = updatedPosts
                onSuccess()
            }, onFailure = { e ->
                    Log.e("PostViewModel", "Failed to delete post: ${e.localizedMessage}")
                    onError(e.localizedMessage ?: "Failed to delete post")
                })
        }
    }

    fun resolvePost(postId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            PostRepository.resolvePost(postId = postId, onSuccess = {
                onSuccess()
            }, onFailure = { e ->
                    onError(e.localizedMessage ?: "Failed to resolve post")
                })
        }
    }

    private fun setRefreshing(value: Boolean) {
        _isRefreshing.value = value
    }

    fun refreshPosts() {
        viewModelScope.launch {
            setRefreshing(true)
            delay(1000)
            getPostsByUser()
            setRefreshing(false)
        }
    }
}
