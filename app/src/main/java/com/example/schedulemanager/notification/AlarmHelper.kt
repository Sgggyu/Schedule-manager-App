package com.example.schedulemanager.notification

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresPermission
import com.example.schedulemanager.logic.model.Plan
import com.example.schedulemanager.logic.model.TIMES
import java.util.Calendar
import androidx.core.net.toUri
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class AlarmHelper(private val context: Context) {

    private val alarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // 设置闹钟
    fun setAlarm(plan: Plan,type: String) {
        setRepeatingAlarm(plan,type)
    }


    fun setRepeatingAlarm(plan: Plan,type: String){
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("triggerMode",plan.triggerMode)
            //通知内容区
            putExtra("planId",plan.id)
            putExtra("title",plan.name)
            putExtra("content","${plan.triggerStartTime} - ${plan.triggerEndTime}")
            putExtra("duration", getDuration(plan))
            putExtra("planType",plan.type)
            putExtra("type",type) // 标识是开始闹钟
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            if(type == "start") plan.id*2 else plan.id*2+1, // 使用不同的请求码区分开始和结束闹钟
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerMillis = if(type == "start") parseTimeToMillis(plan.triggerStartTime) else parseTimeToMillis(plan.triggerEndTime)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12+
            if (!alarmManager.canScheduleExactAlarms()) {


            }
        }
        //只进行开始时间提醒，结束时间再
        when(type){
            "start" ->{
                //测试使用
                val triggerMillis = System.currentTimeMillis() + 10_000
                Log.v("test","闹钟设置成功，计划：${plan.name}，类型：$type, 时间：${if(type == "start") plan.triggerStartTime else plan.triggerEndTime}, 毫秒数：$triggerMillis")
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerMillis,
                    pendingIntent)
            }
            "end" ->{
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerMillis,
                    pendingIntent
                )
            }
            else -> {
                throw Exception("Plan type is error")
            }
        }
    }

    // 取消闹钟
    fun cancelAlarm(plan: Plan) {
        val startIntent = Intent(context, AlarmReceiver::class.java)
        val startPendingIntent = PendingIntent.getBroadcast(
            context,
            plan.id*2,
            startIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(startPendingIntent)
    }

    // 快速开关
    fun togglePlan(plan: Plan) {
        if (plan.isEnable) {
            setAlarm(plan,"start")
        } else {
            cancelAlarm(plan)
        }
    }

    fun parseTimeToMillis(time: String): Long {
        val parts = time.split(":")
        val hour = parts[0].toInt()
        val minute = parts[1].toInt()

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // 如果时间已经过去，就设置为明天
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }
        return calendar.timeInMillis
    }

    fun getDuration(plan: Plan): Int{
        var time = plan.triggerStartTime.split(":")
        val start = time[0].toInt() * 60 + time[1].toInt()
        time = plan.triggerEndTime.split(":")
        val end = time[0].toInt() * 60 + time[1].toInt()
        return (start - end)%1440
    }

}
