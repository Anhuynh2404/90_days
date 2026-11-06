package com.example.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.Project90Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDate

class NotificationActionReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_ADD_WATER = "com.example.ACTION_ADD_WATER"
        const val ACTION_ADD_STEPS = "com.example.ACTION_ADD_STEPS"
        const val ACTION_REPOST = "com.example.ACTION_REPOST"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val action = intent.action
        if (action == ACTION_REPOST) {
            PersistentHabitNotificationService.startOrUpdate(context)
            return
        }

        val app = context.applicationContext as? Project90Application ?: return
        val repo = app.repository
        val todayEpochDay = LocalDate.now().toEpochDay()

        CoroutineScope(Dispatchers.IO).launch {
            when (action) {
                ACTION_ADD_WATER -> {
                    repo.addWater(todayEpochDay, 250)
                    HabitNotificationHelper.postOrUpdateNotification(context)
                }
                ACTION_ADD_STEPS -> {
                    val logs = repo.getLogsForDate(todayEpochDay).firstOrNull() ?: emptyList()
                    val currentSteps = logs.find { it.habitId == "pedometer_10k" }?.progressValue ?: 0
                    repo.updateSteps(todayEpochDay, currentSteps + 500)
                    HabitNotificationHelper.postOrUpdateNotification(context)
                }
            }
        }
    }
}
