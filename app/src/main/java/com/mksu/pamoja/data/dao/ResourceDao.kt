package com.mksu.pamoja.data.dao

import androidx.room.*
import com.mksu.pamoja.data.model.Resource
import com.mksu.pamoja.data.model.ResourceCategory
import com.mksu.pamoja.data.model.ResourceType
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Resource entity
 */
@Dao
interface ResourceDao {
    
    @Query("SELECT * FROM resources WHERE id = :resourceId")
    suspend fun getResourceById(resourceId: String): Resource?
    
    @Query("SELECT * FROM resources ORDER BY createdAt DESC")
    fun getAllResources(): Flow<List<Resource>>
    
    @Query("SELECT * FROM resources WHERE category = :category ORDER BY createdAt DESC")
    fun getResourcesByCategory(category: ResourceCategory): Flow<List<Resource>>
    
    @Query("SELECT * FROM resources WHERE type = :type ORDER BY createdAt DESC")
    fun getResourcesByType(type: ResourceType): Flow<List<Resource>>
    
    @Query("SELECT * FROM resources WHERE isBookmarked = 1 ORDER BY createdAt DESC")
    fun getBookmarkedResources(): Flow<List<Resource>>
    
    @Query("SELECT * FROM resources WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%'")
    fun searchResources(query: String): Flow<List<Resource>>
    
    @Query("SELECT * FROM resources ORDER BY likes DESC LIMIT :limit")
    fun getPopularResources(limit: Int = 10): Flow<List<Resource>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResource(resource: Resource)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResources(resources: List<Resource>)
    
    @Update
    suspend fun updateResource(resource: Resource)
    
    @Delete
    suspend fun deleteResource(resource: Resource)
    
    @Query("UPDATE resources SET isBookmarked = :isBookmarked WHERE id = :resourceId")
    suspend fun updateBookmarkStatus(resourceId: String, isBookmarked: Boolean)
    
    @Query("UPDATE resources SET views = views + 1 WHERE id = :resourceId")
    suspend fun incrementViews(resourceId: String)
    
    @Query("UPDATE resources SET likes = likes + 1 WHERE id = :resourceId")
    suspend fun incrementLikes(resourceId: String)
}
