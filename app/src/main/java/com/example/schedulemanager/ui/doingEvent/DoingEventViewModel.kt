package com.example.schedulemanager.ui.doingEvent

import androidx.lifecycle.ViewModel
import java.time.LocalDateTime

class DoingEventViewModel : ViewModel() {
    var eventName  = ""
    var startTime: LocalDateTime? = null
    var eventType = ""
    var typeId = -1
    var eventGoal = ""
    var description = ""
}