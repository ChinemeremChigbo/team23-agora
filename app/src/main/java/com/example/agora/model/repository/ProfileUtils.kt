package com.example.agora.model.repository

import com.example.agora.model.data.User
import com.example.agora.model.util.UserManager.currentUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ProfileSettingUtils {
    companion object {
        // NOTE: to get current user, DO NOT use this function,
        //      simply call UserManager.fetchUser OR UserManager.currentUser
        fun getUserById(uid: String, callback: (User?) -> Unit) {
            FirebaseFirestore.getInstance().collection("users").document(uid)
                .get()
                .addOnSuccessListener { document ->
                    currentUser = document.data?.let { User.convertDBEntryToUser(it) }
                    callback(currentUser)
                }
                .addOnFailureListener {
                    callback(null)
                }
        }

        suspend fun getUserByIdSync(uid: String): User? {
            return suspendCancellableCoroutine { continuation ->
                FirebaseFirestore.getInstance().collection("users").document(uid)
                    .get()
                    .addOnSuccessListener { document ->
                        val user = document.data?.let { User.convertDBEntryToUser(it) }
                        continuation.resume(user)
                    }
                    .addOnFailureListener {
                        continuation.resume(null)
                    }
            }
        }
    }
}