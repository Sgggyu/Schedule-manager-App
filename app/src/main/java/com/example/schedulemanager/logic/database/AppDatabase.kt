package com.example.schedulemanager.logic.database

import androidx.room.Database
import androidx.room.RoomDatabase

import androidx.room.TypeConverters
import com.example.schedulemanager.logic.model.Event
import com.example.schedulemanager.logic.model.Plan

@Database(version = 1, entities = [Event::class, Plan::class])
@TypeConverters(TimeConverter::class)
abstract class AppDatabase : RoomDatabase(){
    abstract fun EventDao(): com.example.schedulemanager.logic.dao.EventDao
    abstract fun PlanDao(): com.example.schedulemanager.logic.dao.PlanDao

    companion object{
        private var instance: AppDatabase? = null

        @Synchronized
        fun getInstance(context: android.content.Context): AppDatabase {
            if (instance == null) {
                instance = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
            }
            return instance!!
        }
    }
}