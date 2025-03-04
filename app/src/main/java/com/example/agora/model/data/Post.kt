package com.example.agora.model.data

import com.example.agora.model.util.DataUtil
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp

enum class PostStatus {
    ACTIVE, RESOLVED, DELETED
}

enum class Category(val value: String) {
    SELL("Marketplace"), RIDESHARE("Rideshare"), SUBLET("Sublet"), OTHER("Other")
}

class Post(
    var postId: String = "",
    var status: PostStatus = PostStatus.ACTIVE,
    var createdAt: Timestamp? = Timestamp.now(),
    var title: String = "",
    var description: String = "",
    var price: Double = 0.0,
    var category: Category = Category.OTHER,
    var images: MutableList<String> = mutableListOf("https://picsum.photos/200"),
    var comments: MutableList<Comment> = mutableListOf(),
    var userId: String = "",
    var address: Address = Address(),
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {
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

    companion object {
        fun convertDBEntryToPostPreview(entry: Map<String, Any>): Post {
            return Post(
                postId = entry["postId"].toString(),
                title = entry["title"].toString(),
                price = entry["price"].toString().toDoubleOrNull() ?: 0.0,
                createdAt = DataUtil.convertStringToTimestamp(entry["createdAt"].toString()),
                images = (entry["images"] as? List<*>)?.map { it.toString() }?.toMutableList()
                    ?: mutableListOf("https://picsum.photos/200"), // Handle empty images
            )
        }

        fun convertDBEntryToPostDetail(entry: Map<String, Any>): Post {
            return Post(
                postId = entry["postId"].toString(),
                title = entry["title"].toString(),
                description = entry["description"].toString(),
                price = entry["price"].toString().toDoubleOrNull() ?: 0.0,
                status = PostStatus.entries.find { it.name == entry["status"] }
                    ?: PostStatus.DELETED,
                category = Category.entries.find { it.name == entry["category"] } ?: Category.OTHER,
                createdAt = DataUtil.convertStringToTimestamp(entry["createdAt"].toString()),
                userId = entry["userId"].toString(),
                address = (entry["address"] as? Map<String, Any>)?.let {
                    Address.convertDBEntryToAddress(it)
                } ?: Address(),
                images = (entry["images"] as? List<*>)?.map { it.toString() }?.toMutableList()
                    ?: mutableListOf("https://picsum.photos/200"), // Handle empty images
            )
        }
    }
}