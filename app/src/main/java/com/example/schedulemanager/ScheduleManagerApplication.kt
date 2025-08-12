package com.example.schedulemanager

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.example.schedulemanager.logic.dao.EventDao
import com.example.schedulemanager.logic.dao.PlanDao
import com.example.schedulemanager.logic.database.AppDatabase

class ScheduleManagerApplication : Application() {
    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        lateinit var database: AppDatabase
            private set

        lateinit var eventDao: EventDao
            private set

        lateinit var planDao: PlanDao
            private set
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        database = AppDatabase.getInstance(context = context)
        eventDao = database.EventDao()
        planDao = database.PlanDao()

    }
}