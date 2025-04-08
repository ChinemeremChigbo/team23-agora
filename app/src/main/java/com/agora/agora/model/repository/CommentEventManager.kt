package com.agora.agora.model.repository

object CommentEventManager {
    private val observers = mutableListOf<CommentObserver>()
    init {
        // default observers for post owner and mentions
        observers += PostAuthorNotifier()
        observers += MentionNotifier()
    }
    fun addObserver(observer: CommentObserver) = observers.add(observer)
    fun removeObserver(observer: CommentObserver) = observers.remove(observer)
    fun notifyCommentAdded(
        commentId: String,
        postId: String,
        commenterId: String,
        posterId: String,
        mentionedUserIds: List<String>
    ) {
        // notify all subscribed observers about a new comment event
        for (observer in observers) {
            observer.onCommentAdded(commentId, postId, commenterId, posterId, mentionedUserIds)
        }
    }
}

interface CommentObserver {
    fun onCommentAdded(
        commentId: String,
        postId: String,
        commenterId: String,
        posterId: String,
        mentionedUserIds: List<String>
    )
}

// concrete observer 1: notifies the post owner of a new comment
class PostAuthorNotifier : CommentObserver {
    override fun onCommentAdded(
        commentId: String,
        postId: String,
        commenterId: String,
        posterId: String,
        mentionedUserIds: List<String>
    ) {
        if (commenterId != posterId) {
            NotificationRepository.addNotification(
                userId = posterId,
                postId = postId,
                commentId = commentId,
                commenterId = commenterId,
                type = NotificationType.POSTER,
                onSuccess = {},
                onFailure = {}
            )
        }
    }
}

// concrete observer 2: notifies all mentioned users about the new comment
class MentionNotifier : CommentObserver {
    override fun onCommentAdded(
        commentId: String,
        postId: String,
        commenterId: String,
        posterId: String,
        mentionedUserIds: List<String>
    ) {
        for (uid in mentionedUserIds) {
            NotificationRepository.addNotification(
                userId = uid,
                postId = postId,
                commentId = commentId,
                commenterId = commenterId,
                type = NotificationType.MENTION,
                onSuccess = {},
                onFailure = {}
            )
        }
    }
}
