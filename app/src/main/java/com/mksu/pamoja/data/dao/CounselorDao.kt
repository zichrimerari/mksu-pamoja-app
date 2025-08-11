package com.mksu.pamoja.data.dao

import androidx.room.*
import com.mksu.pamoja.data.model.Counselor
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Counselor entity
 */
@Dao
interface CounselorDao {
    
    @Query("SELECT * FROM counselors WHERE id = :counselorId")
    suspend fun getCounselorById(counselorId: String): Counselor?
    
    @Query("SELECT * FROM counselors WHERE isAvailable = 1")
    fun getAvailableCounselors(): Flow<List<Counselor>>
    
    @Query("SELECT * FROM counselors ORDER BY rating DESC")
    fun getAllCounselorsOrderedByRating(): Flow<List<Counselor>>
    
    @Query("SELECT * FROM counselors WHERE specializations LIKE '%' || :specialization || '%'")
    fun getCounselorsBySpecialization(specialization: String): Flow<List<Counselor>>
    
    @Query("SELECT * FROM counselors")
    fun getAllCounselors(): Flow<List<Counselor>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCounselor(counselor: Counselor)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCounselors(counselors: List<Counselor>)
    
    @Update
    suspend fun updateCounselor(counselor: Counselor)
    
    @Delete
    suspend fun deleteCounselor(counselor: Counselor)
    
    @Query("UPDATE counselors SET isAvailable = :isAvailable WHERE id = :counselorId")
    suspend fun updateAvailability(counselorId: String, isAvailable: Boolean)
}
