package com.example.agora.model.repository

import com.google.firebase.firestore.FirebaseFirestore

class WishlistUtils {
    fun addToWishList(userId: String, postId: String, callback: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val wishlistsRef = db.collection("wishlists")

        wishlistsRef.whereEqualTo("userId", userId)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val documentSnapshot = documents.documents[0]
                    val wishlistId = documentSnapshot.id

                    val wishlistData = documentSnapshot.data
                    val posts = (wishlistData?.get("posts") as? List<Map<String, Any>>)?.toMutableList() ?: mutableListOf()

                    if (posts.none { it["postId"] == postId }) {
                        val newPost = mapOf("postId" to postId, "timestamp" to System.currentTimeMillis())
                        posts.add(newPost)

                        wishlistsRef.document(wishlistId)
                            .update("posts", posts)
                            .addOnSuccessListener { callback(true) }
                    } else {
                        callback(false) // do nothing if post present
                    }
                } else {
                    // create new wishlist
                    val newWishlist = mapOf(
                        "userId" to userId,
                        "posts" to listOf(mapOf("postId" to postId, "timestamp" to System.currentTimeMillis()))
                    )

                    wishlistsRef.add(newWishlist).addOnSuccessListener {
                        callback(true)
                    }
                }
            }
    }

    fun removeFromWishList(userId: String, postId: String, callback: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val wishlistsRef = db.collection("wishlists")

        wishlistsRef.whereEqualTo("userId", userId)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val documentSnapshot = documents.documents[0]
                    val wishlistId = documentSnapshot.id

                    val wishlistData = documentSnapshot.data
                    val posts = (wishlistData?.get("posts") as? List<Map<String, Any>>)?.toMutableList() ?: mutableListOf()

                    val updatedPosts = posts.filter { it["postId"] != postId } // remove

                    if (updatedPosts.size != posts.size) {
                        wishlistsRef.document(wishlistId)
                            .update("posts", updatedPosts)
                            .addOnSuccessListener { callback(true) }
                    } else {
                        callback(false)
                    }
                } else {
                    callback(false)
                }
            }
    }


    fun getWishList(userId: String, callback: (List<Map<String, Any>>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val wishlistsRef = db.collection("wishlists")

        wishlistsRef.whereEqualTo("userId", userId)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.documents[0]
                    val posts = document.data?.get("posts") as? List<Map<String, Any>> ?: emptyList()
                    callback(posts)
                } else {
                    callback(emptyList())
                }
            }
    }
}