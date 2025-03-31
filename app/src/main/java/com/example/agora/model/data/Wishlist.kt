package com.example.agora.model.data

import com.google.firebase.Timestamp

class WishList(private var userId: String = "") {

    private val posts: MutableMap<String, Timestamp> = mutableMapOf()

    fun getUserId(): String {
        return userId
    }

    fun getWishlistPosts(): Map<String, Timestamp> {
        return posts.toMap()
    }

    fun addPost(postId: String, timestamp: Timestamp) {
        posts[postId] = timestamp
    }

    fun removePost(postId: String) {
        posts.remove(postId)
    }
}
