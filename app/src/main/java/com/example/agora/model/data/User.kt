package com.example.agora.model.data
import java.util.*

enum class UserStatus {
    ACTIVATED, DEACTIVATED
}

class User(
    private var userId: UUID = UUID.randomUUID(),
    private var status: UserStatus = UserStatus.ACTIVATED,
    var username: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var bio: String = "",
    var profileImage: String = "",
    var email: String = "",
    var phoneNumber: String = ""
) {

    // Getters and Setters
    fun getUserId(): UUID = userId
    fun setUserId(value: UUID) { userId = value }

    fun getStatus(): UserStatus = status
    fun setStatus(value: UserStatus) { status = value }

    // Methods
    fun login(password: String): Boolean {
        // TODO
        return false
    }

    fun register() {
        // TODO
    }

    fun updateInfo(newInfo: Map<String, Any>) {
        // TODO
    }

    fun changeStatus(newStatus: UserStatus) {
        status = newStatus
    }
}
