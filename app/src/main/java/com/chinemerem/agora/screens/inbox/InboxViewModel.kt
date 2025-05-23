package com.chinemerem.agora.screens.inbox

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.chinemerem.agora.model.data.Notification
import com.chinemerem.agora.model.repository.NotificationRepository
import com.google.firebase.auth.FirebaseAuth
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InboxViewModel : ViewModel() {
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    private val _isLoading = MutableLiveData<Boolean>(true)
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        getSuspendedResults()
    }

    fun getSuspendedResults() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                getNotifications()
            } catch (e: Exception) {
                // Handle any exceptions that occur
                // TODO: add error screen component and display the component "oops something went wrong"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun getNotifications(): List<Notification> {
        return suspendCoroutine { continuation ->
            if (userId != null) {
                NotificationRepository.getUserNotifications(userId, callback = { result ->
                    _notifications.value = result
                    continuation.resume(_notifications.value)
                }, onFailure = {})
            }
        }
    }

    fun viewNotification(notif: Notification, navController: NavController) {
        // navigate to post detail page
        navController.navigate("post_detail/${notif.postId}")

        // call backend api to delete the notification
        // comment out deleting notification for now
        // NotificationUtils.removeNotification(notif.userId, notif.notificationId, onSuccess = { Log.d("hehe", "onsuccess")}, onFailure = {})
    }
}
