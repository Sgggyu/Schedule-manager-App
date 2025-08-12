package com.example.schedulemanager.logic.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime


@Entity
data class Event(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @Embedded
    val data: EventData
) {
    //方便后期转化为Plan
    fun toPlan(): Plan {
        return Plan(
            id = this.id,
            data = this.data
        )
    }
}