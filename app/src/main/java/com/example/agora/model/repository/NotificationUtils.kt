package com.example.agora.model.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class NotificationUtils {
    companion object {
        fun addNotification(
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
            var message = "New comment on post $postName from user $creatorUsername"

            val notificationData = hashMapOf(
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
            onSuccess: (List<Map<String, Any>>) -> Unit,
            onFailure: (Exception) -> Unit
        ) {
            val db = FirebaseFirestore.getInstance()
            val userRef = db.collection("users").document(userId)

            userRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val notificationIds = document.get("notifications") as? List<String> ?: emptyList()

                        if (notificationIds.isEmpty()) {
                            onSuccess(emptyList())
                            return@addOnSuccessListener
                        }

                        db.collection("notifications")
                            .whereIn(FieldPath.documentId(), notificationIds)
                            .get()
                            .addOnSuccessListener { querySnapshot ->
                                val notifications = querySnapshot.documents
                                    .mapNotNull { it.data }
                                    .sortedByDescending { it["createdAt"] as? Timestamp }
                                onSuccess(notifications)
                            }
                            .addOnFailureListener { onFailure(it) }
                    } else {
                        onFailure(Exception("User not found"))
                    }
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