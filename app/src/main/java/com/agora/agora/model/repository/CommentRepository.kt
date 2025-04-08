package com.agora.agora.model.repository

import com.agora.agora.model.data.Comment
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class CommentRepository {
    companion object {
        private fun findCommentMentions(text: String): List<String> {
            return "@(\\S+)".toRegex().findAll(text).map { it.groupValues[1] }.toList()
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

            db.collection("users").whereIn("username", mentionUsernames).get()
                .addOnSuccessListener { users ->
                    val userIds = users.documents.map { it.id }
                    onSuccess(userIds)
                }.addOnFailureListener { exception ->
                    onFailure(exception)
                }
        }

        fun createComment(
            postId: String,
            userId: String,
            sellerId: String,
            text: String,
            onSuccess: (String) -> Unit,
            onFailure: (Exception) -> Unit
        ) {
            val db = FirebaseFirestore.getInstance()
            val commentId = db.collection("comments").document().id

            findMentionedUserIds(text, onSuccess = { userIds ->
                val newComment = hashMapOf(
                    "commentId" to commentId,
                    "userId" to userId,
                    "text" to text,
                    "createdAt" to Timestamp.now(),
                    "mentions" to userIds
                )

                db.collection("comments").document(commentId).set(newComment).addOnSuccessListener {
                    val postRef = db.collection("posts").document(postId)

                    postRef.update("comments", FieldValue.arrayUnion(commentId))
                        .addOnSuccessListener {
                            CommentEventManager.notifyCommentAdded(
                                commentId,
                                postId,
                                commenterId = userId,
                                posterId = sellerId,
                                mentionedUserIds = userIds
                            )
                            onSuccess(commentId)
                        }.addOnFailureListener { onFailure(it) }
                }.addOnFailureListener { onFailure(it) }
            }, onFailure = { exception ->
                    println("Error: ${exception.message}")
                })
        }

        fun deleteComment(
            commentId: String,
            postId: String,
            onSuccess: () -> Unit,
            onFailure: (Exception) -> Unit
        ) {
            val db = FirebaseFirestore.getInstance()
            val commentRef = db.collection("comments").document(commentId)
            val postRef = db.collection("posts").document(postId)

            commentRef.delete().addOnSuccessListener {
                postRef.update("comments", FieldValue.arrayRemove(commentId))
                    .addOnSuccessListener { onSuccess() }.addOnFailureListener { onFailure(it) }
            }.addOnFailureListener { onFailure(it) }
        }

        fun getComment(commentId: String, callback: (Comment?) -> Unit) {
            val db = FirebaseFirestore.getInstance()

            db.collection("comments").document(commentId).get().addOnSuccessListener { comment ->
                if (comment.exists()) {
                    callback(comment.data?.let { Comment.convertDBEntryToComment(it) })
                } else {
                    callback(null)
                }
            }
        }

        fun getPostComments(
            postId: String,
            callback: (List<Comment>) -> Unit,
            onFailure: (Exception) -> Unit
        ) {
            val db = FirebaseFirestore.getInstance()

            db.collection("posts").document(postId).get().addOnSuccessListener { document ->
                val commentIds = document.get("comments") as? List<String>
                    ?: return@addOnSuccessListener callback(
                        emptyList()
                    )

                if (commentIds.isEmpty()) {
                    return@addOnSuccessListener
                }

                db.collection("comments")
                    .whereIn(FieldPath.documentId(), commentIds)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        val comments = snapshot.documents.mapNotNull {
                            it.data?.let(
                                Comment::convertDBEntryToComment
                            )
                        }
                        val sortedComments = comments.sortedByDescending { it.createdAt?.seconds }
                        callback(sortedComments)
                    }.addOnFailureListener(onFailure)
            }.addOnFailureListener { onFailure(it) }
        }
    }
}
