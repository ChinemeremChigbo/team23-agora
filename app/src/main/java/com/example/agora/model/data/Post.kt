package com.example.agora.model.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.sql.Timestamp
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
    private var createdAt: Timestamp = Timestamp(System.currentTimeMillis()),
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

    fun filterPosts(
        minPrice: Int? = null,
        maxPrice: Int? = null,
        category: Category? = null,
        sortByPrice: Boolean = false,
        priceLowToHi: Boolean = true,
        callback: (List<Map<String, Any>>) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()

        var query: Query = db.collection("posts")

        minPrice?.let {
            query = query.whereGreaterThanOrEqualTo("price", minPrice)
        }

        maxPrice?.let {
            query = query.whereLessThanOrEqualTo("price", maxPrice)
        }

        category?.let {
            query = query.whereEqualTo("category", category.name)
        }

        query = if (sortByPrice) {
            query.orderBy(
                "price", if (priceLowToHi) Query.Direction.ASCENDING else Query.Direction.DESCENDING
            )
        } else {
            query.orderBy(
                "createdAt", Query.Direction.DESCENDING
            )
        }

        query.get()
            .addOnSuccessListener { documents ->
                val resultList = mutableListOf<Map<String, Any>>()
                for (document in documents) {
                    resultList.add(document.data)
                }
                callback(resultList)
            }
            .addOnFailureListener { exception ->
                println("Error getting posts: $exception")
                callback(emptyList())
            }
    }

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