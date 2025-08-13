package com.example.schedulemanager

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.provider.Settings
import androidx.lifecycle.Lifecycle
import com.example.schedulemanager.logic.dao.EventDao
import com.example.schedulemanager.logic.dao.PlanDao
import com.example.schedulemanager.logic.database.AppDatabase
import com.example.schedulemanager.logic.model.Event
import com.example.schedulemanager.logic.model.EventData
import com.example.schedulemanager.logic.model.TYPE
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime

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

    override fun onTerminate() {
        super.onTerminate()
        // 关闭数据库连接
        database.close()
    }

    fun test() {
        val august2025Events = listOf(
            // 2025-08-01
            Event(data = EventData("Sleep", LocalDateTime.of(2025,8,1,23,0), LocalDateTime.of(2025,8,1,23,59), 5, TYPE.SLEEP, "Night sleep part 1")),
            // 2025-08-02
            Event(data = EventData("Sleep", LocalDateTime.of(2025,8,2,0,0), LocalDateTime.of(2025,8,2,7,0), 6, TYPE.SLEEP, "Night sleep part 2")),
            Event(data = EventData("Work", LocalDateTime.of(2025,8,2,9,0), LocalDateTime.of(2025,8,2,17,0), 6, TYPE.WORK, "Office work")),
            // 2025-08-03
            Event(data = EventData("Exercise", LocalDateTime.of(2025,8,3,18,0), LocalDateTime.of(2025,8,3,19,0), 0, TYPE.EXERCISE, "Evening run")),
            // 2025-08-04
            Event(data = EventData("Sleep", LocalDateTime.of(2025,8,3,23,30), LocalDateTime.of(2025,8,3,23,59), 0, TYPE.SLEEP, "Night sleep part 1")),
            Event(data = EventData("Sleep", LocalDateTime.of(2025,8,4,0,0), LocalDateTime.of(2025,8,4,7,30), 1, TYPE.SLEEP, "Night sleep part 2")),
            Event(data = EventData("Work", LocalDateTime.of(2025,8,4,9,0), LocalDateTime.of(2025,8,4,17,0), 1, TYPE.WORK, "Office work")),
            // 2025-08-05
            Event(data = EventData("Meal", LocalDateTime.of(2025,8,5,12,0), LocalDateTime.of(2025,8,5,12,30), 2, TYPE.MEAL, "Lunch")),
            // 2025-08-06
            Event(data = EventData("Sleep", LocalDateTime.of(2025,8,5,23,0), LocalDateTime.of(2025,8,5,23,59), 2, TYPE.SLEEP, "Night sleep part 1")),
            Event(data = EventData("Sleep", LocalDateTime.of(2025,8,6,0,0), LocalDateTime.of(2025,8,6,7,0), 3, TYPE.SLEEP, "Night sleep part 2")),
            Event(data = EventData("Work", LocalDateTime.of(2025,8,6,9,0), LocalDateTime.of(2025,8,6,17,0), 3, TYPE.WORK, "Office work")),
            // 2025-08-07
            Event(data = EventData("Exercise", LocalDateTime.of(2025,8,7,18,0), LocalDateTime.of(2025,8,7,19,0), 4, TYPE.EXERCISE, "Yoga")),
            // 2025-08-08
            Event(data = EventData("Sleep", LocalDateTime.of(2025,8,7,23,30), LocalDateTime.of(2025,8,7,23,59), 4, TYPE.SLEEP, "Night sleep part 1")),
            Event(data = EventData("Sleep", LocalDateTime.of(2025,8,8,0,0), LocalDateTime.of(2025,8,8,7,30), 5, TYPE.SLEEP, "Night sleep part 2")),
            // 2025-08-09
            Event(data = EventData("Other", LocalDateTime.of(2025,8,9,15,0), LocalDateTime.of(2025,8,9,16,0), 6, TYPE.OTHER, "Doctor appointment")),
            // 2025-08-10
            Event(data = EventData("Leisure", LocalDateTime.of(2025,8,10,20,0), LocalDateTime.of(2025,8,10,22,0), 0, TYPE.LEISURE, "Board games"))
        )
        // 插入测试数据到数据库
        // 使用协程来执行数据库操作

        GlobalScope.launch {
            eventDao.insertEvents(august2025Events)
        }

    }
}