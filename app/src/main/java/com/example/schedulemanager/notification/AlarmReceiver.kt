package com.example.schedulemanager.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.schedulemanager.ScheduleManagerApplication
import com.example.schedulemanager.logic.model.Plan
import com.example.schedulemanager.logic.model.TIMES
import com.example.schedulemanager.notification.NotificationHelper
import java.util.Calendar


class AlarmReceiver: BroadcastReceiver() {
    val alarmHelper = AlarmHelper(context = ScheduleManagerApplication.context)
    override fun onReceive(context: Context?, intent: Intent?) {

        Log.v("test","闹钟响了")
        val startOrEnd = intent?.getStringExtra("type")?:""
        val triggerMode = intent?.getIntExtra("triggerMode",0)
        val planId = intent?.getIntExtra("planId",0)?:0
        val title = intent?.getStringExtra("title")?:""
        val triggerStartTime = intent?.getStringExtra("triggerStartTime")?:""
        val triggerEndTime = intent?.getStringExtra("triggerEndTime")?:""
        val content = "计划时间：$triggerStartTime - $triggerEndTime"
        val planType = intent?.getIntExtra("planType",-1)?: -1
        val duration = intent?.getIntExtra("duration",25)?: -1
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val plan = Plan(
            id = planId,
            name = title,
            type = planType,
            triggerMode = triggerMode ?: 0,
            triggerStartTime = triggerStartTime,
            triggerEndTime = triggerEndTime,
            isEnable = true
        )

        when(triggerMode){
            TIMES.EVERYDAY ->{
                // 每天都触发
                if (context != null) {
                    if (startOrEnd == "start") {
                        NotificationHelper(context).planStartNotification(
                            planId,
                            title,
                            content,
                            planType,
                            duration,
                            TIMES.EVERYDAY
                        )
                        alarmHelper.setRepeatingAlarm(
                            plan , "start", interval = 0
                        )
                    }else{
                        NotificationHelper(context).planEndNotification(
                            planId,
                            title,
                            planType
                        )
                    }

                }
            }

            TIMES.WEEKDAY -> {
                if (context != null) {
                    if (startOrEnd == "start") {
                        // 仅在工作日发送通知
                        if (dayOfWeek in Calendar.MONDAY..Calendar.FRIDAY) {
                            NotificationHelper(context).planStartNotification(
                                planId,
                                title,
                                content,
                                planType,
                                duration,
                                TIMES.WEEKDAY
                            )
                        }
                        alarmHelper.setRepeatingAlarm(
                            plan, "start", interval = 0
                        )
                    } else {
                        NotificationHelper(context).planEndNotification(
                            planId,
                            title,
                            planType
                        )
                    }
                }


            }
        }
    }
}