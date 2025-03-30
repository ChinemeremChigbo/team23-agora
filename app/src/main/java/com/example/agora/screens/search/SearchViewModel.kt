package com.example.agora.screens.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.agora.model.data.Address
import com.example.agora.model.data.Category
import com.example.agora.model.data.Post
import com.example.agora.model.repository.AddressUtils.Companion.getUserAddress
import com.example.agora.model.repository.SearchFilterUtils
import com.example.agora.model.repository.SortOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SearchViewModel(initialSearchText: String = "") : ViewModel() {
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts = _posts.asStateFlow()

    private val _searchText = MutableStateFlow<String>(initialSearchText)
    val searchText = _searchText.asStateFlow()

    private val _isExpanded = MutableStateFlow(false)
    val isExpanded = _isExpanded.asStateFlow()

    private val _isLoading = MutableLiveData<Boolean>(true)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _sortBy = MutableStateFlow<SortOptions>(SortOptions.NEWEST)
    val sortBy = _sortBy.asStateFlow()

    private val _selectedPriceIntervals = MutableStateFlow<List<String>>(emptyList())
    val selectedPriceIntervals = _selectedPriceIntervals.asStateFlow()

    private var selfAddress: Address? = null

    fun changeCategory(category: Category?) {
        _selectedCategory.value = category
        getSuspendedResults()
    }

    fun changeSort(sortBy: SortOptions) {
        _sortBy.value = sortBy
        getSuspendedResults()
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
        _searchText.value = query
        _isExpanded.value = false
        getSuspendedResults()
    }

    init {
        viewModelScope.launch {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                selfAddress = getUserAddress(userId)
            }
            getSuspendedResults()
        }
    }

    fun getSuspendedResults() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                fetchResults()
            } catch (e: Exception) {
                // Handle any exceptions that occur
                // TODO: add error screen component and display the component "oops something went wrong"
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun fetchResults(): List<Post> {
        _posts.value = emptyList()
        return suspendCoroutine { continuation ->
            if (_selectedPriceIntervals.value.isEmpty()) {
                SearchFilterUtils.getPosts(
                    category = _selectedCategory.value,
                    searchString = _searchText.value,
                    sortByPrice = if (_sortBy.value != SortOptions.NEWEST) true else false,
                    priceLowToHi = if (_sortBy.value == SortOptions.LOWESTPRICE) true else false,
                    sortByDistance = if (_sortBy.value == SortOptions.DISTANCE) true else false,
                    selfAddress = selfAddress,
                ) { posts ->
                    _posts.value += posts.map { post -> Post.convertDBEntryToPostDetail(post) }
                    continuation.resume(_posts.value)
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
                        sortByDistance = if (_sortBy.value == SortOptions.DISTANCE) true else false,
                        selfAddress = selfAddress,
                        minPrice = minPrice,
                        maxPrice = maxPrice
                    ) { posts ->
                        _posts.value += posts.map { post -> Post.convertDBEntryToPostDetail(post) }
                       continuation.resume(_posts.value)
                    }
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