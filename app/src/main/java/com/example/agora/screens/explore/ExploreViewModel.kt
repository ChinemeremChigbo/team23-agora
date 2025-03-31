package com.example.agora.screens.explore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agora.model.data.Category
import com.example.agora.model.data.Post
import com.example.agora.model.repository.SearchFilterUtils
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExploreViewModel : ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isExpanded = MutableStateFlow(false)
    val isExpanded = _isExpanded.asStateFlow()

    private val _postList = MutableStateFlow<List<Pair<String, List<Post>>>>(listOf())
    val postList: StateFlow<List<Pair<String, List<Post>>>> = _postList.asStateFlow()

    private val _isLoading = MutableLiveData<Boolean>(true)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isRefreshing = MutableLiveData<Boolean>(false)
    val isRefreshing: LiveData<Boolean> get() = _isRefreshing

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    fun onExpandedChange(expanded: Boolean) {
        _isExpanded.value = expanded
    }

    init {
        viewModelScope.launch {
            try {
                val feed = getFeed() // Call the suspend function
                _postList.value =
                    feed.filter { it.second.isNotEmpty() } // Update LiveData with the result
                _isLoading.value = false
            } catch (e: Exception) {
                // Handle any exceptions that occur
                // TODO: add error screen component and display the component "oops something went wrong"
            }
        }
    }

    private suspend fun getFeed(): MutableList<Pair<String, List<Post>>> {
        val feed: MutableList<Pair<String, List<Post>>> = mutableListOf()

        return suspendCoroutine { continuation ->
            val remainingCalls = AtomicInteger(Category.entries.size)
            for (category in Category.entries) {
                feed.add(category.value to listOf())
                SearchFilterUtils.getPosts(
                    category = category,
                    limit = 5,
                    callback = { result ->
                        val posts: List<Post> = result.map { Post.convertDBEntryToPostPreview(it) }
                        // ensure order of adding feed items
                        val index = feed.indexOfFirst { it.first == category.value }
                        if (index != -1) {
                            feed[index] = category.value to posts
                        }
                        // If all callbacks have been completed, resume the continuation
                        if (remainingCalls.decrementAndGet() == 0) {
                            continuation.resume(feed)
                        }
                    }
                )
            }
        }
    }

    fun refreshFeed() {
        viewModelScope.launch {
            _isRefreshing.value = true
            delay(1000)
            val feed = getFeed()
            _postList.value = feed.filter { it.second.isNotEmpty() }
            _isRefreshing.value = false
        }
    }
}
