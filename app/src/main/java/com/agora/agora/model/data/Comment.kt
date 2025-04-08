package com.agora.agora.model.data

import com.google.firebase.Timestamp

class Comment(
    val commentId: String = "",
    val userId: String = "",
    val postId: String = "",
    var text: String = "",
    var createdAt: Timestamp? = Timestamp.now(),
    var mentions: List<String> = listOf()
) {
    companion object {
        fun convertDBEntryToComment(entry: Map<String, Any>): Comment {
            return Comment(
                commentId = entry["commentId"] as? String ?: "",
                userId = entry["userId"] as? String ?: "",
                postId = entry["postId"] as? String ?: "",
                text = entry["text"] as? String ?: "",
                createdAt = entry["createdAt"] as? Timestamp,
                mentions = entry["mentions"] as? List<String> ?: listOf()
            )
        }
    }
}
