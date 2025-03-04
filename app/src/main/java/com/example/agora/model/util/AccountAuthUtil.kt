package com.example.agora.model.util

import android.provider.ContactsContract.Profile
import com.example.agora.model.data.User
import com.example.agora.model.repository.ProfileSettingUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AccountAuthUtil {
    companion object {
        suspend fun accountSignIn(auth: FirebaseAuth, email: String, password: String) {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null && !user.isEmailVerified) {
                sendVerificationEmail(auth)
                auth.signOut()
                throw Exception("Please verify your email before logging in, check your email inbox!")
            }
            // TODO: create currentUser auth listener which should grab the login info (i.e. uuid)
        }
        suspend fun accountSignUp(auth: FirebaseAuth, email: String, password: String): String {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            // send verification email
            println("FirebaseAuth sending verification email for ${result.user?.email}...")

            result.user!!.sendEmailVerification().await()
            return result.user!!.uid
        }
        fun signOut(auth: FirebaseAuth) {
            auth.signOut()
        }
        private suspend fun sendVerificationEmail(auth: FirebaseAuth){
            val user = auth.currentUser
            println("FirebaseAuth sendVerificationEmail")
            if (user != null) {
                println("FirebaseAuth resending verification email for ${user.email}")
            }
            user?.sendEmailVerification()?.await()
        }
        suspend fun deleteAccount(auth: FirebaseAuth) {
            auth.currentUser!!.delete().await()
        }
    }
}

// Allow cross-app access details about current user
object UserManager {
    var currentUser: User? = null

    fun fetchUser(uid: String, onComplete: (User?) -> Unit) {
        if (currentUser != null) {
            onComplete(currentUser)
            return
        }

        // only calls database once!
        ProfileSettingUtils.getUserById(uid, onComplete)
    }
}