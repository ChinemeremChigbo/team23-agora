package com.example.agora.model.util

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AccountAuthUtil {
    companion object {
        suspend fun accountSignIn(auth: FirebaseAuth, email: String, password: String) {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null && !user.isEmailVerified) {
                auth.signOut()
                throw Exception("Please verify your email before logging in!")
            }
            // TODO: create currentUser auth listener which should grab the login info (i.e. uuid)
        }
        suspend fun accountSignUp(auth: FirebaseAuth, email: String, password: String) {
            auth.createUserWithEmailAndPassword(email, password).await()
        }
        fun signOut(auth: FirebaseAuth) {
            auth.signOut()
        }
        suspend fun deleteAccount(auth: FirebaseAuth) {
            auth.currentUser!!.delete().await()
        }
    }
}