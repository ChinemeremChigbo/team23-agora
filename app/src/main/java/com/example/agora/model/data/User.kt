package com.example.agora.model.data
import com.google.firebase.firestore.FirebaseFirestore
import java.sql.Timestamp

enum class UserStatus {
    ACTIVATED, DEACTIVATED
}

class User(
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    var userId: String = "",
    var status: UserStatus = UserStatus.ACTIVATED,
    var username: String = "",
    var fullName: String = "",
    var bio: String = "",
    var profileImage: String = "",
    var email: String = "",
    var phoneNumber: String = "",
    var country: String = "",
    var state: String = "",
    var city: String = "",
    var postalCode: String = "",
    var address: String = "",
    var wishList: MutableMap<String, Timestamp> = mutableMapOf()
) {
    // Methods
    // NOTE: we don't need login method as Firebase handle password management
    fun register() {
        // Use the userId as the document ID
        db.collection("users").document(userId)
            .set(mapOf(
                "status" to status.name,  // Convert Enum to String
                "username" to username,
                "fullName" to fullName,
                "bio" to bio,
                "profileImage" to profileImage,
                "email" to email,
                "phoneNumber" to phoneNumber,
                "address" to mapOf(
                    "country" to country,
                    "city" to city,
                    "state" to state,
                    "address" to address,
                    "postalCode" to postalCode
                ),
                "wishlist" to wishList,
            ))
            .addOnSuccessListener {
                println("User successfully added to Firestore!")
            }
            .addOnFailureListener { e ->
                println("Error adding user: ${e.message}")
            }
    }

    /**
     * sample uage:
     * val updates = mapOf(
     *     "bio" to "Updated bio",
     *     "phoneNumber" to "9876543210"
     * )
     */
    fun updateInfo(newInfo: Map<String, Any>) {
        db.collection("users").document(userId)
            .update(newInfo)
            .addOnSuccessListener {
                println("User information updated successfully!")
            }
            .addOnFailureListener { e ->
                println("Error updating user: ${e.message}")
            }
    }

    fun updateUserStatus(newStatus: UserStatus) {
        status = newStatus
        updateInfo(mapOf("status" to newStatus.name))
    }
}
