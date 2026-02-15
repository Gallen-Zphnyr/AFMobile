package com.example.afmobile.data

import com.google.firebase.Timestamp

/**
 * User data class
 * Essential fields: uid, username, email
 * Optional fields: profilePicture, phoneNumber, address (can be set later)
 */
data class User(
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val profilePicture: String? = null,
    val phoneNumber: String? = null,
    val address: String? = null,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)

/**
 * Firebase User data class for Firestore parsing
 * Only name and email are required initially
 * Location/address can be set later by user
 */
data class FirebaseUser(
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val profilePicture: String? = null,
    val phoneNumber: String? = null,
    val address: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) {
    fun toUser(): User {
        return User(
            uid = uid,
            username = username,
            email = email,
            profilePicture = profilePicture,
            phoneNumber = phoneNumber,
            address = address,
            createdAt = createdAt?.seconds ?: 0L,
            updatedAt = updatedAt?.seconds ?: 0L
        )
    }
}
