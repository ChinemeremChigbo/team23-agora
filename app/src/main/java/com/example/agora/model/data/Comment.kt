package com.example.agora.model.data

import java.util.*
import java.sql.Timestamp

class Comment(
    private var commentId: UUID = UUID.randomUUID(),
    private var creatorId: UUID = UUID.randomUUID(),
    private var text: String = "",
    private var mentions: Array<UUID> = arrayOf(),
    private var createdAt: Timestamp = Timestamp(System.currentTimeMillis())
) {

    // Getters and Setters
    fun getCommentId(): UUID = commentId
    fun setCommentId(value: UUID) { commentId = value }

    fun getCreatorId(): UUID = creatorId
    fun setCreatorId(value: UUID) { creatorId = value }

    fun getText(): String = text
    fun setText(value: String) { text = value }

    fun getMentions(): Array<UUID> = mentions
    fun setMentions(value: Array<UUID>) { mentions = value }

    fun getCreatedAt(): Timestamp = createdAt
    fun setCreatedAt(value: Timestamp) { createdAt = value }

    // Methods
    private fun parseComment(rawText: String) {
        // Placeholder implementation
    }

    fun notify() {
        // Placeholder implementation
    }

    fun getFormattedComment(): String {
        return "" // Placeholder implementation
    }
}
