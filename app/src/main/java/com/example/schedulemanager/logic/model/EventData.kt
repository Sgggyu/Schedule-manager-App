package com.example.schedulemanager.logic.model

import java.time.LocalDateTime

/*
    * EventData.kt
    * 给Event和Plan提供一个基类
    * 方便后期转化
 */
data class EventData(
    val name: String,

    // 使用 LocalDateTime 存储，但在数据库中是 Long（时间戳）
    val beginTime: LocalDateTime,
    val endTime: LocalDateTime,

    // 星期几，0=周日, 1=周一 ... 6=周六
    val day: Int,

    // 事件类型（TYPE.SLEEP, TYPE.WORK, TYPE.EXERCISE）
    val type: Int,

    var description: String
)
