package com.example.agora.screens.comments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CommentsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Comments"
    }
    val text: LiveData<String> = _text
}
