package com.example.schedulemanager.logic.dao

import androidx.core.content.edit
import com.example.schedulemanager.ScheduleManagerApplication


object PlanInfoDao {
    private fun sharedPreferences() =
        ScheduleManagerApplication.context
            .getSharedPreferences("schedule_manager", android.content.Context.MODE_PRIVATE)

    fun savePlanInfo(planName: String,type:Int, duration: Int){
        PlanInfoDao.sharedPreferences().edit {
            putString("planName", planName)
            putInt("planType", type)
            putInt("goalMinute", duration)

        }
    }

    fun getSavedPlanInfo():Map<String,Any?>{
        return mapOf(
            "planName" to sharedPreferences().getString("planName",""),
            "planType" to sharedPreferences().getInt("planType",0),
            "goalMinute" to sharedPreferences().getInt("duration",0)
        )
    }
    fun isPlanSaved(): Boolean{
        return PlanInfoDao.sharedPreferences().contains("planName") &&
                PlanInfoDao.sharedPreferences().contains("planType")&&
                PlanInfoDao.sharedPreferences().contains("planGoal")

    }

    fun clearPlanInfo(){
        PlanInfoDao.sharedPreferences().edit{
            remove("planName")
            remove("planType")
            remove("goalMinute")
        }
    }
}