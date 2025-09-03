package com.example.schedulemanager.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.schedulemanager.MainActivity

import com.example.schedulemanager.ScheduleManagerApplication
import com.example.schedulemanager.logic.model.getRender

class NotificationHelper(val context: Context)  {
    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val channel1 = NotificationChannel("forPlan","规划通知", NotificationManager.IMPORTANCE_HIGH)
    init {
        //这里添加后续的channel
        manager.createNotificationChannel(channel1)
    }



    fun planStartNotification(planId: Int,title: String, content: String, planType: Int, duration: Int, type: Int){
        //计划相关通知
        val intent = Intent(context, MainActivity::class.java).apply{
            putExtra("planId",planId)
            putExtra("planName",title)
            putExtra("planType",planType)
            putExtra("type","start")
            putExtra("duration",duration)
            //启动标识
            putExtra("intentType","fromPlanNotification" )
        }
        val pi = PendingIntent.getActivity(context,-1,intent,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val notification = Notification.Builder(context,"forPlan")
            .setContentTitle(title)
            .setStyle(Notification.BigTextStyle())
            .setContentText(content)
            .setSmallIcon(getRender(type).icon)
            .setAutoCancel(true)
            .setContentIntent(pi)
            .build()
        manager.notify(planId*2 ,notification)
    }

    fun planEndNotification(planId: Int, title: String, type: Int){
        //计划相关通知
        val intent = Intent(context, MainActivity::class.java)
        val pi = PendingIntent.getActivity(context,-1,intent,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val content = "${title} 已结束,快来写总结吧！"
        val notification = Notification.Builder(context,"forPlan")
            .setContentTitle(title)
            .setStyle(Notification.BigTextStyle())
            .setContentText(content)
            .setSmallIcon(getRender(type).icon)
            .setAutoCancel(true)
            .setContentIntent(pi)
            .build()
        manager.notify( planId*2+1,notification)
    }

}