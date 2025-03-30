package com.example.agora.screens.postEdit

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.agora.model.data.Address
import com.example.agora.model.data.Address.Companion.convertDBEntryToAddress
import com.example.agora.model.data.Category
import com.example.agora.model.repository.AddressUtils.Companion.getLatLongForPostalCode
import com.example.agora.model.repository.AddressUtils.Companion.getUserAddress
import com.example.agora.model.repository.PostUtils
import com.example.agora.util.uploadImageToS3
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.*

class PostEditViewModel(
    application: Application,
    private val postId: String
) : AndroidViewModel(application) {
    private val context get() = getApplication<Application>().applicationContext
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid

    private var initialImageUris: List<Uri> = emptyList()
    var images = MutableStateFlow<List<Uri>>(emptyList())
    var title = MutableStateFlow("")
    var price = MutableStateFlow("")
    var category = MutableStateFlow("")
    var description = MutableStateFlow("")
    var streetAddress = MutableStateFlow("")
    var city = MutableStateFlow("")
    var state = MutableStateFlow("")
    var postalCode = MutableStateFlow("")
    var country = MutableStateFlow("")

    // Prepopulate if editing
    val editing = postId.isNotEmpty()

    init {
        if (editing) {
            fetchPostDetails(postId)
        } else {
            preloadUserAddress()
        }
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

    fun updateStreetAddress(newStreet: String) {
        streetAddress.value = newStreet
    }

    fun updateCity(newCity: String) {
        city.value = newCity
    }

    fun updateState(newState: String) {
        state.value = newState
    }

    fun updatePostalCode(newPostalCode: String) {
        postalCode.value = newPostalCode
    }

    fun updateCountry(newCountry: String) {
        country.value = newCountry
    }

    private fun preloadUserAddress() {
        if (userId == null) return

        viewModelScope.launch {
            val address = getUserAddress(userId)
            address?.let {
                updateStreetAddress(it.getStreet())
                updateCity(it.getCity())
                updateState(it.getState())
                updatePostalCode(it.getPostalCode())
                updateCountry(it.getCountry())
            }
        }
    }

    private fun fetchPostDetails(postId: String) {
        PostUtils.getPostById(postId, { post ->
            if (post != null) {
                val uris = post.images.map { Uri.parse(it) }
                initialImageUris = uris
                updateImages(uris)
                updateTitle(post.title)
                updatePrice(post.price.toString())
                updateCategory(post.category.value)
                updateDescription(post.description)
                val address = post.address
                address.let {
                    updateStreetAddress(it.getStreet())
                    updateCity(it.getCity())
                    updateState(it.getState())
                    updatePostalCode(it.getPostalCode())
                    updateCountry(it.getCountry())
                }
            }
        })
    }

    fun upsertPost(
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            // Check fields are non empty
            if (!checkRequiredFields { errorMessage -> onError(errorMessage) }) return@launch

            // Validate price, category
            val priceDouble = try {
                price.value.toDouble()
            } catch (e: NumberFormatException) {
                onError("Price is poorly formatted"); return@launch
            }

            val categoryEnum = try {
                Category.entries.first { it.value == category.value }
            } catch (e: NoSuchElementException) {
                onError("No such category found"); return@launch
            }


            val latLng = try {
                getLatLongForPostalCode(country.value, postalCode.value)
                    ?.let { LatLng(it.first, it.second) }
                    ?: LatLng(-1.0, -1.0) // fallback
            } catch (e: Exception) {
                Log.e("PostEditViewModel", "Geocoding failed: ${e.message}")
                LatLng(-1.0, -1.0) // fallback on error
            }

            val newAddress = Address.create(
                country = country.value,
                city = city.value,
                state = state.value,
                street = streetAddress.value,
                postalCode = postalCode.value,
                latLng = latLng
            )

            try {
                // Upload images and wait for completion
                val uploadedUrls = uploadImages()

                if (editing) {  // Edit post
                    PostUtils.editPost(
                        postId = postId,
                        title = title.value,
                        description = description.value,
                        price = priceDouble,
                        category = categoryEnum,
                        images = uploadedUrls,
                        address = newAddress!!,
                        onSuccess = {
                            onSuccess("Post edited successfully!")
                        },
                        onFailure = { e -> onError(e.localizedMessage ?: "Edit Post failed") }
                    )
                } else {        // Create post
                    PostUtils.createPost(
                        title = title.value,
                        description = description.value,
                        price = priceDouble,
                        category = categoryEnum,
                        images = uploadedUrls,
                        userId = userId!!,
                        address = newAddress!!,
                        onSuccess = { onSuccess("Post created successfully!") },
                        onFailure = { e -> onError(e.localizedMessage ?: "Create Post failed") }
                    )
                }
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Post failed")
            }
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

    private suspend fun uploadImages(): List<String> = withContext(Dispatchers.IO) {
        // compare current images with originally loaded images
        if (editing && images.value == initialImageUris) {
            Log.d("PostEditViewModel", "No new images detected, skipping upload")
            return@withContext initialImageUris.map { it.toString() }
        }
        val uploadJobs = images.value.map { uri ->
            async {
                val deferred = CompletableDeferred<String>()
                uploadImageToS3(
                    context,
                    uri,
                    onSuccess = { uploadedUrl -> deferred.complete(uploadedUrl) },
                    onFailure = { errorMessage -> deferred.completeExceptionally(Exception("Image upload failed: $errorMessage")) }
                )
                deferred.await()
            }
        }
        uploadJobs.awaitAll() // wait for all uploads to complete
    }
}

class PostEditViewModelFactory(
    private val application: Application,
    private val postId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PostEditViewModel(application, postId) as T
    }
}
