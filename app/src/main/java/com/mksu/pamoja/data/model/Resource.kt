package com.mksu.pamoja.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.DocumentId

/**
 * Mental health resource data model for MKSU Pamoja app
 */
@Entity(tableName = "resources")
data class Resource(
    @PrimaryKey
    @DocumentId
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val content: String = "",
    val category: ResourceCategory = ResourceCategory.GENERAL,
    val type: ResourceType = ResourceType.ARTICLE,
    val imageUrl: String = "",
    val videoUrl: String = "",
    val audioUrl: String = "",
    val pdfUrl: String = "",
    val tags: List<String> = emptyList(),
    val author: String = "",
    val readingTime: Int = 0, // in minutes
    val isBookmarked: Boolean = false,
    val likes: Int = 0,
    val views: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class ResourceCategory {
    GENERAL,
    ANXIETY,
    DEPRESSION,
    STRESS_MANAGEMENT,
    RELATIONSHIPS,
    ACADEMIC_PRESSURE,
    SELF_CARE,
    MINDFULNESS,
    CRISIS_SUPPORT
}

enum class ResourceType {
    ARTICLE,
    VIDEO,
    AUDIO,
    PDF,
    INTERACTIVE,
    QUIZ
}
