package com.example.agora.model.data

import java.util.*
import java.sql.Timestamp

enum class PostStatus {
    ACTIVE, RESOLVED, DELETED
}

enum class Category {
    SELL, RIDESHARE, SUBLET, OTHER
}

class Post(
    private var postId: UUID = UUID.randomUUID(),
    private var status: PostStatus = PostStatus.ACTIVE,
    private var createdAt: Timestamp = Timestamp(System.currentTimeMillis()),
    var title: String = "",
    var description: String = "",
    var category: Category = Category.OTHER,
    var images: Array<String> = arrayOf()
) {

    // Getters and Setters
    fun getPostId(): UUID = postId
    fun setPostId(value: UUID) { postId = value }

    fun getStatus(): PostStatus = status
    fun setStatus(value: PostStatus) { status = value }

    fun getCreatedAt(): Timestamp = createdAt
    fun setCreatedAt(value: Timestamp) { createdAt = value }

    // Methods
    fun updateInfo(newInfo: Map<String, Any>) {
        // Placeholder implementation
    }

    fun addComment(comment: Comment) {
        // Placeholder implementation
    }

    fun removeComment(comment: Comment) {
        // Placeholder implementation
    }

    fun changeStatus(newStatus: PostStatus) {
        status = newStatus
    }
}