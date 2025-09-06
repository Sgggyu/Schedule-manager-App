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
import androidx.core.content.ContextCompat.startActivity
import com.example.schedulemanager.logic.model.Plan
import com.example.schedulemanager.logic.model.TIMES
import java.util.Calendar
import androidx.core.net.toUri
import com.example.schedulemanager.ActivityCollector
import com.example.schedulemanager.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class AlarmHelper(private val context: Context) {

    private val alarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                MaterialAlertDialogBuilder(context,R.style.CustomMaterialAlertDialog)
                    .setTitle("需要授权精确闹钟")
                    .setMessage("为了确保闹钟能够准时触发，请允许本应用使用精确闹钟权限。")
                    .setPositiveButton("前往设置") { _, _ ->
                        try {
                            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                                data = Uri.parse("package:${context.packageName}")
                            }
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // 兜底方案：跳转到应用详情设置页
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.parse("package:${context.packageName}")
                            }
                            context.startActivity(intent)
                        }
                    }
                    .setNegativeButton("取消"){_,_->
                        //若用户拒绝授权，则关闭所有活动页面，退出应用
                        ActivityCollector.finishAll()
                    }
                    .show()
            }
        }
    }
    // 设置闹钟
    fun setAlarm(plan: Plan,type: String) {
        setRepeatingAlarm(plan,type)
    }


    fun setRepeatingAlarm(plan: Plan,type: String, interval: Long = 0) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("triggerMode",plan.triggerMode)
            //通知内容区
            putExtra("planId",plan.id)
            putExtra("title",plan.name)
            putExtra("triggerStartTime",plan.triggerStartTime)
            putExtra("triggerEndTime",plan.triggerEndTime)
            putExtra("triggerMode",plan.triggerMode)
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

        var triggerMillis = parseTimeToMillis(plan.triggerStartTime)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager



        //只进行开始时间提醒，结束时间再
        when(type){
            "start" ->{
                //测试使用
                Log.v("test","闹钟设置成功，id：${plan.id*2}，触发时间：${plan.triggerStartTime}，触发模式：${plan.triggerMode}，计划类型：${plan.type}")
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerMillis + interval,
                    pendingIntent)
            }
            "end" ->{
                //作为结束闹钟，间隔时间为计划持续时间
                val triggerMillis = System.currentTimeMillis()
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerMillis + interval,
                    pendingIntent
                )
            }
            else -> {
                throw Exception("Plan type is error")
            }
        }
    }
    // 设置结束闹钟（更简单的调用方式
    fun setEndAlarm(id: Int, title: String, interval: Long = 0){
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("type","end") // 标识是结束闹钟
            putExtra("planId",id)
            putExtra("title",title)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id*2+1, // 使用不同的请求码区分开始和结束闹钟
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val triggerMillis = System.currentTimeMillis()
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerMillis + interval,
            pendingIntent
        )
    }
    // 取消闹钟
    fun cancelAlarm(plan: Plan) {
        Log.v("test","取消闹钟，id：${plan.id*2}")
        val intent = Intent(context, AlarmReceiver::class.java)
        val startPendingIntent = PendingIntent.getBroadcast(
            context,
            plan.id*2,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val endPendingIntent = PendingIntent.getBroadcast(
            context,
            plan.id*2 + 1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(startPendingIntent)
        alarmManager.cancel (endPendingIntent)
    }
    fun cancelStartAlarm(){

    }
    fun cancelEndAlarm(id: Int){
        val intent = Intent(context, AlarmReceiver::class.java)
        val endPendingIntent = PendingIntent.getBroadcast(
            context,
            id*2 + 1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel (endPendingIntent)
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
        return if (end>=start) end - start else 1440-start+end
    }

}
