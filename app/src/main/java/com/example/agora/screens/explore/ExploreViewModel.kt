package com.example.agora.screens.explore

import androidx.lifecycle.ViewModel
import com.example.agora.model.data.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ExploreViewModel : ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isExpanded = MutableStateFlow(false)
    val isExpanded = _isExpanded.asStateFlow()

    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches = _recentSearches.asStateFlow()

    private val titles1 = listOf(
        Post(title="Fridge", price=10.12),
        Post(title="Fridge 2", price=100.12),
        Post(title="Fridge 3", price=121.00)
    )
    private val titles2 = listOf(
        Post(title="Book", price=7.99),
        Post(title="Book 2", price=9.50),
        Post(title="Book 3", price=10.25)
    )
    private val titles3 = listOf(Post(title="Plates", price=11.99))

    private val _sections = MutableStateFlow<List<List<Post>>>(listOf(titles1, titles2, titles3))
    val sections = _sections.asStateFlow()

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    fun onExpandedChange(expanded: Boolean) {
        _isExpanded.value = expanded
    }

    fun onSearchSubmitted(query: String) {
        if (query.isNotBlank() && !_recentSearches.value.contains(query)) {
            _recentSearches.value = listOf(query) + _recentSearches.value.take(4) // Store up to 5 recent searches
        }
        _searchText.value = query
        _isExpanded.value = false
    }
}
