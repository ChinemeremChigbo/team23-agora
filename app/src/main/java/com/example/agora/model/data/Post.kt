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
    private var postId: String = "",
    private var status: PostStatus = PostStatus.ACTIVE,
    private var createdAt: Timestamp = Timestamp(System.currentTimeMillis()),
    var title: String = "",
    var description: String = "",
    var price: Double = 0.0,
    var category: Category = Category.OTHER,
    var images: Array<String> = arrayOf("https://picsum.photos/200"),
    var comments: MutableList<Comment> = mutableListOf()
) {

    // Getters and Setters
    fun getPostId(): String = postId
    fun setPostId(value: String) { postId = value }

    fun getStatus(): PostStatus = status
    fun setStatus(value: PostStatus) { status = value }

    fun getCreatedAt(): Timestamp = createdAt
    fun setCreatedAt(value: Timestamp) { createdAt = value }

    // Methods
    fun updateInfo(newInfo: Map<String, Any>) {
        // TODO
    }

    fun addComment(text: String) {
        val comment: Comment = Comment(text=text, creatorId="")
        val mentions: List<String> = comment.findMentions()
        for (mention in mentions) {
            // TODO: create a notification
        }
        comments.add(comment)
    }

    fun removeComment(comment: Comment) {
        // TODO
    }

    fun changeStatus(newStatus: PostStatus) {
        status = newStatus
    }
}