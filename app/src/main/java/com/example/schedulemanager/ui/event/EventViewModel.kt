package com.example.schedulemanager.ui.event

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.example.schedulemanager.logic.Repository
import java.time.LocalDateTime
import java.time.ZoneOffset

class EventViewModel: ViewModel() {
    private val startToEnd = MutableLiveData<Pair<LocalDateTime, LocalDateTime>>()

    val eventsLiveData = startToEnd.switchMap {
        val (start, end) = it
        Repository.getEventsBetween(start.toInstant(ZoneOffset.UTC).toEpochMilli(), end.toInstant(ZoneOffset.UTC).toEpochMilli())
    }

    fun refreshEvents(start: LocalDateTime, end: LocalDateTime) {
        startToEnd.value = Pair(start, end)
    }
}