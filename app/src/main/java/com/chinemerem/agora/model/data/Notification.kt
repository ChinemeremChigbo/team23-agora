package com.chinemerem.agora.model.data

import com.chinemerem.agora.model.repository.PostRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference

class Notification(
    var notificationId: String = "",
    var postId: String = "",
    var userId: String = "",
    var message: String = "",
    var commentId: String = "",
    var eventInfo: String = "",
    var previewImg: String = "",
    var createdAt: Timestamp?
) {
    companion object {
        fun convertDBEntryToNotification(
            entry: Map<String, Any>,
            callback: (Notification) -> Unit
        ) {
            val postId = entry["postId"]?.toString() ?: ""
            var preview = ""
            PostRepository.getPostById(postId) { post ->
                if (post != null) {
                    preview = post.images[0]
                }

                val notification = Notification(
                    notificationId = if (entry["notificationId"] is DocumentReference) {
                        (entry["notificationId"] as DocumentReference).id
                    } else {
                        entry["notificationId"]?.toString() ?: ""
                    },
                    postId = postId,
                    userId = entry["userId"]?.toString() ?: "",
                    message = entry["message"]?.toString() ?: "",
                    commentId = entry["commentId"]?.toString() ?: "",
                    eventInfo = entry["eventInfo"]?.toString() ?: "",
                    previewImg = preview,
                    createdAt = entry["createdAt"] as? Timestamp
                )

                callback(notification)
            }
        }
    }
}
