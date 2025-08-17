package com.example.schedulemanager.logic.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.schedulemanager.logic.model.Event

@Dao
interface EventDao {
    @Query("SELECT * FROM Event WHERE beginTime >= :startMillis AND endTime <= :endMillis ORDER BY beginTime ASC")
    suspend fun refreshEventsBetween(startMillis: Long, endMillis: Long): List<Event>
    @Insert
    suspend fun insertEvent(event: Event): Long
    @Insert
    suspend fun insertEvents(events: List<Event>): List<Long>

    @Query("UPDATE Event SET description = :newDescription WHERE id = :eventId")
    suspend fun updateDescriptionById(eventId: Int, newDescription: String)

    @Query("DELETE FROM Event" )
    suspend fun clearAllEvents()
}