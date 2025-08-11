package com.mksu.pamoja.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mksu.pamoja.data.dao.UserDao
import com.mksu.pamoja.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for User data operations
 */
@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    
    companion object {
        private const val USERS_COLLECTION = "users"
    }
    
    // Local database operations
    suspend fun getUserById(userId: String): User? {
        return userDao.getUserById(userId)
    }
    
    suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)
    }
    
    fun getAllUsers(): Flow<List<User>> {
        return userDao.getAllUsers()
    }
    
    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }
    
    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
        // Also update in Firestore
        updateUserInFirestore(user)
    }
    
    suspend fun updateLastActive(userId: String) {
        val timestamp = System.currentTimeMillis()
        userDao.updateLastActive(userId, timestamp)
        // Update in Firestore as well
        firestore.collection(USERS_COLLECTION)
            .document(userId)
            .update("lastActive", timestamp)
    }
    
    // Firestore operations
    suspend fun createUserInFirestore(user: User): Result<User> {
        return try {
            firestore.collection(USERS_COLLECTION)
                .document(user.id)
                .set(user)
                .await()
            
            // Also save locally
            insertUser(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUserFromFirestore(userId: String): Result<User?> {
        return try {
            val document = firestore.collection(USERS_COLLECTION)
                .document(userId)
                .get()
                .await()
            
            val user = document.toObject(User::class.java)
            user?.let { insertUser(it) } // Cache locally
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private suspend fun updateUserInFirestore(user: User) {
        try {
            firestore.collection(USERS_COLLECTION)
                .document(user.id)
                .set(user)
                .await()
        } catch (e: Exception) {
            // Handle error silently or log
        }
    }
    
    suspend fun syncUserData(userId: String) {
        try {
            val firestoreUser = getUserFromFirestore(userId).getOrNull()
            firestoreUser?.let { insertUser(it) }
        } catch (e: Exception) {
            // Handle sync error
        }
    }
    
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
    
    suspend fun getCurrentUser(): User? {
        val userId = getCurrentUserId() ?: return null
        return getUserById(userId) ?: getUserFromFirestore(userId).getOrNull()
    }
}
