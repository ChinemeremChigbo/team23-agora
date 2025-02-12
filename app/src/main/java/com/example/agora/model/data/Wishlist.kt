package com.example.agora.model.data

import java.util.*
import java.sql.Timestamp

class WishList {
    private val savedPosts: MutableMap<UUID, Timestamp> = mutableMapOf()

    // Methods
    fun addToWishList(postId: UUID): Boolean {
        return if (!savedPosts.containsKey(postId)) {
            savedPosts[postId] = Timestamp(System.currentTimeMillis())
            true
        } else {
            false
        }
    }

    fun removeFromWishList(postId: UUID): Boolean {
        return if (savedPosts.containsKey(postId)) {
            savedPosts.remove(postId)
            true
        } else {
            false
        }
    }
}
