package com.agora.agora.model.repository

import com.agora.agora.model.data.Address
import com.agora.agora.model.data.Category
import com.agora.agora.model.data.Post
import com.agora.agora.model.data.PostStatus
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class PostRepository {
    companion object {
        const val DEFAULT_IMAGE = "https://files.catbox.moe/dtg63k.jpg"

        /** Create a new post in Firestore with User's Address */
        fun createPost(
            title: String,
            description: String,
            price: Double,
            category: Category,
            images: List<String>,
            address: Address,
            userId: String,
            onSuccess: (String) -> Unit,
            onFailure: (Exception) -> Unit
        ) {
            val db = FirebaseFirestore.getInstance()
            val postId = db.collection("posts").document().id
            val validImages = images.ifEmpty { listOf(DEFAULT_IMAGE) }
            val comments = emptyList<String>()

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
                    "country" to address.getCountry(),
                    "lat" to address.getLatLng().latitude,
                    "lng" to address.getLatLng().longitude
                )
            )

            db.collection("posts").document(postId)
                .set(newPost)
                .addOnSuccessListener { onSuccess(postId) }
                .addOnFailureListener { onFailure(it) }
        }

        /** Get a specific post by ID */
        fun getPostById(postId: String, callback: (Post?) -> Unit) {
            val db = FirebaseFirestore.getInstance()

            db.collection("posts").document(postId).get().addOnSuccessListener { post ->
                if (post.exists()) {
                    callback(post.data?.let { Post.convertDBEntryToPostDetail(it) })
                } else {
                    callback(null)
                }
            }
        }

        /** Edit an existing post */
        fun editPost(
            postId: String,
            title: String,
            description: String,
            price: Double,
            category: Category,
            images: List<String>,
            address: Address,
            onSuccess: () -> Unit,
            onFailure: (Exception) -> Unit
        ) {
            val db = FirebaseFirestore.getInstance()
            val updatedPost = hashMapOf(
                "title" to title,
                "description" to description,
                "price" to price,
                "category" to category.name,
                "images" to images.ifEmpty { listOf(DEFAULT_IMAGE) },
                "address" to mapOf(
                    "address" to address.getStreet(),
                    "city" to address.getCity(),
                    "state" to address.getState(),
                    "postalCode" to address.getPostalCode(),
                    "country" to address.getCountry(),
                    "lat" to address.getLatLng().latitude,
                    "lng" to address.getLatLng().longitude
                )
            )

            db.collection("posts").document(postId).update(updatedPost as Map<String, Any>)
                .addOnSuccessListener { onSuccess() }.addOnFailureListener { e ->
                    onFailure(Exception("Failed to edit post: ${e.message}"))
                }
        }

        /** Delete an existing post */
        fun deletePost(postId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
            updatePostStatus(postId, PostStatus.DELETED, onSuccess, onFailure)
        }

        /** Mark a post as resolved */
        fun resolvePost(postId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
            updatePostStatus(postId, PostStatus.RESOLVED, onSuccess, onFailure)
        }

        private fun updatePostStatus(
            postId: String,
            newStatus: PostStatus,
            onSuccess: () -> Unit,
            onFailure: (Exception) -> Unit
        ) {
            val db = FirebaseFirestore.getInstance()
            db.collection("posts").document(postId).update(mapOf("status" to newStatus.name))
                .addOnSuccessListener {
                    onSuccess()
                }.addOnFailureListener { e ->
                    onFailure(e)
                }
        }

        /** Get all posts by a specific user, sorted by "createdAt"*/
        fun getPostsByUser(userId: String, callback: (List<Post>) -> Unit) {
            val db = FirebaseFirestore.getInstance()

            db.collection("posts").whereEqualTo("userId", userId)
                .whereNotIn("status", listOf(PostStatus.DELETED.name))
                .orderBy("createdAt", Query.Direction.DESCENDING).get()
                .addOnSuccessListener { posts ->
                    val postList = posts.mapNotNull {
                        it.data.let { data ->
                            Post.convertDBEntryToPostDetail(data)
                        }
                    }
                    callback(postList)
                }.addOnFailureListener {
                    callback(emptyList())
                }
        }

        fun reportPost(
            reportData: Map<String, Any>,
            onSuccess: () -> Unit,
            onFailure: (String) -> Unit
        ) {
            val db = FirebaseFirestore.getInstance()

            db.collection("reports").add(reportData).addOnSuccessListener {
                println("Report successfully submitted!")
                onSuccess()
            }.addOnFailureListener { e ->
                println("Error submitting report: ${e.message}")
                onFailure(e.localizedMessage ?: "Unknown error")
            }
        }
    }
}
