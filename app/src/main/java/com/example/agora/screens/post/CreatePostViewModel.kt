package com.example.agora.screens.post

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.agora.model.data.Category
import com.example.agora.model.repository.PostUtils
import com.example.agora.model.util.UserManager
import com.example.agora.util.uploadImageToS3
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow

class CreatePostViewModel(
    application: Application,
    postId: String
): AndroidViewModel(application) {
    private val context = application.applicationContext
//    val userId = UserManager.currentUser!!.userId
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser!!.uid // todo: get current user with UserManager
    val uploadedUrls = mutableListOf<String>()

    var images = MutableStateFlow<List<Uri>>(emptyList())
    var title = MutableStateFlow("")
    var price = MutableStateFlow("")
    var category = MutableStateFlow("")
    var description = MutableStateFlow("")

    // Prepopulate if editing
    val editing = postId.isNotEmpty()
    init {
        if (editing) fetchPostDetails(postId)
    }

    fun updateImages(newImages: List<Uri>) {
        images.value = newImages
    }

    fun updateTitle(newTitle: String) {
        title.value = newTitle
    }

    fun updatePrice(newPrice: String) {
        price.value = newPrice
    }

    fun updateCategory(newCategory: String) {
        category.value = newCategory
    }

    fun updateDescription(newDescription: String) {
        description.value = newDescription
    }

    private fun fetchPostDetails(postId: String) {
        PostUtils.getPostById(postId, { post ->
            if (post != null) {
//            updateImages(post.images)   // todo: huh
                updateTitle(post.title)
                updatePrice(post.price.toString())
                updateCategory(post.category.value)
                updateDescription(post.description)
            }
        })
    }

    fun createEditPost(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        // Check fields are non empty
        if (!checkRequiredFields{ errorMessage -> onError(errorMessage) }) return

        // Validate price, category
        val priceDouble = try {
            price.value.toDouble()
        } catch (e: NumberFormatException) {
            onError("Price is poorly formatted"); return
        }

        val categoryEnum = try {
            Category.entries.first { it.value == category.value }
        } catch (e: NoSuchElementException) {
            onError("No such category found"); return
        }

        // Update images
        if (!uploadImages{ errorMessage -> onError(errorMessage) }) return

        try {
            if (editing) {
                // Edit post
                // todo: add backend request
            } else {
                // Create post
                PostUtils.createPost(
                    title = title.value,
                    description = description.value,
                    price = priceDouble,
                    category = categoryEnum,
                    images = uploadedUrls,
                    userId = userId,
                    onSuccess = { onSuccess() },
                    onFailure = { e -> onError(e.localizedMessage ?: "Post failed") }
                )
            }
        } catch (e: Exception) {
            onError(e.localizedMessage ?: "Post failed")
        }
    }

    private fun checkRequiredFields(onError: (String) -> Unit): Boolean {
        val fields = mapOf(
            "Title" to title.value,
            "Price" to price.value,
            "Category" to category.value,
            "Description" to description.value,
        )

        for ((key, value) in fields) {
            if (value.isEmpty()) {
                onError("$key field cannot be empty")
                return false
            }
        }
        return true
    }

    private fun uploadImages(onError: (String) -> Unit): Boolean {
        var uploadSuccessful = true
        images.value.forEach { uri ->
            uploadImageToS3(
                context,
                uri,
                onSuccess = { uploadedImageUrl ->
                    uploadedUrls.add(uploadedImageUrl)
                },
                onFailure = { errorMessage ->
                    onError("Image upload failed: $errorMessage")
                    uploadSuccessful = false
                }
            )
        }
        return uploadSuccessful
    }
}

class CreatePostViewModelFactory(
    private val application: Application,
    private val postId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CreatePostViewModel(application, postId) as T
    }
}
