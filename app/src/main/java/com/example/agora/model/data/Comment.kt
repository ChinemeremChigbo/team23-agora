package com.example.agora.model.data

import java.util.*
import com.google.firebase.Timestamp

class Comment(
    val commentId: String = "",
    val creatorId: String = "",
    var text: String = "",
    var createdAt: Timestamp? = Timestamp.now(),
    var mentions: List<String> = listOf(),
    var parentCommentID: String = ""
) {

    // Getters and Setters

//    // comment and creator id should not be changeable
//    fun getCommentId(): String = commentId
//    fun getCreatorId(): String = creatorId
//
//    fun getText(): String = text
//    fun setText(value: String) { text = value }
//
//    fun getMentions(): Array<String> = mentions
//    fun setMentions(value: Array<String>) { mentions = value }
//
//    fun getCreatedAt(): Timestamp = createdAt
//    fun setCreatedAt(value: Timestamp) { createdAt = value }

    // returns a list of usernames
    fun findMentions(): List<String> {
        return "@(\\S+)".toRegex().findAll(text).map { it.groupValues[1] }.toList()
    }

    fun notify(users: List<String>, comment: String) {
        // TODO
    }

//    fun getFormattedComment(): String {
//        return getText()
//    }
}
