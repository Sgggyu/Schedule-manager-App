package com.example.schedulemanager.ui.event

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.example.schedulemanager.logic.Repository
import com.example.schedulemanager.logic.model.Event
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

class EventViewModel: ViewModel() {
    var currentTime = MutableLiveData<LocalDateTime>()
    val dayOfMonth get() = currentTime.value?.dayOfMonth ?: LocalDateTime.now().dayOfMonth
    val startTime get() = currentTime.value?.with(LocalTime.MIN) ?: LocalDateTime.now().with(LocalTime.MIN)
    val endTime get() = currentTime.value?.with(LocalTime.MAX) ?: LocalDateTime.now().with(LocalTime.MAX)

    val monthEvents = ArrayList<List<Event>>().apply {
        repeat(32) {
            add(emptyList())
        }
    } // 用于存储每一天的事件列表，索引0空出，索引1表示1号，索引31表示31号
    // 计算本周的开始和结束时间
    val startToEnd: LiveData<Pair<LocalDateTime, LocalDateTime>> = currentTime.switchMap { now ->
        liveData {
            val dayOfMonth = now.dayOfMonth // 1=Monday, 7=Sunday
            val startOfMonth = now.minusDays((dayOfMonth - 1).toLong()).with(LocalTime.MIN)
            val endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1)
            emit(Pair(startOfMonth, endOfMonth))
        }
    }

    val Events = startToEnd.switchMap { startToEnd ->
        val result = Repository.getEventsBetween(
            startToEnd.first.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            startToEnd.second.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
        result
    }

    fun refreshEvents(currentTime: LocalDateTime) {
        this.currentTime.value = currentTime
    }
}