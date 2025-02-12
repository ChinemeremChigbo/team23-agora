package com.example.agora.model.util

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AccountAuthUtil {
    companion object {
        private val auth: FirebaseAuth = FirebaseAuth.getInstance()

        suspend fun accountSignIn(email: String, password: String) {
            auth.signInWithEmailAndPassword(email, password).await()
            // TODO: create currentUser auth listener which should grab the login info (i.e. uuid)
        }
        suspend fun accountSignUp(email: String, password: String) {
            auth.createUserWithEmailAndPassword(email, password).await()
        }
        suspend fun signOut() {
            auth.signOut()
        }
        suspend fun deleteAccount() {
            auth.currentUser!!.delete().await()
        }
    }
}