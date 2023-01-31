package com.nexusinfinity.electronicsportal.notification.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.nexusinfinity.electronicsportal.LoginActivity
import com.nexusinfinity.electronicsportal.R
import com.nexusinfinity.electronicsportal.constants.Constant

class FirebaseService: FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//        val intent = Intent(this, LoginActivity::class.java)
//        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(manager)
        }

//        val pendingIntent = PendingIntent.getActivities(this, 1, arrayOf(intent), PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(this, Constant.CHANNEL_ID)
            .setContentTitle(message.data["title"])
            .setContentText(message.data["message"])
            .setSmallIcon(R.drawable.shop)
//            .setContentIntent(pendingIntent)
            .build()

        manager.notify(Constant.NOTIFICATION_ID, notification)
        
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(manager: NotificationManager) {
        val channel = NotificationChannel(Constant.CHANNEL_ID, "impChannel", IMPORTANCE_HIGH)
        channel.description = "Notification of Importance"
        manager.createNotificationChannel(channel)
    }
}