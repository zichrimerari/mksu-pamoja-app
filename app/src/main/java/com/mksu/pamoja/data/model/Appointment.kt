package com.mksu.pamoja.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.DocumentId

/**
 * Appointment data model for MKSU Pamoja app
 */
@Entity(tableName = "appointments")
data class Appointment(
    @PrimaryKey
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val counselorId: String = "",
    val title: String = "",
    val description: String = "",
    val scheduledDateTime: Long = 0L,
    val duration: Int = 60, // Duration in minutes
    val status: AppointmentStatus = AppointmentStatus.PENDING,
    val type: AppointmentType = AppointmentType.IN_PERSON,
    val location: String = "",
    val meetingLink: String = "",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class AppointmentStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    COMPLETED,
    NO_SHOW
}

enum class AppointmentType {
    IN_PERSON,
    VIDEO_CALL,
    PHONE_CALL
}
