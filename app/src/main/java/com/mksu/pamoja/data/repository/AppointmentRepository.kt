package com.mksu.pamoja.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.mksu.pamoja.data.dao.AppointmentDao
import com.mksu.pamoja.data.model.Appointment
import com.mksu.pamoja.data.model.AppointmentStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for Appointment data operations
 */
@Singleton
class AppointmentRepository @Inject constructor(
    private val appointmentDao: AppointmentDao,
    private val firestore: FirebaseFirestore
) {
    
    companion object {
        private const val APPOINTMENTS_COLLECTION = "appointments"
    }
    
    // Local database operations
    suspend fun getAppointmentById(appointmentId: String): Appointment? {
        return appointmentDao.getAppointmentById(appointmentId)
    }
    
    fun getAppointmentsByUser(userId: String): Flow<List<Appointment>> {
        return appointmentDao.getAppointmentsByUser(userId)
    }
    
    fun getAppointmentsByCounselor(counselorId: String): Flow<List<Appointment>> {
        return appointmentDao.getAppointmentsByCounselor(counselorId)
    }
    
    fun getAppointmentsByUserAndStatus(userId: String, status: AppointmentStatus): Flow<List<Appointment>> {
        return appointmentDao.getAppointmentsByUserAndStatus(userId, status)
    }
    
    fun getAppointmentsInDateRange(startTime: Long, endTime: Long): Flow<List<Appointment>> {
        return appointmentDao.getAppointmentsInDateRange(startTime, endTime)
    }
    
    suspend fun insertAppointment(appointment: Appointment) {
        appointmentDao.insertAppointment(appointment)
    }
    
    suspend fun updateAppointment(appointment: Appointment) {
        appointmentDao.updateAppointment(appointment)
        // Also update in Firestore
        updateAppointmentInFirestore(appointment)
    }
    
    suspend fun updateAppointmentStatus(appointmentId: String, status: AppointmentStatus) {
        val updatedAt = System.currentTimeMillis()
        appointmentDao.updateAppointmentStatus(appointmentId, status, updatedAt)
        
        // Update in Firestore as well
        firestore.collection(APPOINTMENTS_COLLECTION)
            .document(appointmentId)
            .update(
                mapOf(
                    "status" to status,
                    "updatedAt" to updatedAt
                )
            )
    }
    
    // Firestore operations
    suspend fun createAppointmentInFirestore(appointment: Appointment): Result<Appointment> {
        return try {
            firestore.collection(APPOINTMENTS_COLLECTION)
                .document(appointment.id)
                .set(appointment)
                .await()
            
            // Also save locally
            insertAppointment(appointment)
            Result.success(appointment)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun syncAppointmentsFromFirestore(userId: String): Result<List<Appointment>> {
        return try {
            val snapshot = firestore.collection(APPOINTMENTS_COLLECTION)
                .whereEqualTo("userId", userId)
                .get()
                .await()
            
            val appointments = snapshot.documents.mapNotNull { document ->
                document.toObject(Appointment::class.java)
            }
            
            // Cache locally
            appointmentDao.insertAppointments(appointments)
            Result.success(appointments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private suspend fun updateAppointmentInFirestore(appointment: Appointment) {
        try {
            firestore.collection(APPOINTMENTS_COLLECTION)
                .document(appointment.id)
                .set(appointment)
                .await()
        } catch (e: Exception) {
            // Handle error silently or log
        }
    }
    
    suspend fun cancelAppointment(appointmentId: String): Result<Unit> {
        return try {
            updateAppointmentStatus(appointmentId, AppointmentStatus.CANCELLED)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun confirmAppointment(appointmentId: String): Result<Unit> {
        return try {
            updateAppointmentStatus(appointmentId, AppointmentStatus.CONFIRMED)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
