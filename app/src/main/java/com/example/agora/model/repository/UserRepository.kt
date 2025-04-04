package com.example.agora.model.repository

import android.util.Log
import com.example.agora.model.data.User
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository {
    companion object {
        fun updateProfileImage(userId: String, imageUrl: String) {
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(userId).update("profileImage", imageUrl)
                .addOnSuccessListener {
                    Log.d("ProfileUpdate", "Profile image updated successfully in Firestore.")
                }.addOnFailureListener { e ->
                    Log.e("ProfileUpdate", "Error updating profile image: ${e.message}")
                }
        }

        fun register(user: User) {
            val db = FirebaseFirestore.getInstance()
            val address = user.address
            // Use the userId as the document ID
            db.collection("users").document(user.userId).set(
                mapOf(
                    "userId" to user.userId,
                    "status" to user.status.name, // Convert Enum to String
                    "username" to user.username,
                    "fullName" to user.fullName,
                    "bio" to user.bio,
                    "profileImage" to user.profileImage,
                    "email" to user.email,
                    "phoneNumber" to user.phoneNumber,
                    "address" to mapOf(
                        "country" to address.getCountry(),
                        "city" to address.getCity(),
                        "state" to address.getState(),
                        "address" to address.getStreet(),
                        "postalCode" to address.getPostalCode(),
                        "lat" to address.getLatLng().latitude,
                        "lng" to address.getLatLng().longitude
                    ),
                    "wishlist" to user.wishList,
                    "notifications" to emptyList<String>()
                )
            ).addOnSuccessListener {
                println("User successfully added to Firestore!")
            }.addOnFailureListener { e ->
                throw Error("Firebase Error")
            }
        }

        fun update(user: User, newInfo: Map<String, Any>) {
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(user.userId).update(newInfo).addOnSuccessListener {
                println("User information updated successfully!")
            }.addOnFailureListener { e ->
                println("Error updating user: ${e.message}")
            }
        }
    }
}
