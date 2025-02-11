package com.example.agora.screens.explore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore


data class PostItemData(val imageUrl: String, val title: String)

class ExploreViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Explore"
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
