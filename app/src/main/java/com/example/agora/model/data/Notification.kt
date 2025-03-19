package com.example.agora.model.data

import com.example.agora.model.repository.PostUtils
import com.example.agora.model.util.DataUtil
import java.util.*
import com.google.firebase.Timestamp


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
        fun convertDBEntryToNotification(entry: Map<String, Any>, callback: (Notification) -> Unit) {
            val postId = entry["postId"]?.toString() ?: ""
            var preview = ""
            PostUtils.getPostById(postId) { post ->
                if (post != null) {
                    preview = post.images[0]
                }

                val notification = Notification(
                    notificationId = entry["notificationId"]?.toString() ?: "",
                    postId = postId,
                    userId = entry["userId"]?.toString() ?: "",
                    message = entry["message"]?.toString() ?: "",
                    commentId = entry["commentId"]?.toString() ?: "",
                    eventInfo = entry["eventInfo"]?.toString() ?: "",
                    previewImg = preview,
                    createdAt = DataUtil.convertStringToTimestamp(entry["createdAt"]?.toString() ?: "")
                )

                callback(notification)
            }
        }
    }
}
