package com.example.agora.model.data

import com.example.agora.model.repository.UserRepository
import java.sql.Timestamp

enum class UserStatus {
    ACTIVATED, DEACTIVATED
}

class User(

    var userId: String = "",
    var status: UserStatus = UserStatus.ACTIVATED,
    var username: String = "",
    var fullName: String = "",
    var bio: String = "",
    var profileImage: String? = null,
    var email: String = "",
    var phoneNumber: String = "",
    var address: Address = Address(),
    var wishList: MutableMap<String, Timestamp> = mutableMapOf(),
    var isEmailVerified: Boolean = false
) {
    // Methods
    // NOTE: we don't need login method as Firebase handle password management
    fun register() {
        UserRepository.register(this)
    }

    fun updateInfo(newInfo: Map<String, Any>) {
        UserRepository.update(this, newInfo)
    }

    fun setUserEmailAsVerified() {
        updateInfo(mapOf("isEmailVerified" to true))
    }

    fun updateUserStatus(newStatus: UserStatus) {
        status = newStatus
        updateInfo(mapOf("status" to newStatus.name))
    }

    companion object {
        fun convertDBEntryToUser(entry: Map<String, Any>): User {
            return User(
                userId = entry["userId"].toString(),
                status = UserStatus.entries.find { it.name == entry["status"] }
                    ?: UserStatus.DEACTIVATED,
                username = entry["username"].toString(),
                fullName = entry["fullName"].toString(),
                bio = entry["bio"].toString(),
                profileImage = entry["profileImage"]?.toString()
                    ?: "https://picsum.photos/200", // Handle empty images
                email = entry["email"].toString(),
                phoneNumber = entry["phoneNumber"].toString(),
                address = (entry["address"] as? Map<String, Any>)?.let {
                    Address.convertDBEntryToAddress(it)
                } ?: Address(),
                isEmailVerified = entry["isEmailVerified"]?.toString()?.toBooleanStrictOrNull()
                    ?: false
            )
        }
    }
}
