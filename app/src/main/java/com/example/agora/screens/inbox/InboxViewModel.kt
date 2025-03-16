package com.example.agora.screens.inbox

import androidx.lifecycle.ViewModel
import com.example.agora.model.data.Notification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class InboxViewModel : ViewModel() {

    // TODO: need to update this class to include id, preview img, user name, post name, post id
    val notif = Notification(
        targetUser = "user123",
        eventInfo = "bicycle 12345"
    )
    val dummy_notifications: List<Notification> = listOf(notif, notif, notif, notif)

    private val _notifications = MutableStateFlow<List<Notification>>(dummy_notifications)
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    fun viewNotification() {
        // navigate to post detail page
        // call backend api to delete the notification
    }
}
