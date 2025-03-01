package com.example.agora.model.data

import java.util.*
import java.sql.Timestamp

class WishList {
    private val savedPosts: MutableMap<String, Timestamp> = mutableMapOf()

    // Methods
    fun addToWishList(postId: String): Boolean {
        return if (!savedPosts.containsKey(postId)) {
            savedPosts[postId] = Timestamp(System.currentTimeMillis())
            true
        } else {
            false
        }
    }

    fun removeFromWishList(postId: String): Boolean {
        return if (savedPosts.containsKey(postId)) {
            savedPosts.remove(postId)
            true
        } else {
            false
        }
    }
}
