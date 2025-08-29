package com.example.schedulemanager.logic.database

import androidx.room.Database
import androidx.room.RenameTable
import androidx.room.RoomDatabase

import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.impl.Migration_1_2
import androidx.work.impl.Migration_3_4
import com.example.schedulemanager.logic.model.Event
import com.example.schedulemanager.logic.model.Plan

@Database(version = 5, entities = [Event::class, Plan::class])
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
        val Migration_2_3 =object : Migration(2,3){
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE IF EXISTS Plan")
                db.execSQL("""
                CREATE TABLE IF NOT EXISTS `PlanTable` (
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
        val Migration_3_4 =object : Migration(3,4){
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE IF EXISTS Plan")
                db.execSQL("DROP TABLE IF EXISTS plan_table")
            }
        }
        val Migration_4_5 = object : Migration(4,5){
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                CREATE TABLE IF NOT EXISTS `planTable` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `name` TEXT NOT NULL,
                    `type` INTEGER NOT NULL,
                    `triggerMode` INTEGER NOT NULL,
                    `triggerStartTime` TEXT NOT NULL,
                    `triggerEndTime` TEXT NOT NULL,
                    `isEnable` INTEGER NOT NULL
                   )
                """.trimIndent())
            }
        }
        @RenameTable(fromTableName = "Plan", toTableName = "plan_table")
        class RenamePlanSpec : AutoMigrationSpec

        private var instance: AppDatabase? = null

        @Synchronized
        fun getInstance(context: android.content.Context): AppDatabase {
            if (instance == null) {
                instance = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                    )
                    .addMigrations(Migration_1_2,Migration_2_3,Migration_3_4,Migration_4_5)
                    .build()
            }
            return instance!!
        }
    }
}