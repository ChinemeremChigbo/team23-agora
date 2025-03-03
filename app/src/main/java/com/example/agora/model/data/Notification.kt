package com.example.agora.model.data

import java.util.*
import java.sql.Timestamp

enum class NotificationStatus {
    READ, UNREAD
}

class Notification(
    private var targetUser: String = "",
    private var status: NotificationStatus = NotificationStatus.UNREAD,
    private var createdAt: Timestamp = Timestamp(System.currentTimeMillis()),
    private var eventInfo: String = ""
) {

    // Getters and Setters
    fun getTargetUser(): String = targetUser
    fun setTargetUser(value: String) { targetUser = value }

    fun getStatus(): NotificationStatus = status
    fun setStatus(value: NotificationStatus) { status = value }

    fun getCreatedAt(): Timestamp = createdAt
    fun setCreatedAt(value: Timestamp) { createdAt = value }

    fun getEventInfo(): String = eventInfo
    fun setEventInfo(value: String) { eventInfo = value }

    // Methods
    fun getFormattedNotification(): String {
        // TODO
        return ""
    }

    fun changeStatus(newStatus: NotificationStatus) {
        status = newStatus
    }
}
