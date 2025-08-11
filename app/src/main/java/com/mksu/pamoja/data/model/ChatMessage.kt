package com.mksu.pamoja.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.DocumentId

/**
 * Chat message data model for MKSU Pamoja app
 */
@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey
    @DocumentId
    val id: String = "",
    val chatId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val senderType: UserType = UserType.STUDENT,
    val message: String = "",
    val messageType: MessageType = MessageType.TEXT,
    val attachmentUrl: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val isEdited: Boolean = false,
    val editedAt: Long = 0L
)

/**
 * Chat session data model
 */
@Entity(tableName = "chat_sessions")
data class ChatSession(
    @PrimaryKey
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val counselorId: String = "",
    val title: String = "",
    val status: ChatStatus = ChatStatus.ACTIVE,
    val lastMessage: String = "",
    val lastMessageTime: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis(),
    val endedAt: Long = 0L
)

enum class UserType {
    STUDENT,
    COUNSELOR,
    ADMIN
}

enum class MessageType {
    TEXT,
    IMAGE,
    AUDIO,
    FILE,
    SYSTEM
}

enum class ChatStatus {
    ACTIVE,
    ENDED,
    ARCHIVED
}
