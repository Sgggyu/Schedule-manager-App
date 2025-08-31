package com.example.schedulemanager.logic

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.schedulemanager.ScheduleManagerApplication
import com.example.schedulemanager.logic.dao.EventInfoDao
import com.example.schedulemanager.logic.dao.PlanInfoDao
import com.example.schedulemanager.logic.model.Event
import com.example.schedulemanager.logic.model.EventData
import com.example.schedulemanager.logic.model.Plan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.time.Duration

object Repository {
    val tag = "Repository"
    val eventDao = ScheduleManagerApplication.eventDao
    val planDao = ScheduleManagerApplication.planDao

    // -------------------
    // Event 部分
    // -------------------

    fun getEventsBetween(startMillis: Long, endMillis: Long) = liveData(Dispatchers.IO) {
        try {
            val events : List<Event> = eventDao.refreshEventsBetween(startMillis, endMillis)
            val result = ArrayList<List<Event>>().apply {
                repeat(32) {
                    add(emptyList())
                }
            }
            val dayEvents = events.groupBy { it.data.beginTime.dayOfMonth }
            for (i in 0 until 31) {
                result[i+1] = dayEvents[i + 1] ?: emptyList()
            }

            emit(Result.success(result))
        } catch (e: Exception) {
            emit(Result.failure<ArrayList<List<Event>>>(e))
        }
    }


    suspend fun insertEvent(event: Event): List<Long> =
        withContext(Dispatchers.IO) {
            //假如event开始和结束时间在两天，我们就分割成两个event存入，这样就可以每天管每天的任务记录
            val eventList = ArrayList<Event>()
            val beginDay = event.data.beginTime.toLocalDate()
            val endDay = event.data.endTime.toLocalDate()

            if (beginDay == endDay){
                eventList.add(event)
            }else{
                var data = event.data.copy(beginTime = event.data.beginTime,
                    endTime = beginDay.atTime(LocalTime.MAX),
                    day = beginDay.dayOfWeek.value)
                eventList.add(event.copy(data = data))
                data = event.data.copy(beginTime = endDay.atTime(LocalTime.MIN),
                    endTime = event.data.endTime,
                    day = endDay.dayOfWeek.value)
                eventList.add(event.copy(data = data))
            }
            eventDao.insertEvents(eventList)
        }

    suspend fun updateEventDescription(eventId: Int, newDescription: String) =
        withContext(Dispatchers.IO) {
            eventDao.updateDescriptionById(eventId, newDescription)
        }

    suspend fun clearAllEvents(){
        withContext(Dispatchers.IO) {
            eventDao.clearAllEvents()
        }
    }


    // -------------------
    // Plan 部分
    // -------------------
    suspend fun insertPlan(plan: Plan) =
        withContext(Dispatchers.IO) {
            planDao.insertPlan(plan)
        }

    suspend fun insertPlans(plans: List<Plan>) =
        withContext(Dispatchers.IO) {
            planDao.insertPlans(plans)
        }
    suspend fun updatePlan(plan: Plan) =
        withContext(Dispatchers.IO) {
            planDao.updatePlan(plan)
        }

    suspend fun getAllPlans(): List<Plan> {
        return planDao.getAllPlans()
    }



    suspend fun deletePlan(id: Int){
        withContext(Dispatchers.IO) {
            planDao.deletePlan(id)
        }
    }
    suspend fun deletePlans(ids:List<Int>){
        withContext(Dispatchers.IO) {
            planDao.deletePlans(ids)
        }
    }
    suspend fun clearAllPlans() =
        withContext(Dispatchers.IO) {
            planDao.clearAllPlans()
        }

    //SharedPreferences部分
    fun saveEventInfo(
        eventName: String,
        eventType: String,
        typeId: Int,
        startTime: Long,
        eventGoal: String
    )  = EventInfoDao.saveEventInfo(eventName, startTime, eventType,typeId, eventGoal)
    fun getSavedEventInfo() = EventInfoDao.getSavedEventInfo()
    fun isEventInfoSaved() = EventInfoDao.isEventInfoSaved()
    fun clearEventInfo() = EventInfoDao.clearEventInfo()
    fun saveDescription(description: String) = EventInfoDao.saveDescription(description)
    fun getEventDescription(): String? = EventInfoDao.getEventDescription()
    fun savePlanInfo(
        planName: String,
        type: Int,
        duration: Int
    ) = PlanInfoDao.savePlanInfo(planName, type, duration)
    fun getSavedPlanInfo() = PlanInfoDao.getSavedPlanInfo()
    fun isPlanSaved() = PlanInfoDao.isPlanSaved()
    fun clearPlanInfo() = PlanInfoDao.clearPlanInfo()
}