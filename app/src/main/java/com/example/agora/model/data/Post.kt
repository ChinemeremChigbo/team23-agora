package com.example.agora.model.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.sql.Timestamp
import java.util.*

enum class PostStatus {
    ACTIVE, RESOLVED, DELETED
}

enum class Category(val value: Int) {
    SELL(0), RIDESHARE(1), SUBLET(2), OTHER(3)
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
    var comments: MutableList<Comment> = mutableListOf(),

    private var db: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {

    // Getters and Setters
    fun getPostId(): String = postId
    fun setPostId(value: String) { postId = value }

    fun getStatus(): PostStatus = status
    fun setStatus(value: PostStatus) { status = value }

    fun getCreatedAt(): Timestamp = createdAt
    fun setCreatedAt(value: Timestamp) { createdAt = value }

    fun filterPosts(
        minPrice: Int = 0,
        maxPrice: Int = Int.MAX_VALUE,
        category: Category? = null,
        priceLowToHi: Boolean = true,
        callback: (List<Map<String, Any>>) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()

        if (category == null) {
            db.collection("posts")
                .whereGreaterThanOrEqualTo("price", minPrice)
                .whereLessThanOrEqualTo("price", maxPrice)
                .orderBy("price",
                    if (priceLowToHi) Query.Direction.ASCENDING else Query.Direction.DESCENDING
                )
                .get()
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
        } else {
            db.collection("posts")
                .whereGreaterThanOrEqualTo("price", minPrice)
                .whereLessThanOrEqualTo("price", maxPrice)
                .whereEqualTo("category", category.value.toString())
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
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