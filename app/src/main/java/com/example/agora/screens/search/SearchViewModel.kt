package com.example.agora.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.agora.model.data.Category
import com.example.agora.model.data.Post
import com.example.agora.model.repository.SearchFilterUtils
import com.example.agora.model.repository.SortOptions
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

    private val _sortBy = MutableStateFlow<SortOptions>(SortOptions.NEWEST)
    val sortBy = _sortBy.asStateFlow()

    private val _selectedPriceIntervals = MutableStateFlow<List<String>>(emptyList())
    val selectedPriceIntervals = _selectedPriceIntervals.asStateFlow()

    fun changeCategory(category: Category?) {
        _selectedCategory.value = category
        fetchResults()
    }

    fun changeSort(sortBy: SortOptions) {
        _sortBy.value = sortBy
        fetchResults()
    }

    fun togglePriceInterval(interval: String) {
        if (_selectedPriceIntervals.value.contains(interval)) {
            _selectedPriceIntervals.value = _selectedPriceIntervals.value - interval
        } else {
            _selectedPriceIntervals.value = _selectedPriceIntervals.value + interval
        }
    }

    fun clearFilters() {
        _selectedPriceIntervals.value = emptyList()
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
        _posts.value = emptyList()
        if (_selectedPriceIntervals.value.isEmpty()) {
            SearchFilterUtils.getPosts(
                category = _selectedCategory.value,
                searchString = _searchText.value,
                sortByPrice = if (_sortBy.value != SortOptions.NEWEST) true else false,
                priceLowToHi = if (_sortBy.value == SortOptions.LOWESTPRICE) true else false,
            ) { posts ->
                _posts.value += posts.map { post -> Post.convertDBEntryToPostDetail(post)}
            }
        } else {
            for (interval in _selectedPriceIntervals.value) {
                val minPrice = SearchFilterUtils.priceFilterOptions.get(interval)?.first
                val maxPrice = SearchFilterUtils.priceFilterOptions.get(interval)?.second
                SearchFilterUtils.getPosts(
                    category = _selectedCategory.value,
                    searchString = _searchText.value,
                    sortByPrice = if (_sortBy.value != SortOptions.NEWEST) true else false,
                    priceLowToHi = if (_sortBy.value == SortOptions.LOWESTPRICE) true else false,
                    minPrice = minPrice,
                    maxPrice = maxPrice
                ) { posts ->
                    _posts.value += posts.map { post -> Post.convertDBEntryToPostDetail(post) }
                }
            }
        }
    }
}

class SearchViewModelFactory(private val searchText: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SearchViewModel(searchText) as T
    }
}