package com.example.schedulemanager.logic.dao

import androidx.compose.ui.graphics.vector.Path
import androidx.core.content.edit
import com.example.schedulemanager.ScheduleManagerApplication


object PlanInfoDao {
    private fun sharedPreferences() =
        ScheduleManagerApplication.context
            .getSharedPreferences("schedule_manager", android.content.Context.MODE_PRIVATE)

    fun savePlanInfo(planId:Int,planName: String,type:Int, duration: Int){
        PlanInfoDao.sharedPreferences().edit {
            putInt("planId",planId)
            putString("planName", planName)
            putInt("planType", type)
            putInt("duration", duration)

        }
    }

    fun getSavedPlanInfo():Map<String,Any?>{
        return mapOf(
            "planId" to sharedPreferences().getInt("planId",0),
            "planName" to sharedPreferences().getString("planName",""),
            "planType" to sharedPreferences().getInt("planType",0),
            "goalMinute" to sharedPreferences().getInt("duration",0)
        )
    }
    fun isPlanSaved(): Boolean{
        return PlanInfoDao.sharedPreferences().contains("planId") &&
                PlanInfoDao.sharedPreferences().contains("planName") &&
                PlanInfoDao.sharedPreferences().contains("planType")&&
                PlanInfoDao.sharedPreferences().contains("duration")

    }

    fun clearPlanInfo(){
        PlanInfoDao.sharedPreferences().edit{
            remove("planId")
            remove("planName")
            remove("planType")
            remove("goalMinute")
        }
    }
}