package com.example.agora.model.repository

import com.example.agora.model.data.PostStatus
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp

class WishlistUtils {
    companion object {
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
                        val posts =
                            (wishlistData?.get("posts") as? List<Map<String, Any>>)?.toMutableList()
                                ?: mutableListOf()

                        if (posts.none { it["postId"] == postId }) {
                            val newPost =
                                mapOf("postId" to postId, "timestamp" to Timestamp.now())
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
                            "posts" to listOf(
                                mapOf(
                                    "postId" to postId,
                                    "timestamp" to Timestamp.now()
                                )
                            )
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
                        val posts =
                            (wishlistData?.get("posts") as? List<Map<String, Any>>)?.toMutableList()
                                ?: mutableListOf()

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
            fetchWishListFromDB(userId) { posts ->
                if (posts.isEmpty()) {
                    callback(emptyList())
                    return@fetchWishListFromDB
                }

                val sortedPosts = posts.sortedByDescending {
                    (it["timestamp"] as? Timestamp)?.seconds ?: 0L
                }

                val db = FirebaseFirestore.getInstance()
                val resultList = MutableList<Map<String, Any>?>(sortedPosts.size) { null }
                var remaining = sortedPosts.size

                sortedPosts.forEachIndexed { index, post ->
                    val postId = post["postId"] as? String
                    if (postId == null) {
                        resultList[index] = emptyMap()
                        remaining--
                        if (remaining == 0) {
                            callback(resultList.filterNotNull())
                        }
                    } else {
                        db.collection("posts").document(postId)
                            .get()
                            .addOnSuccessListener { document ->
                                resultList[index] = if (document != null && document.exists()) {
                                    val data = document.data
                                    // TODO: identify resolved post separately and mark as grey
                                    if (data?.get("status").toString() != PostStatus.ACTIVE.name) {
                                        null // Exclude this post as it's deleted or resolved
                                    } else {
                                        data
                                    }
                                } else {
                                    null
                                }
                                remaining--
                                if (remaining == 0) {
                                    callback(resultList.filterNotNull())
                                }
                            }
                            .addOnFailureListener { exception ->
                                resultList[index] = emptyMap()
                                remaining--
                                if (remaining == 0) {
                                    callback(resultList.filterNotNull())
                                }
                            }
                    }
                }
            }
        }


        private fun fetchWishListFromDB(userId: String, callback: (List<Map<String, Any>>) -> Unit) {
            val db = FirebaseFirestore.getInstance()
            val wishlistsRef = db.collection("wishlists")

            wishlistsRef.whereEqualTo("userId", userId)
                .limit(1)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val document = documents.documents[0]
                        val posts =
                            document.data?.get("posts") as? List<Map<String, Any>> ?: emptyList()
                        callback(posts)
                    } else {
                        callback(emptyList())
                    }
                }
        }

        fun isPostInWishlist(userId: String, postId: String, callback: (Boolean) -> Unit) {
            getWishList(userId) { posts ->
                callback(posts.any { it["postId"] == postId })
            }
        }

    }
}