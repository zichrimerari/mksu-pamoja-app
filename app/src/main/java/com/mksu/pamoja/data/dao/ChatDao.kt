package com.mksu.pamoja.data.dao

import androidx.room.*
import com.mksu.pamoja.data.model.ChatMessage
import com.mksu.pamoja.data.model.ChatSession
import com.mksu.pamoja.data.model.ChatStatus
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Chat entities
 */
@Dao
interface ChatDao {
    
    // Chat Sessions
    @Query("SELECT * FROM chat_sessions WHERE id = :sessionId")
    suspend fun getChatSessionById(sessionId: String): ChatSession?
    
    @Query("SELECT * FROM chat_sessions WHERE userId = :userId ORDER BY lastMessageTime DESC")
    fun getChatSessionsByUser(userId: String): Flow<List<ChatSession>>
    
    @Query("SELECT * FROM chat_sessions WHERE counselorId = :counselorId ORDER BY lastMessageTime DESC")
    fun getChatSessionsByCounselor(counselorId: String): Flow<List<ChatSession>>
    
    @Query("SELECT * FROM chat_sessions WHERE status = :status ORDER BY lastMessageTime DESC")
    fun getChatSessionsByStatus(status: ChatStatus): Flow<List<ChatSession>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatSession(chatSession: ChatSession)
    
    @Update
    suspend fun updateChatSession(chatSession: ChatSession)
    
    @Query("UPDATE chat_sessions SET status = :status, endedAt = :endedAt WHERE id = :sessionId")
    suspend fun updateChatSessionStatus(sessionId: String, status: ChatStatus, endedAt: Long)
    
    // Chat Messages
    @Query("SELECT * FROM chat_messages WHERE chatId = :chatId ORDER BY timestamp ASC")
    fun getMessagesByChatId(chatId: String): Flow<List<ChatMessage>>
    
    @Query("SELECT * FROM chat_messages WHERE id = :messageId")
    suspend fun getMessageById(messageId: String): ChatMessage?
    
    @Query("SELECT * FROM chat_messages WHERE chatId = :chatId AND isRead = 0 AND senderId != :currentUserId")
    suspend fun getUnreadMessages(chatId: String, currentUserId: String): List<ChatMessage>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<ChatMessage>)
    
    @Update
    suspend fun updateMessage(message: ChatMessage)
    
    @Query("UPDATE chat_messages SET isRead = 1 WHERE chatId = :chatId AND senderId != :currentUserId")
    suspend fun markMessagesAsRead(chatId: String, currentUserId: String)
    
    @Delete
    suspend fun deleteMessage(message: ChatMessage)
}
