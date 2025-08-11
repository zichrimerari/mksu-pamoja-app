package com.mksu.pamoja.di

import android.content.Context
import androidx.room.Room
import com.mksu.pamoja.data.dao.*
import com.mksu.pamoja.data.database.MKSUPamojaDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for database dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MKSUPamojaDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            MKSUPamojaDatabase::class.java,
            "mksu_pamoja_database"
        ).build()
    }
    
    @Provides
    fun provideUserDao(database: MKSUPamojaDatabase): UserDao {
        return database.userDao()
    }
    
    @Provides
    fun provideCounselorDao(database: MKSUPamojaDatabase): CounselorDao {
        return database.counselorDao()
    }
    
    @Provides
    fun provideAppointmentDao(database: MKSUPamojaDatabase): AppointmentDao {
        return database.appointmentDao()
    }
    
    @Provides
    fun provideResourceDao(database: MKSUPamojaDatabase): ResourceDao {
        return database.resourceDao()
    }
    
    @Provides
    fun provideChatDao(database: MKSUPamojaDatabase): ChatDao {
        return database.chatDao()
    }
}
