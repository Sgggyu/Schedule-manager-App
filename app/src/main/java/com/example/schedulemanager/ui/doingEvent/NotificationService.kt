package com.example.schedulemanager.ui.doingEvent

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.schedulemanager.MainActivity
import com.example.schedulemanager.R
import com.example.schedulemanager.logic.Repository

class NotificationService : Service() {
    lateinit var manager :NotificationManager
    val chanel1 = NotificationChannel("front_service","FRONT_SERVICE", NotificationManager.IMPORTANCE_HIGH)
    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")

    }

    override fun onCreate() {
        Log.v("test","create")
        super.onCreate()
        manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(chanel1)
        val event = Repository.getSavedEventInfo()
        val intent = Intent(this, MainActivity::class.java)
        val pi = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_IMMUTABLE)
        val notification = Notification.Builder(this,"front_service")
            .setContentTitle("${event["eventName"]}")
            .setStyle(Notification.BigTextStyle())
            .setContentText("类型：${event["eventType"]}|目标：${event["eventGoal"]}\n" +
                    "${Repository.getEventDescription()}")
            .setSmallIcon(com.example.schedulemanager.R.drawable.ic_launcher_foreground)
            .setContentIntent(pi)
            .setAutoCancel(true)
            .setOngoing(true)
            .build()
        startForeground(1,notification)
    }

}