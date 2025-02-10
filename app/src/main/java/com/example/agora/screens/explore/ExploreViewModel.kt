package com.example.agora.screens.explore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ExploreViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Explore"
    }
    private val _posts = MutableLiveData<Any>().apply {
        value = listOf("Fridge", "Fridge 2", "Fridge 3")
    }
    val text: LiveData<String> = _text
    val posts: LiveData<Any> = _posts
}
