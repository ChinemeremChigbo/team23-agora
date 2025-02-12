package com.example.agora.screens.explore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PostItemData(val imageUrl: String, val title: String)

class ExploreViewModel : ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isExpanded = MutableStateFlow(false)
    val isExpanded = _isExpanded.asStateFlow()

    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches = _recentSearches.asStateFlow()

    private val titles1 = listOf("Fridge", "Fridge 2", "Fridge 3")
    private val titles2 = listOf("Book", "Book 2", "Book 3")
    private val titles3 = listOf("Plates")

    private val _sections = MutableStateFlow<List<List<String>>>(listOf(titles1, titles2, titles3))
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
    val text: LiveData<String> = _text
    private val _editorsChoiceItems = MutableLiveData<List<PostItemData>>()
    val editorsChoiceItems: LiveData<List<PostItemData>> = _editorsChoiceItems

    init {
        fetchPosts()
    }
    private fun fetchPosts() {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("post")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val items = querySnapshot.documents.mapNotNull { doc ->
                    val title = doc.getString("title")
                    val imageUrl = doc.getString("imageUrl")
                    // Use a placeholder image for now (replace with actual logic if needed)
                    if (title != null && imageUrl != null) {
                        PostItemData(imageUrl, title)
                    } else {
                        null
                    }
                }
                _editorsChoiceItems.value = items
            }
            .addOnFailureListener { exception ->
                // could be useful in future
            }
    }
}
