package com.example.agora.screens.explore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ExploreViewModel : ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isExpanded = MutableStateFlow(false)
    val isExpanded = _isExpanded.asStateFlow()

    private val _posts = MutableLiveData<Any>().apply {
        value = listOf("Fridge", "Fridge 2", "Fridge 3")
    }
    val posts: LiveData<Any> = _posts

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    fun onExpandedChange() {
        _isExpanded.value = !_isExpanded.value
        if (!_isExpanded.value) {
            onSearchTextChange("")
        }
    }
}
