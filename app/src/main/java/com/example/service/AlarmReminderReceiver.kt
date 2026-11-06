package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R

class AlarmReminderReceiver : BroadcastReceiver() {

    companion object {
        const val LOUD_ALARM_CHANNEL_ID = "project90_loud_alarm_channel"
        const val LOUD_ALARM_CHANNEL_NAME = "Project 90 Hardcore Alarm Alerts"

        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_MESSAGE = "extra_message"
        const val EXTRA_IS_5AM = "extra_is_5am"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val title = intent.getStringExtra(EXTRA_TITLE) ?: "⚠️ CẢNH BÁO KỶ LUẬT PROJECT 90!"
        val message = intent.getStringExtra(EXTRA_MESSAGE) ?: "Đã đến giờ thực hiện cam kết hôm nay!"
        val is5Am = intent.getBooleanExtra(EXTRA_IS_5AM, false)

        createLoudAlarmChannel(context)

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            System.currentTimeMillis().toInt(),
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, LOUD_ALARM_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 800, 300, 800, 300, 1200))
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notifId = if (is5Am) 5001 else (System.currentTimeMillis() % 10000).toInt()
        notificationManager.notify(notifId, notification)

        // Trigger alarm sound and vibration
        try {
            val soundManager = AlarmSoundManager(context)
            soundManager.triggerHardcoreAlarm()
        } catch (_: Exception) {}
    }

    private fun createLoudAlarmChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                LOUD_ALARM_CHANNEL_ID,
                LOUD_ALARM_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Kênh chuông cảnh báo khẩn cấp và báo thức hardcore 5:00 AM"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 800, 300, 800, 300, 1200)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                setBypassDnd(true)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}
