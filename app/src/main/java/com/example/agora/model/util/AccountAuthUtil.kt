package com.example.agora.model.util

import com.example.agora.model.data.User
import com.example.agora.model.repository.ProfileSettingUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AccountAuthUtil {
    companion object {
        suspend fun accountSignIn(auth: FirebaseAuth, email: String, password: String) {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null && !user.isEmailVerified) {
                sendVerificationEmail()
                auth.signOut()
                throw Exception("Please verify your email before logging in, check your email inbox!")
            }
            // TODO: create currentUser auth listener which should grab the login info (i.e. uuid)
        }

        suspend fun accountSignUp(auth: FirebaseAuth, email: String, password: String): String {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            // send verification email
            println("FirebaseAuth sending verification email for ${result.user?.email}...")

            sendVerificationEmail()
            return result.user!!.uid
        }

        fun signOut(auth: FirebaseAuth) {
            auth.signOut()
        }

        fun updatePassword(auth: FirebaseAuth, newPassword: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
            val user: FirebaseUser? = auth.currentUser

            // Check if the user is logged in
            if (user != null) {
                user.updatePassword(newPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            onSuccess() // Callback when password is updated successfully
                        } else {
                            onFailure("Password update failed: ${task.exception?.localizedMessage}")
                        }
                    }
            } else {
                onFailure("User not logged in.")
            }
        }

        private fun sendVerificationEmail(){
            EmailTemplate.verificationCode = EmailTemplate.generateVerificationCode()
            val emailRequest = EmailRequest(
                sender = Sender("Agora", "agoraapp.help@gmail.com"),
                to = listOf(Recipient("j35zhan@uwaterloo.ca", "User")),
                subject = "Verify your email for Agora",
                htmlContent = EmailTemplate.htmlContent
            )

            BrevoClient.service.sendEmail(emailRequest).enqueue(object : retrofit2.Callback<EmailResponse> {
                override fun onResponse(call: retrofit2.Call<EmailResponse>, response: retrofit2.Response<EmailResponse>) {
                    if (response.isSuccessful) {
                        println("Brevo Email sent successfully: ${response.body()?.messageId}")
                    } else {
                        println("Brevo Failed to send email: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: retrofit2.Call<EmailResponse>, t: Throwable) {
                    println("Brevo Error: ${t.message}")
                }
            })
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