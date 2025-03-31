package com.example.agora.screens.inbox

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.agora.model.data.Notification
import com.example.agora.model.repository.NotificationUtils
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

    private val _hasError = MutableStateFlow(false)
    val hasError: StateFlow<Boolean> get() = _hasError

    init {
        getSuspendedResults()
    }

    fun getSuspendedResults() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _hasError.value = false
                getNotifications()
            } catch (e: Exception) {
                _hasError.value = true
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun getNotifications(): List<Notification> {
        return suspendCoroutine { continuation ->
            if (userId != null) {
                NotificationUtils.getUserNotifications(userId, callback = { result ->
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
