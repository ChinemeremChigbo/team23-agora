package com.example.agora.model.repository

import com.example.agora.model.data.User
import com.google.firebase.firestore.FirebaseFirestore
import java.util.regex.Pattern
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

class ProfileSettingUtils {
    companion object {
        // NOTE: to get current user, DO NOT use this function,
        //      simply call UserManager.fetchUser OR UserManager.currentUser
        fun getUserById(uid: String, callback: (User?) -> Unit) {
            FirebaseFirestore.getInstance().collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    val currentUser = document.data?.let { User.convertDBEntryToUser(it) }
                    callback(currentUser)
                }.addOnFailureListener {
                    callback(null)
                }
        }

        suspend fun getUserByIdSync(uid: String): User? {
            return suspendCancellableCoroutine { continuation ->
                FirebaseFirestore.getInstance().collection("users").document(uid).get()
                    .addOnSuccessListener { document ->
                        val user = document.data?.let { User.convertDBEntryToUser(it) }
                        continuation.resume(user)
                    }.addOnFailureListener {
                        continuation.resume(null)
                    }
            }
        }

        fun isValidPhoneNumber(phone: String): Boolean {
            val pattern = Pattern.compile(
                """^\+?1?[-.\s]?\(?([2-9][0-9]{2})\)?[-.\s]?([2-9][0-9]{2})[-.\s]?([0-9]{4})$"""
            )
            return pattern.matcher(phone).matches()
        }
    }
}
