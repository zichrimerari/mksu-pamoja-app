package com.mksu.pamoja.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mksu.pamoja.data.dao.ChatDao
import com.mksu.pamoja.data.model.ChatMessage
import com.mksu.pamoja.data.model.ChatSession
import com.mksu.pamoja.data.model.ChatStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for Chat data operations
 */
@Singleton
class ChatRepository @Inject constructor(
    private val chatDao: ChatDao,
    private val firestore: FirebaseFirestore
) {
    
    companion object {
        private const val CHAT_SESSIONS_COLLECTION = "chat_sessions"
        private const val CHAT_MESSAGES_COLLECTION = "chat_messages"
    }
    
    // Chat Sessions
    suspend fun getChatSessionById(sessionId: String): ChatSession? {
        return chatDao.getChatSessionById(sessionId)
    }
    
    fun getChatSessionsByUser(userId: String): Flow<List<ChatSession>> {
        return chatDao.getChatSessionsByUser(userId)
    }
    
    fun getChatSessionsByCounselor(counselorId: String): Flow<List<ChatSession>> {
        return chatDao.getChatSessionsByCounselor(counselorId)
    }
    
    suspend fun createChatSession(chatSession: ChatSession): Result<ChatSession> {
        return try {
            // Save to Firestore first
            firestore.collection(CHAT_SESSIONS_COLLECTION)
                .document(chatSession.id)
                .set(chatSession)
                .await()
            
            // Cache locally
            chatDao.insertChatSession(chatSession)
            Result.success(chatSession)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateChatSession(chatSession: ChatSession) {
        chatDao.updateChatSession(chatSession)
        // Also update in Firestore
        updateChatSessionInFirestore(chatSession)
    }
    
    suspend fun endChatSession(sessionId: String) {
        val endedAt = System.currentTimeMillis()
        chatDao.updateChatSessionStatus(sessionId, ChatStatus.ENDED, endedAt)
        
        // Update in Firestore
        firestore.collection(CHAT_SESSIONS_COLLECTION)
            .document(sessionId)
            .update(
                mapOf(
                    "status" to ChatStatus.ENDED,
                    "endedAt" to endedAt
                )
            )
    }
    
    // Chat Messages
    fun getMessagesByChatId(chatId: String): Flow<List<ChatMessage>> {
        return chatDao.getMessagesByChatId(chatId)
    }
    
    suspend fun sendMessage(message: ChatMessage): Result<ChatMessage> {
        return try {
            // Save to Firestore first
            firestore.collection(CHAT_MESSAGES_COLLECTION)
                .document(message.id)
                .set(message)
                .await()
            
            // Cache locally
            chatDao.insertMessage(message)
            
            // Update chat session's last message
            updateLastMessage(message.chatId, message.message, message.timestamp)
            
            Result.success(message)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun markMessagesAsRead(chatId: String, currentUserId: String) {
        chatDao.markMessagesAsRead(chatId, currentUserId)
        
        // Also update in Firestore
        val snapshot = firestore.collection(CHAT_MESSAGES_COLLECTION)
            .whereEqualTo("chatId", chatId)
            .whereNotEqualTo("senderId", currentUserId)
            .whereEqualTo("isRead", false)
            .get()
            .await()
        
        val batch = firestore.batch()
        snapshot.documents.forEach { document ->
            batch.update(document.reference, "isRead", true)
        }
        batch.commit().await()
    }
    
    suspend fun getUnreadMessagesCount(chatId: String, currentUserId: String): Int {
        return chatDao.getUnreadMessages(chatId, currentUserId).size
    }
    
    private suspend fun updateLastMessage(chatId: String, lastMessage: String, timestamp: Long) {
        try {
            firestore.collection(CHAT_SESSIONS_COLLECTION)
                .document(chatId)
                .update(
                    mapOf(
                        "lastMessage" to lastMessage,
                        "lastMessageTime" to timestamp
                    )
                )
                .await()
        } catch (e: Exception) {
            // Handle error
        }
    }
    
    private suspend fun updateChatSessionInFirestore(chatSession: ChatSession) {
        try {
            firestore.collection(CHAT_SESSIONS_COLLECTION)
                .document(chatSession.id)
                .set(chatSession)
                .await()
        } catch (e: Exception) {
            // Handle error silently or log
        }
    }
    
    suspend fun syncMessagesFromFirestore(chatId: String): Result<List<ChatMessage>> {
        return try {
            val snapshot = firestore.collection(CHAT_MESSAGES_COLLECTION)
                .whereEqualTo("chatId", chatId)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .await()
            
            val messages = snapshot.documents.mapNotNull { document ->
                document.toObject(ChatMessage::class.java)
            }
            
            // Cache locally
            chatDao.insertMessages(messages)
            Result.success(messages)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
