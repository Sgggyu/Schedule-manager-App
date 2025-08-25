package com.example.schedulemanager.logic.database

import androidx.room.Database
import androidx.room.RoomDatabase

import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.impl.Migration_1_2
import com.example.schedulemanager.logic.model.Event
import com.example.schedulemanager.logic.model.Plan

@Database(version = 2, entities = [Event::class, Plan::class])
@TypeConverters(TimeConverter::class)
abstract class AppDatabase : RoomDatabase(){
    abstract fun EventDao(): com.example.schedulemanager.logic.dao.EventDao
    abstract fun PlanDao(): com.example.schedulemanager.logic.dao.PlanDao

    companion object{
        val Migration_1_2 =object : Migration(1,2){
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE IF EXISTS Plan")
                db.execSQL("""
                CREATE TABLE IF NOT EXISTS `Plan` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `name` TEXT NOT NULL,
                    `type` INTEGER NOT NULL,
                    `triggerMode` INTEGER NOT NULL,
                    `triggerTime` TEXT NOT NULL,
                    `isEnable` INTEGER NOT NULL
                   )
                """.trimIndent())
            }
        }

        private var instance: AppDatabase? = null

        @Synchronized
        fun getInstance(context: android.content.Context): AppDatabase {
            if (instance == null) {
                instance = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).addMigrations(Migration_1_2)
                    .build()
            }
            return instance!!
        }
    }
}