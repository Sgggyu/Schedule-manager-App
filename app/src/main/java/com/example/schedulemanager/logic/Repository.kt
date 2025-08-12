package com.example.schedulemanager.logic

import com.example.schedulemanager.ScheduleManagerApplication
import com.example.schedulemanager.logic.model.Event
import com.example.schedulemanager.logic.model.Plan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object Repository {
    val eventDao = ScheduleManagerApplication.eventDao
    val planDao = ScheduleManagerApplication.planDao

    // -------------------
    // Event 部分
    // -------------------

    suspend fun getEventsBetween(startMillis: Long, endMillis: Long): List<Event> =
        withContext(Dispatchers.IO) {
            eventDao.refreshEventsBetween(startMillis, endMillis)
        }

    suspend fun insertEvent(event: Event): Long =
        withContext(Dispatchers.IO) {
            eventDao.insertEvent(event)
        }

    suspend fun updateEventDescription(eventId: Int, newDescription: String) =
        withContext(Dispatchers.IO) {
            eventDao.updateDescriptionById(eventId, newDescription)
        }

    // -------------------
    // Plan 部分
    // -------------------

    suspend fun insertPlans(plans: List<Plan>) =
        withContext(Dispatchers.IO) {
            planDao.insertPlans(plans)
        }

    suspend fun getAllPlans(): List<Plan> =
        withContext(Dispatchers.IO) {
            planDao.getAllPlans()
        }

    suspend fun clearAllPlans() =
        withContext(Dispatchers.IO) {
            planDao.clearAllPlans()
        }
}