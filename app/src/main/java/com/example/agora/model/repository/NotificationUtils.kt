package com.example.agora.model.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.example.agora.model.data.Notification

enum class NotificationType {
    POSTER, MENTION
}

class NotificationUtils {
    companion object {
        fun addNotification(
            userId: String,
            postId: String,
            commentId: String,
            commenterId: String,
            type: NotificationType,
            onSuccess: () -> Unit,
            onFailure: (Exception) -> Unit
        ) {
            val db = FirebaseFirestore.getInstance()
            val notificationRef = db.collection("notifications").document()

            db.collection("users").document(commenterId).get()
                .addOnSuccessListener { userDocument ->

                    val name = userDocument.getString("username") ?: ""
                    val message = when (type) {
                        NotificationType.POSTER -> "$name commented on your post!"
                        NotificationType.MENTION -> "You were mentioned in a comment by $name"
                    }
                    val notificationData = hashMapOf(
                        "notificationId" to notificationRef,
                        "userId" to userId,
                        "postId" to postId,
                        "message" to message,
                        "commentId" to commentId,
                        "eventInfo" to "comment",
                        "createdAt" to Timestamp.now()
                    )
                    notificationRef.set(notificationData)
                        .addOnSuccessListener {
                            db.collection("users").document(userId)
                                .update("notifications", FieldValue.arrayUnion(notificationRef.id))
                                .addOnSuccessListener {
                                    onSuccess()
                                }
                                .addOnFailureListener {
                                    onFailure(it)
                                }
                        }
                        .addOnFailureListener {
                            onFailure(it)
                        }
                    notificationRef.set(notificationData)
                        .addOnSuccessListener {
                            onSuccess()
                        }
                        .addOnFailureListener {
                            onFailure(it)
                        }
                }
                .addOnFailureListener {
                    onFailure(it)
                }
        }

        fun getUserNotifications(
            userId: String,
            callback: (List<Notification>) -> Unit,
            onFailure: (Exception) -> Unit
        ) {
            val db = FirebaseFirestore.getInstance()

            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    val notificationIds =
                        (document.get("notifications") as? List<String>) ?: emptyList()
                    if (notificationIds.isEmpty()) {
                        return@addOnSuccessListener callback(emptyList())
                    }
                    db.collection("notifications")
                        .whereIn(FieldPath.documentId(), notificationIds)
                        .get()
                        .addOnSuccessListener { snapshot ->
                            val notifications = mutableListOf<Notification>()
                            snapshot.documents.forEach { document ->
                                val data = document.data
                                if (data != null) {
                                    Notification.convertDBEntryToNotification(data) { notification ->
                                        notifications.add(notification)

                                        if (notifications.size == snapshot.documents.size) {
                                            notifications.sortByDescending { it.createdAt }
                                            callback(notifications)
                                        }
                                    }
                                }
                            }
                        }
                        .addOnFailureListener(onFailure)
                }
                .addOnFailureListener { onFailure(it) }
        }

        fun removeNotification(
            userId: String,
            notificationId: String,
            onSuccess: () -> Unit,
            onFailure: (Exception) -> Unit
        ) {
            val db = FirebaseFirestore.getInstance()
            val userRef = db.collection("users").document(userId)
            val notificationRef = db.collection("notifications").document(notificationId)

            notificationRef.delete()
                .addOnSuccessListener {
                    userRef.update("notifications", FieldValue.arrayRemove(notificationId))
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { onFailure(it) }
                }
                .addOnFailureListener { onFailure(it) }
        }
    }
}