package com.mksu.pamoja.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.DocumentId

/**
 * Counselor data model for MKSU Pamoja app
 */
@Entity(tableName = "counselors")
data class Counselor(
    @PrimaryKey
    @DocumentId
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val profileImageUrl: String = "",
    val specializations: List<String> = emptyList(),
    val qualifications: List<String> = emptyList(),
    val bio: String = "",
    val yearsOfExperience: Int = 0,
    val isAvailable: Boolean = true,
    val rating: Double = 0.0,
    val totalSessions: Int = 0,
    val officeLocation: String = "",
    val workingHours: String = "",
    val consultationFee: Double = 0.0,
    val languages: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
) {
    val fullName: String
        get() = "Dr. $firstName $lastName"
        
    val specializationsText: String
        get() = specializations.joinToString(", ")
}
