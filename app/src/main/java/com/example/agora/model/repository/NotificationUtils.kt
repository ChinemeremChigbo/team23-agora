package com.example.agora.model.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.example.agora.model.data.Notification

class NotificationUtils {
    companion object {
        fun addNotification(
            notificationId: String,
            userId: String,
            postId: String,
            creatorId: String, // the id of the user who wrote the comment
            commentId: String,
            onSuccess: () -> Unit,
            onFailure: (Exception) -> Unit
        ) {
            val db = FirebaseFirestore.getInstance()
            val notificationRef = db.collection("notifications").document()

            // todo
//            var message = "New comment on post $postName from user $creatorUsername"
            var message = "New comment on post abc from user xyz"

            val notificationData = hashMapOf(
                "notificationId" to notificationId,
                "userId" to userId,
                "postId" to postId,
                "message" to message,
                "commentId" to commentId,
                "eventInfo" to "comment",
                "createdAt" to Timestamp.now()
            )

            notificationRef.set(notificationData)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onFailure(it) }
        }

        fun getUserNotifications(
            userId: String,
            callback: (List<Notification>) -> Unit,
            onFailure: (Exception) -> Unit
        ) {
            val db = FirebaseFirestore.getInstance()

            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    val notificationIds = document.get("notifications") as? List<String> ?: return@addOnSuccessListener callback(emptyList())

                    db.collection("notifications")
                        .whereIn(FieldPath.documentId(), notificationIds)
                        .get()
                        .addOnSuccessListener { snapshot ->
                            val notifications = snapshot.documents.mapNotNull { it.data?.let(Notification::convertDBEntryToNotification) }
                                .sortedByDescending { it.createdAt }
                            callback(notifications)
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