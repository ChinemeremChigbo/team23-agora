package com.example.agora.model.data

import java.util.*
import com.google.firebase.Timestamp


class Notification(
    var postId: String = "",
    var userId: String = "",
    var message: String = "",
    var commentId: String = "",
    var eventInfo: String = "",
    var createdAt: Timestamp?
) {
    companion object {
        fun convertDBEntryToNotification() {
            // todo
        }
    }
}
