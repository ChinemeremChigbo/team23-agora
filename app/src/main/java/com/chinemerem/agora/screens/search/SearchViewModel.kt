package com.chinemerem.agora.screens.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.chinemerem.agora.model.data.Address
import com.chinemerem.agora.model.data.Category
import com.chinemerem.agora.model.data.Post
import com.chinemerem.agora.model.repository.AddressRepository.Companion.getUserAddress
import com.chinemerem.agora.model.repository.SearchFilterRepository
import com.chinemerem.agora.model.repository.SortOptions
import com.google.firebase.auth.FirebaseAuth
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
                SearchFilterRepository.getPosts(
                    category = _selectedCategory.value,
                    searchString = _searchText.value,
                    sortByPrice = if (_sortBy.value != SortOptions.NEWEST) true else false,
                    priceLowToHi = if (_sortBy.value == SortOptions.LOWESTPRICE) true else false,
                    sortByDistance = if (_sortBy.value == SortOptions.DISTANCE) true else false,
                    selfAddress = selfAddress
                ) { posts ->
                    _posts.value += posts.map { post -> Post.convertDBEntryToPostDetail(post) }
                    continuation.resume(_posts.value)
                }
            } else {
                for (interval in _selectedPriceIntervals.value) {
                    val minPrice = SearchFilterRepository.priceFilterOptions[interval]?.first
                    val maxPrice = SearchFilterRepository.priceFilterOptions[interval]?.second
                    SearchFilterRepository.getPosts(
                        category = _selectedCategory.value,
                        searchString = _searchText.value,
                        sortByPrice = if (_sortBy.value != SortOptions.NEWEST) true else false,
                        priceLowToHi = if (_sortBy.value == SortOptions.LOWESTPRICE) {
                            true
                        } else {
                            false
                        },
                        sortByDistance = if (_sortBy.value == SortOptions.DISTANCE) true else false,
                        selfAddress = selfAddress,
                        minPrice = minPrice,
                        maxPrice = maxPrice
                    ) { posts ->
                        _posts.value += posts.map { post -> Post.convertDBEntryToPostDetail(post) }
                        if (interval == _selectedPriceIntervals.value.last()) {
                            continuation.resume(_posts.value)
                        }
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
