package com.mksu.pamoja.data.dao

import androidx.room.*
import com.mksu.pamoja.data.model.Appointment
import com.mksu.pamoja.data.model.AppointmentStatus
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Appointment entity
 */
@Dao
interface AppointmentDao {
    
    @Query("SELECT * FROM appointments WHERE id = :appointmentId")
    suspend fun getAppointmentById(appointmentId: String): Appointment?
    
    @Query("SELECT * FROM appointments WHERE userId = :userId ORDER BY scheduledDateTime DESC")
    fun getAppointmentsByUser(userId: String): Flow<List<Appointment>>
    
    @Query("SELECT * FROM appointments WHERE counselorId = :counselorId ORDER BY scheduledDateTime DESC")
    fun getAppointmentsByCounselor(counselorId: String): Flow<List<Appointment>>
    
    @Query("SELECT * FROM appointments WHERE userId = :userId AND status = :status ORDER BY scheduledDateTime DESC")
    fun getAppointmentsByUserAndStatus(userId: String, status: AppointmentStatus): Flow<List<Appointment>>
    
    @Query("SELECT * FROM appointments WHERE scheduledDateTime >= :startTime AND scheduledDateTime <= :endTime")
    fun getAppointmentsInDateRange(startTime: Long, endTime: Long): Flow<List<Appointment>>
    
    @Query("SELECT * FROM appointments WHERE status = :status ORDER BY scheduledDateTime ASC")
    fun getAppointmentsByStatus(status: AppointmentStatus): Flow<List<Appointment>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: Appointment)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointments(appointments: List<Appointment>)
    
    @Update
    suspend fun updateAppointment(appointment: Appointment)
    
    @Delete
    suspend fun deleteAppointment(appointment: Appointment)
    
    @Query("UPDATE appointments SET status = :status, updatedAt = :updatedAt WHERE id = :appointmentId")
    suspend fun updateAppointmentStatus(appointmentId: String, status: AppointmentStatus, updatedAt: Long)
}
