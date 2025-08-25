package com.example.schedulemanager.logic.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.time.LocalTime

@Entity
data class Plan(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val type: Int,
    val triggerMode: Int,
    val triggerTime: String,
    val isEnable: Boolean
)
