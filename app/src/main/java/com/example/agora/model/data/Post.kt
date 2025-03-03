package com.example.agora.model.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.Timestamp
import java.util.*
import kotlin.math.min

enum class PostStatus {
    ACTIVE, RESOLVED, DELETED
}

enum class Category() {
    SELL, RIDESHARE, SUBLET, OTHER
}

class Post(
    private var postId: String = "",
    private var userId: String = "123",
    private var status: PostStatus = PostStatus.ACTIVE,
    private var createdAt: Timestamp = Timestamp.now(),
    var title: String = "",
    var description: String = "",
    var price: Double = 0.0,
    var category: Category = Category.OTHER,
    var images: Array<String> = arrayOf("https://picsum.photos/200"),
    var comments: MutableList<Comment> = mutableListOf(),

    private var db: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {

    // Getters and Setters
    fun getPostId(): String = postId
    fun setPostId(value: String) { postId = value }

    fun getUserId(): String = userId
    fun setUserId(value: String) { userId = value }

    fun getStatus(): PostStatus = status
    fun setStatus(value: PostStatus) { status = value }

    fun getCreatedAt(): Timestamp = createdAt
    fun setCreatedAt(value: Timestamp) { createdAt = value }

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