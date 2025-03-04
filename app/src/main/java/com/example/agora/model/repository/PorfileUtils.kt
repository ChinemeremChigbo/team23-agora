package com.example.agora.model.repository

import com.example.agora.model.data.User
import com.example.agora.model.util.UserManager.currentUser
import com.google.firebase.firestore.FirebaseFirestore

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
    }
}