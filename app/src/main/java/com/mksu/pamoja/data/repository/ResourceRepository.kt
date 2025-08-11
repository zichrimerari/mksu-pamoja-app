package com.mksu.pamoja.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.mksu.pamoja.data.dao.ResourceDao
import com.mksu.pamoja.data.model.Resource
import com.mksu.pamoja.data.model.ResourceCategory
import com.mksu.pamoja.data.model.ResourceType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for Resource data operations
 */
@Singleton
class ResourceRepository @Inject constructor(
    private val resourceDao: ResourceDao,
    private val firestore: FirebaseFirestore
) {
    
    companion object {
        private const val RESOURCES_COLLECTION = "resources"
    }
    
    // Local database operations
    suspend fun getResourceById(resourceId: String): Resource? {
        return resourceDao.getResourceById(resourceId)
    }
    
    fun getAllResources(): Flow<List<Resource>> {
        return resourceDao.getAllResources()
    }
    
    fun getResourcesByCategory(category: ResourceCategory): Flow<List<Resource>> {
        return resourceDao.getResourcesByCategory(category)
    }
    
    fun getResourcesByType(type: ResourceType): Flow<List<Resource>> {
        return resourceDao.getResourcesByType(type)
    }
    
    fun getBookmarkedResources(): Flow<List<Resource>> {
        return resourceDao.getBookmarkedResources()
    }
    
    fun searchResources(query: String): Flow<List<Resource>> {
        return resourceDao.searchResources(query)
    }
    
    fun getPopularResources(limit: Int = 10): Flow<List<Resource>> {
        return resourceDao.getPopularResources(limit)
    }
    
    suspend fun insertResource(resource: Resource) {
        resourceDao.insertResource(resource)
    }
    
    suspend fun updateResource(resource: Resource) {
        resourceDao.updateResource(resource)
        updateResourceInFirestore(resource)
    }
    
    suspend fun toggleBookmark(resourceId: String, isBookmarked: Boolean) {
        resourceDao.updateBookmarkStatus(resourceId, isBookmarked)
    }
    
    suspend fun incrementViews(resourceId: String) {
        resourceDao.incrementViews(resourceId)
        // Also update in Firestore
        firestore.collection(RESOURCES_COLLECTION)
            .document(resourceId)
            .update("views", com.google.firebase.firestore.FieldValue.increment(1))
    }
    
    suspend fun incrementLikes(resourceId: String) {
        resourceDao.incrementLikes(resourceId)
        // Also update in Firestore
        firestore.collection(RESOURCES_COLLECTION)
            .document(resourceId)
            .update("likes", com.google.firebase.firestore.FieldValue.increment(1))
    }
    
    // Firestore operations
    suspend fun syncResourcesFromFirestore(): Result<List<Resource>> {
        return try {
            val snapshot = firestore.collection(RESOURCES_COLLECTION)
                .get()
                .await()
            
            val resources = snapshot.documents.mapNotNull { document ->
                document.toObject(Resource::class.java)
            }
            
            // Cache locally
            resourceDao.insertResources(resources)
            Result.success(resources)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private suspend fun updateResourceInFirestore(resource: Resource) {
        try {
            firestore.collection(RESOURCES_COLLECTION)
                .document(resource.id)
                .set(resource)
                .await()
        } catch (e: Exception) {
            // Handle error silently or log
        }
    }
}
