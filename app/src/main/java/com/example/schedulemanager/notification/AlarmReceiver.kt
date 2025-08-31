package com.example.schedulemanager.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.schedulemanager.logic.model.TIMES
import com.example.schedulemanager.notification.NotificationHelper
import java.util.Calendar


class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.v("test","闹钟响了")
        val startOrEnd = intent?.getStringExtra("type")?:""
        val triggerMode = intent?.getIntExtra("triggerMode",0)
        val planId = intent?.getIntExtra("planId",0)
        val title = intent?.getStringExtra("title")?:""
        val content = intent?.getStringExtra("content")?:""
        val planType = intent?.getIntExtra("planType",-1)?: -1
        val duration = intent?.getIntExtra("duration",25)?: -1
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        when(triggerMode){
            TIMES.EVERYDAY ->{
                // 每天都触发
                if (context != null) {
                    if (startOrEnd == "start"){
                        NotificationHelper(context).planStartNotification(
                            title,
                            content,
                            planType,
                            duration,
                            TIMES.EVERYDAY
                        )
                    } else if (startOrEnd == "end") {
                        NotificationHelper(context).planEndNotification(title,planType)
                    }
                }
            }

            TIMES.WEEKDAY -> {
                // 仅在工作日触发（周一到周五）
                if (dayOfWeek in Calendar.MONDAY..Calendar.FRIDAY) {
                    if (context != null) {
                        if (startOrEnd == "start"){
                            NotificationHelper(context).planStartNotification(
                                title,
                                content,
                                planType,
                                duration,
                                TIMES.EVERYDAY
                            )
                        } else if (startOrEnd == "end") {
                            NotificationHelper(context).planEndNotification(title,planType)
                        }
                    }
                }
            }
        }
    }
}