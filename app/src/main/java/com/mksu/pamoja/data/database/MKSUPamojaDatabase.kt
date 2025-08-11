package com.mksu.pamoja.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.mksu.pamoja.data.dao.*
import com.mksu.pamoja.data.model.*

/**
 * Room database for MKSU Pamoja app
 */
@Database(
    entities = [
        User::class,
        Counselor::class,
        Appointment::class,
        Resource::class,
        ChatMessage::class,
        ChatSession::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MKSUPamojaDatabase : RoomDatabase() {
    
    abstract fun userDao(): UserDao
    abstract fun counselorDao(): CounselorDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun resourceDao(): ResourceDao
    abstract fun chatDao(): ChatDao
    
    companion object {
        @Volatile
        private var INSTANCE: MKSUPamojaDatabase? = null
        
        fun getDatabase(context: Context): MKSUPamojaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MKSUPamojaDatabase::class.java,
                    "mksu_pamoja_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
