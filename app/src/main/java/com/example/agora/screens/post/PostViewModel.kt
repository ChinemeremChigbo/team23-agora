package com.example.agora.screens.post

import android.util.Log
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

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

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
}
