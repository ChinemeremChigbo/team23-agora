package com.example.agora.model.data

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
    var createdAt: Timestamp?
) {
    companion object {
        fun convertDBEntryToNotification(entry: Map<String, Any>): Notification {
            return Notification(
                notificationId = entry["notificationId"]?.toString() ?: "",
                postId = entry["postId"]?.toString() ?: "",
                userId = entry["userId"]?.toString() ?: "",
                message = entry["message"]?.toString() ?: "",
                commentId = entry["commentId"]?.toString() ?: "",
                eventInfo = entry["eventInfo"]?.toString() ?: "",
                createdAt = DataUtil.convertStringToTimestamp(entry["createdAt"]?.toString() ?: "")
            )
        }
    }
}
