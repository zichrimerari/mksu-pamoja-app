package com.mksu.pamoja.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.mksu.pamoja.data.dao.CounselorDao
import com.mksu.pamoja.data.model.Counselor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for Counselor data operations
 */
@Singleton
class CounselorRepository @Inject constructor(
    private val counselorDao: CounselorDao,
    private val firestore: FirebaseFirestore
) {
    
    companion object {
        private const val COUNSELORS_COLLECTION = "counselors"
    }
    
    // Local database operations
    suspend fun getCounselorById(counselorId: String): Counselor? {
        return counselorDao.getCounselorById(counselorId)
    }
    
    fun getAvailableCounselors(): Flow<List<Counselor>> {
        return counselorDao.getAvailableCounselors()
    }
    
    fun getAllCounselorsOrderedByRating(): Flow<List<Counselor>> {
        return counselorDao.getAllCounselorsOrderedByRating()
    }
    
    fun getCounselorsBySpecialization(specialization: String): Flow<List<Counselor>> {
        return counselorDao.getCounselorsBySpecialization(specialization)
    }
    
    fun getAllCounselors(): Flow<List<Counselor>> {
        return counselorDao.getAllCounselors()
    }
    
    suspend fun insertCounselor(counselor: Counselor) {
        counselorDao.insertCounselor(counselor)
    }
    
    suspend fun updateCounselor(counselor: Counselor) {
        counselorDao.updateCounselor(counselor)
        // Also update in Firestore
        updateCounselorInFirestore(counselor)
    }
    
    suspend fun updateAvailability(counselorId: String, isAvailable: Boolean) {
        counselorDao.updateAvailability(counselorId, isAvailable)
        // Update in Firestore as well
        firestore.collection(COUNSELORS_COLLECTION)
            .document(counselorId)
            .update("isAvailable", isAvailable)
    }
    
    // Firestore operations
    suspend fun syncCounselorsFromFirestore(): Result<List<Counselor>> {
        return try {
            val snapshot = firestore.collection(COUNSELORS_COLLECTION)
                .get()
                .await()
            
            val counselors = snapshot.documents.mapNotNull { document ->
                document.toObject(Counselor::class.java)
            }
            
            // Cache locally
            counselorDao.insertCounselors(counselors)
            Result.success(counselors)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getCounselorFromFirestore(counselorId: String): Result<Counselor?> {
        return try {
            val document = firestore.collection(COUNSELORS_COLLECTION)
                .document(counselorId)
                .get()
                .await()
            
            val counselor = document.toObject(Counselor::class.java)
            counselor?.let { insertCounselor(it) } // Cache locally
            Result.success(counselor)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private suspend fun updateCounselorInFirestore(counselor: Counselor) {
        try {
            firestore.collection(COUNSELORS_COLLECTION)
                .document(counselor.id)
                .set(counselor)
                .await()
        } catch (e: Exception) {
            // Handle error silently or log
        }
    }
    
    suspend fun searchCounselors(query: String): Flow<List<Counselor>> {
        // For now, search by specialization
        return getCounselorsBySpecialization(query)
    }
}
