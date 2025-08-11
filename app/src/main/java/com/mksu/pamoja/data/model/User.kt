package com.mksu.pamoja.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.DocumentId

/**
 * User data model for MKSU Pamoja app
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey
    @DocumentId
    val id: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val studentId: String = "",
    val phoneNumber: String = "",
    val course: String = "",
    val yearOfStudy: Int = 1,
    val profileImageUrl: String = "",
    val isVerified: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val lastActive: Long = System.currentTimeMillis()
) {
    val fullName: String
        get() = "$firstName $lastName"
}
