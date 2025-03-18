package com.example.agora.screens.inbox

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.agora.model.data.Notification
import com.example.agora.model.repository.NotificationUtils
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class InboxViewModel : ViewModel() {
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    init {
        getNotifications()
    }

    fun getNotifications() {
        if (userId != null) {
            NotificationUtils.getUserNotifications(userId, callback = { result ->
                _notifications.value = result
            }, onFailure = {})
        }
    }

    fun viewNotification(notif: Notification, navController: NavController) {
        // navigate to post detail page
        navController.navigate("post_detail/${notif.postId}")

        // call backend api to delete the notification
        Log.d("hehe", notif.message)
        Log.d("hehe", notif.notificationId)
        NotificationUtils.removeNotification(notif.userId, notif.notificationId, onSuccess = { Log.d("hehe", "onsuccess")}, onFailure = {})
    }
}
