package com.example.schedulemanager.logic.dao

import androidx.core.content.edit
import com.example.schedulemanager.ScheduleManagerApplication

object EventInfoDao {
    private fun sharedPreferences() =
        ScheduleManagerApplication.context
            .getSharedPreferences("schedule_manager", android.content.Context.MODE_PRIVATE)
    fun saveEventInfo(eventName: String, startTime: Long, eventType: String,typeId:Int, eventGoal: String) {
        sharedPreferences().edit {
            putString("eventName", eventName)
            putLong("startTime", startTime)
            putInt("typeId", typeId)
            putString("eventType", eventType)
            putString("eventGoal", eventGoal)
        }
    }
    fun saveDescription(description: String) {
        sharedPreferences().edit {
            putString("eventDescription", description)
        }
    }

    fun getEventDescription(): String? {
        return sharedPreferences().getString("eventDescription", "")
    }
    fun getSavedEventInfo(): Map<String, Any?> {
        return mapOf(
            "eventName" to sharedPreferences().getString("eventName", ""),
            "startTime" to sharedPreferences().getLong("startTime", 0L),
            "typeId" to sharedPreferences().getInt("typeId", 0),
            "eventType" to sharedPreferences().getString("eventType", ""),
            "eventGoal" to sharedPreferences().getString("eventGoal", ""),
        )
    }
    fun isEventInfoSaved(): Boolean {
        return sharedPreferences().contains("eventName") &&
                sharedPreferences().contains("startTime") &&
                sharedPreferences().contains("typeId") &&
                sharedPreferences().contains("eventType") &&
                sharedPreferences().contains("eventGoal")
    }
    fun clearEventInfo() {
        sharedPreferences().edit {
            remove("eventName")
            remove("startTime")
            remove("typeId")
            remove("eventType")
            remove("eventGoal")
            remove("eventDescription")
        }
    }
}