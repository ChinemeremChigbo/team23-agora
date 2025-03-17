package com.example.agora.model.repository


import com.example.agora.model.data.Address
import com.example.agora.model.data.Category
import com.example.agora.model.data.Post
import com.example.agora.model.data.PostStatus
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class PostUtils {
    companion object {
        private const val DEFAULT_IMAGE = "https://files.catbox.moe/dtg63k.jpg"

        /** Fetch User's Address from Firestore */
        private fun fetchUserAddress(
            userId: String,
            onSuccess: (Address) -> Unit,
            onFailure: (Exception) -> Unit
        ) {
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    val addressMap = document.get("address") as? Map<*, *>
                    if (addressMap != null) {
                        val address = (addressMap  as? Map<String, Any>)?.let {
                            Address.convertDBEntryToAddress(it)
                        }
                        if (address != null) {
                            onSuccess(address)
                        } else {
                            onFailure(Exception("Invalid address format"))
                        }
                    } else {
                        onFailure(Exception("No address found for user"))
                    }
                }
                .addOnFailureListener { onFailure(it) }
        }

        /** Create a new post in Firestore with User's Address */
        fun createPost(
            title: String,
            description: String,
            price: Double,
            category: Category,
            images: List<String>,
            userId: String,
            onSuccess: (String) -> Unit,
            onFailure: (Exception) -> Unit
        ) {
            fetchUserAddress(
                userId = userId,
                onSuccess = { address ->
                    val db = FirebaseFirestore.getInstance()
                    val postId = db.collection("posts").document().id
                    val validImages = images.ifEmpty { listOf(DEFAULT_IMAGE) }
                    val comments = emptyList<String>() // posts should not be made with comments

                    val newPost = hashMapOf(
                        "postId" to postId,
                        "title" to title,
                        "description" to description,
                        "price" to price,
                        "category" to category.name,
                        "images" to validImages,
                        "comments" to comments,
                        "createdAt" to Timestamp.now(),
                        "userId" to userId,
                        "status" to PostStatus.ACTIVE.name,
                        "address" to mapOf(
                            "address" to address.getStreet(),
                            "city" to address.getCity(),
                            "state" to address.getState(),
                            "postalCode" to address.getPostalCode(),
                            "country" to address.getCountry()
                        )
                    )

                    db.collection("posts").document(postId)
                        .set(newPost)
                        .addOnSuccessListener { onSuccess(postId) }
                        .addOnFailureListener { onFailure(it) }
                },
                onFailure = { exception ->
                    onFailure(Exception("Failed to fetch user address: ${exception.message}"))
                }
            )
        }


        /** Get a specific post by ID */
        fun getPostById(postId: String, callback: (Post?) -> Unit) {
            val db = FirebaseFirestore.getInstance()

            db.collection("posts").document(postId)
                .get()
                .addOnSuccessListener { post ->
                    if (post.exists()) {
                        callback(post.data?.let { Post.convertDBEntryToPostDetail(it) })
                    } else {
                        callback(null)
                    }
                }
        }

        /** Get all posts by a specific user, sorted by "createdAt"*/
        fun getPostsByUser (userId: String, callback: (List<Post>) -> Unit) {
            val db = FirebaseFirestore.getInstance()

            db.collection("posts")
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { posts ->
                    val postList = posts.mapNotNull { it.data.let { data -> Post.convertDBEntryToPostDetail(data) } }
                    callback(postList)
                }
                .addOnFailureListener {
                    callback(emptyList())
                }
        }
    }
}
