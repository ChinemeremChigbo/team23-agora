package com.example.agora.screens.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.agora.model.data.Category
import com.example.agora.model.data.Post
import com.example.agora.model.repository.SearchFilterUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SearchViewModel(initialSearchText: String = ""): ViewModel() {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts = _posts.asStateFlow()

    private val _searchText = MutableStateFlow<String>(initialSearchText)
    val searchText = _searchText.asStateFlow()

    private val _isExpanded = MutableStateFlow(false)
    val isExpanded = _isExpanded.asStateFlow()

    private val _recentSearches = MutableStateFlow<List<String>>(listOf(initialSearchText))
    val recentSearches = _recentSearches.asStateFlow()

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    fun changeCategory(category: Category?) {
        _selectedCategory.value = category
        fetchResults()
    }

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    fun onExpandedChange(expanded: Boolean) {
        _isExpanded.value = expanded
    }

    fun onSearchSubmitted(query: String) {
        val trimmedQuery = query.trim().lowercase()
        if (query.isNotBlank() && !_recentSearches.value.contains(trimmedQuery)) {
            _recentSearches.value =
                listOf(trimmedQuery) + _recentSearches.value.take(4) // Store up to 5 recent searches
        }
        _searchText.value = query
        _isExpanded.value = false
        fetchResults()
    }

    init {
        fetchResults()
    }

    fun fetchResults() {
        Log.i(_selectedCategory.value?.value, "selected cat")
        SearchFilterUtils.getPosts(
            category = _selectedCategory.value,
            searchString = _searchText.value
        ) { posts ->
            _posts.value = posts.map { post -> Post.convertDBEntryToPostDetail(post)}
        }
    }
}

class SearchViewModelFactory(private val searchText: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SearchViewModel(searchText) as T
    }
}