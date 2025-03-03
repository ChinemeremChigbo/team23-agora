package com.example.agora.screens.explore

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agora.model.data.Post
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExploreViewModel : ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isExpanded = MutableStateFlow(false)
    val isExpanded = _isExpanded.asStateFlow()

    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches = _recentSearches.asStateFlow()

    private val _postList = MutableStateFlow<List<Pair<String, List<Post>>>>(listOf())
    val postList: StateFlow<List<Pair<String, List<Post>>>> = _postList.asStateFlow()

    private val _isLoading = MutableLiveData<Boolean>(true)
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    fun onExpandedChange(expanded: Boolean) {
        _isExpanded.value = expanded
    }

    fun onSearchSubmitted(query: String) {
        if (query.isNotBlank() && !_recentSearches.value.contains(query)) {
            _recentSearches.value =
                listOf(query) + _recentSearches.value.take(4) // Store up to 5 recent searches
        }
        _searchText.value = query
        _isExpanded.value = false
    }

    init {
        viewModelScope.launch {
            try {
                val feed = Post.PostUtils.getFeed()  // Call the suspend function
                _postList.value = feed.filter { it.second.isNotEmpty() }  // Update LiveData with the result
                _isLoading.value = false
            } catch (e: Exception) {
                // Handle any exceptions that occur
            }
        }
    }
}
