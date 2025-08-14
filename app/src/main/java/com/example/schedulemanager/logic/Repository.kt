package com.example.schedulemanager.logic

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.schedulemanager.ScheduleManagerApplication
import com.example.schedulemanager.logic.model.Event
import com.example.schedulemanager.logic.model.Plan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

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


    suspend fun insertEvent(event: Event): Long =
        withContext(Dispatchers.IO) {
            eventDao.insertEvent(event)
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

    suspend fun insertPlans(plans: List<Plan>) =
        withContext(Dispatchers.IO) {
            planDao.insertPlans(plans)
        }

    fun getAllPlans() = liveData(Dispatchers.IO) {
        try {
            val plans = planDao.getAllPlans()
            emit(Result.success(plans))
        } catch (e: Exception) {
            emit(Result.failure<List<Plan>>(e))
        }
    }

    suspend fun clearAllPlans() =
        withContext(Dispatchers.IO) {
            planDao.clearAllPlans()
        }
}