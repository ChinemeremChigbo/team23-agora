package com.example.agora.screens.wishlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WishlistViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Wishlist"
    }
    val text: LiveData<String> = _text
}
