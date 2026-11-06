package com.example.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.ServiceCompat
import com.example.Project90Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate

class PersistentHabitNotificationService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        fun startOrUpdate(context: Context) {
            // First, immediately post the notification via NotificationManagerCompat so it displays instantly
            HabitNotificationHelper.postOrUpdateNotification(context)

            // Next, start the foreground service to ensure background persistence
            val intent = Intent(context, PersistentHabitNotificationService::class.java)
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            } catch (_: Exception) {}
        }
    }

    override fun onCreate() {
        super.onCreate()
        HabitNotificationHelper.createNotificationChannel(this)

        val initialNotif = HabitNotificationHelper.buildDashboardNotification(
            context = this,
            dayNumber = 1,
            totalDays = 90,
            daysRemainingToFeb = 80,
            currentStreak = 7,
            completedCount = 0,
            totalCount = 13,
            stepCount = 0,
            waterMl = 0,
            pushupDone = false,
            squatDone = false,
            noSugarDone = false,
            pendingHabitsText = "Đang tải bảng theo dõi thói quen...",
            isCompletedAll = false
        )

        // Android 14+ requires foregroundServiceType matching manifest
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val fgsType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            } else {
                0
            }
            ServiceCompat.startForeground(this, HabitNotificationHelper.NOTIFICATION_ID, initialNotif, fgsType)
        } else {
            startForeground(HabitNotificationHelper.NOTIFICATION_ID, initialNotif)
        }

        // Post fresh content right away
        HabitNotificationHelper.postOrUpdateNotification(this)

        // Real-time observer on repository
        serviceScope.launch {
            val app = applicationContext as? Project90Application ?: return@launch
            val repo = app.repository
            val todayEpochDay = LocalDate.now().toEpochDay()

            combine(
                repo.userFlow,
                repo.allHabitsFlow,
                repo.getLogsForDate(todayEpochDay)
            ) { _, _, _ ->
                HabitNotificationHelper.postOrUpdateNotification(this@PersistentHabitNotificationService)
            }.collect {}
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        HabitNotificationHelper.postOrUpdateNotification(this)
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        // Ensure notification is still alive even if service is destroyed
        HabitNotificationHelper.postOrUpdateNotification(this)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        // User swiped app away from recents: ensure notification stays in status bar!
        HabitNotificationHelper.postOrUpdateNotification(this)
    }
}
