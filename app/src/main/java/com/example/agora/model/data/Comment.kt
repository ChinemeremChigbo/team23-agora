package com.example.agora.model.data

import java.util.*
import java.sql.Timestamp

class Comment(
    private val commentId: UUID = UUID.randomUUID(),
    private val creatorId: UUID = UUID.randomUUID(),
    private var text: String = "",
    private var mentions: Array<UUID> = arrayOf(),
    private var createdAt: Timestamp = Timestamp(System.currentTimeMillis())
) {

    // Getters and Setters

    // comment and creator id should not be changeable
    fun getCommentId(): UUID = commentId
    fun getCreatorId(): UUID = creatorId

    fun getText(): String = text
    fun setText(value: String) { text = value }

    fun getMentions(): Array<UUID> = mentions
    fun setMentions(value: Array<UUID>) { mentions = value }

    fun getCreatedAt(): Timestamp = createdAt
    fun setCreatedAt(value: Timestamp) { createdAt = value }

    // returns a list of usernames
    fun findMentions(): List<String> {
        return "@(\\S+)".toRegex().findAll(text).map { it.groupValues[1] }.toList()
    }

    fun notify(users: List<String>, comment: String) {
        // TODO
    }

    fun getFormattedComment(): String {
        return getText()
    }
}
