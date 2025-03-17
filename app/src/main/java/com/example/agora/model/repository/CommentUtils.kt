package com.example.agora.model.repository

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class CommentUtils {
    companion object {
        private fun findCommentMentions(text: String): List<String> {
            return "@(\\S+)".toRegex().findAll(text)
                .map { it.groupValues[1] }
                .toList()
        }

        private fun findMentionedUserIds(
            text: String,
            onSuccess: (List<String>) -> Unit,
            onFailure: (Exception) -> Unit
        ) {
            val db = FirebaseFirestore.getInstance()
            val mentionUsernames: List<String> = findCommentMentions(text)

            if (mentionUsernames.isEmpty()) {
                onSuccess(emptyList())
                return
            }

            val userIds = mutableSetOf<String>()
            val tasks = mutableListOf<Task<QuerySnapshot>>()

            for (username in mentionUsernames) {
                val query = db.collection("users")
                    .whereEqualTo("username", username)
                    .get()

                tasks.add(query)
            }

            Tasks.whenAllSuccess<QuerySnapshot>(tasks)
                .addOnSuccessListener { results ->
                    for (snapshot in results) {
                        for (document in snapshot.documents) {
                            val userId = document.getString("userId")
                            if (userId != null) {
                                userIds.add(userId)
                            }
                        }
                    }
                    onSuccess(userIds.toList())
                }
                .addOnFailureListener { onFailure(it) }
        }


        /** Create a new comment in Firestore for a given post */
        fun createComment(
            postId: String,
            creatorId: String,
            text: String,
            parentCommentID: String = "",
            onSuccess: (String) -> Unit,
            onFailure: (Exception) -> Unit
        ) {
            val db = FirebaseFirestore.getInstance()
            val commentId = db.collection("comments").document().id

            var mentionIds: List<String> = emptyList()

            findMentionedUserIds(text,
                onSuccess = { userIds ->
                    mentionIds = userIds
                },
                onFailure = { exception ->
                    println("Error: ${exception.message}")
                }
            )

            val newComment = hashMapOf(
                "commentId" to commentId,
                "creatorId" to creatorId,
                "text" to text,
                "createdAt" to Timestamp.now(),
                "mentions" to mentionIds,
                "parentCommentID" to parentCommentID
            )

            db.collection("comments").document(commentId)
                .set(newComment)
                .addOnSuccessListener {
                    val postRef = db.collection("posts").document(postId)

                    postRef.update("comments", FieldValue.arrayUnion(commentId))
                        .addOnSuccessListener {
                            onSuccess(commentId)
                        }
                        .addOnFailureListener { onFailure(it) }
                }
                .addOnFailureListener { onFailure(it) }
        }

        fun deleteComment(
            commentId: String,
            onSuccess: () -> Unit,
            onFailure: (Exception) -> Unit
        ) {
            val db = FirebaseFirestore.getInstance()

            db.collection("comments").document(commentId)
                .delete()
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { exception ->
                    onFailure(Exception("Failed to delete comment: ${exception.message}"))
                }
        }
    }
}