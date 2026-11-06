package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.Project90Application
import com.example.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.LocalDate
import java.util.Locale

object HabitNotificationHelper {

    const val CHANNEL_ID = "project90_persistent_channel"
    const val CHANNEL_NAME = "Project 90 Ongoing Habit Checklist"
    const val NOTIFICATION_ID = 9001

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Bảng Dashboard theo dõi thói quen chuyên nghiệp ghim 24/7 trên thanh thông báo"
                setShowBadge(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            manager?.createNotificationChannel(channel)
        }
    }

    fun buildDashboardNotification(
        context: Context,
        dayNumber: Int,
        totalDays: Int,
        daysRemainingToFeb: Long,
        currentStreak: Int,
        completedCount: Int,
        totalCount: Int,
        stepCount: Int,
        waterMl: Int,
        pushupDone: Boolean,
        squatDone: Boolean,
        noSugarDone: Boolean,
        pendingHabitsText: String,
        isCompletedAll: Boolean
    ): Notification {
        createNotificationChannel(context)

        val percent = if (totalCount > 0) ((completedCount.toFloat() / totalCount.toFloat()) * 100).toInt() else 0
        val numberFormat = NumberFormat.getInstance(Locale("vi", "VN"))

        // Open App PendingIntent
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Quick Action: Add Water (+250ml)
        val addWaterIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = NotificationActionReceiver.ACTION_ADD_WATER
        }
        val addWaterPendingIntent = PendingIntent.getBroadcast(
            context,
            101,
            addWaterIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Quick Action: Add Steps (+500 steps)
        val addStepsIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = NotificationActionReceiver.ACTION_ADD_STEPS
        }
        val addStepsPendingIntent = PendingIntent.getBroadcast(
            context,
            102,
            addStepsIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 1. Collapsed RemoteViews
        val collapsedViews = RemoteViews(context.packageName, R.layout.notification_dashboard_collapsed).apply {
            setTextViewText(R.id.notif_collapsed_title, "PROJECT 90 • NGÀY $dayNumber/$totalDays")
            setTextViewText(R.id.notif_collapsed_streak, "🔥 ${currentStreak}N")
            setProgressBar(R.id.notif_collapsed_progressbar, 100, percent, false)
            setTextViewText(R.id.notif_collapsed_progress_text, "$completedCount/$totalCount Đạt ($percent%)")
            if (isCompletedAll) {
                setTextViewText(R.id.notif_collapsed_status, "🔥 100% Hoàn thành! Chuỗi kỷ luật tiếp tục vững vàng!")
            } else {
                val pendingCount = totalCount - completedCount
                setTextViewText(R.id.notif_collapsed_status, "⚠️ Còn $pendingCount mục chưa xong hôm nay (Kéo xuống để xem)")
            }
        }

        // 2. Expanded RemoteViews (Command Center Dashboard)
        val expandedViews = RemoteViews(context.packageName, R.layout.notification_dashboard_expanded).apply {
            setTextViewText(R.id.notif_expanded_day, "PROJECT 90 • NGÀY $dayNumber / $totalDays")
            setTextViewText(R.id.notif_expanded_countdown, "Còn $daysRemainingToFeb ngày đếm ngược đến Tháng 2")
            setTextViewText(R.id.notif_expanded_streak, "🔥 $currentStreak Ngày Chuỗi")

            setTextViewText(R.id.notif_expanded_progress_label, "$completedCount / $totalCount Mục ($percent%)")
            setProgressBar(R.id.notif_expanded_progressbar, 100, percent, false)

            // 4 Mini Dashboard Tiles
            setTextViewText(R.id.notif_stat_steps, "${numberFormat.format(stepCount)} / 10k")
            setTextViewText(R.id.notif_stat_water, "${numberFormat.format(waterMl)} / 2.000ml")
            setTextViewText(
                R.id.notif_stat_workout,
                if (pushupDone && squatDone) "50/50 • 100/100" else "50 Hít • 100 Squat"
            )
            setTextViewText(
                R.id.notif_stat_sugar,
                if (noSugarDone) "ĐÃ ĐẠT ✅" else "CHƯA ĐẠT ⚠️"
            )

            // Summary Callout Box
            if (isCompletedAll) {
                setTextViewText(R.id.notif_expanded_summary_title, "🔥 XUẤT SẮC! HOÀN THÀNH 100% TOÀN BỘ MỤC TIÊU HÔM NAY")
                setTextViewText(R.id.notif_expanded_summary_body, "Kỷ luật thép đã được giữ vững! Chuỗi $currentStreak ngày liên tiếp không một ngày lơ là.")
            } else {
                val pendingCount = totalCount - completedCount
                setTextViewText(R.id.notif_expanded_summary_title, "⚠️ CÒN $pendingCount MỤC CHƯA HOÀN THÀNH HÔM NAY:")
                setTextViewText(R.id.notif_expanded_summary_body, pendingHabitsText)
            }

            // Click Handlers for Interactive Buttons
            setOnClickPendingIntent(R.id.notif_btn_open_app, openAppPendingIntent)
            setOnClickPendingIntent(R.id.notif_btn_add_water, addWaterPendingIntent)
            setOnClickPendingIntent(R.id.notif_btn_add_steps, addStepsPendingIntent)
        }

        val fallbackTitle = if (isCompletedAll) {
            "🔥 Project 90: Đã hoàn tất 100% mục tiêu hôm nay ($completedCount/$totalCount)"
        } else {
            "⚠️ Project 90: Còn ${totalCount - completedCount} mục chưa hoàn thành!"
        }

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_flame)
            .setContentTitle(fallbackTitle)
            .setContentText(if (isCompletedAll) "Chúc mừng chuỗi $currentStreak ngày kỷ luật!" else "Chạm để mở Dashboard")
            .setCustomContentView(collapsedViews)
            .setCustomBigContentView(expandedViews)
            .setContentIntent(openAppPendingIntent)
            .setOngoing(true) // CANNOT BE SWIPED AWAY
            .setAutoCancel(false)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setOnlyAlertOnce(true)
            .build()
    }

    fun postOrUpdateNotification(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val app = context.applicationContext as? Project90Application ?: return@launch
                val repo = app.repository
                val todayEpochDay = LocalDate.now().toEpochDay()

                val user = repo.userFlow.firstOrNull()
                val habits = repo.allHabitsFlow.firstOrNull() ?: emptyList()
                val logs = repo.getLogsForDate(todayEpochDay).firstOrNull() ?: emptyList()

                val dailyHabits = habits.filter { !it.isWeekly }
                val logsMap = logs.associateBy { it.habitId }

                val pendingHabits = dailyHabits.filter { habit ->
                    val log = logsMap[habit.id]
                    log?.isCompleted != true
                }

                val startDay = user?.startDateEpochDay ?: (todayEpochDay - 10)
                val targetDay = user?.targetDateEpochDay ?: (todayEpochDay + 80)
                val dayNumber = ((todayEpochDay - startDay) + 1).toInt().coerceIn(1, 90)
                val daysRemaining = (targetDay - todayEpochDay).coerceAtLeast(0)

                val completedCount = dailyHabits.size - pendingHabits.size
                val currentStreak = user?.currentStreak ?: 7
                val isCompletedAll = pendingHabits.isEmpty() && dailyHabits.isNotEmpty()

                // Key metrics
                val stepLog = logsMap["pedometer_10k"]
                val stepCount = stepLog?.progressValue ?: 0

                val waterLog = logsMap["water_2l"]
                val waterMl = waterLog?.progressValue ?: 0

                val pushupDone = logsMap["pushups_50"]?.isCompleted == true
                val squatDone = logsMap["squats_100"]?.isCompleted == true
                val noSugarDone = logsMap["no_sugar"]?.isCompleted == true

                val pendingHabitsText = if (pendingHabits.isNotEmpty()) {
                    val items = pendingHabits.take(5).joinToString("\n") { habit ->
                        val log = logsMap[habit.id]
                        val progress = log?.progressValue ?: 0
                        if (habit.targetValue > 1) {
                            "• ${habit.title}: Đã ${progress}/${habit.targetValue} ${habit.unit}"
                        } else {
                            "• ${habit.title}"
                        }
                    }
                    val more = if (pendingHabits.size > 5) "\n(+${pendingHabits.size - 5} mục khác)" else ""
                    "$items$more\n👉 Bấm nút bên dưới để cập nhật nhanh hoặc mở app!"
                } else {
                    "Toàn bộ mục tiêu trong ngày đã hoàn tất xuất sắc."
                }

                val notification = buildDashboardNotification(
                    context = context,
                    dayNumber = dayNumber,
                    totalDays = 90,
                    daysRemainingToFeb = daysRemaining,
                    currentStreak = currentStreak,
                    completedCount = completedCount,
                    totalCount = dailyHabits.size,
                    stepCount = stepCount,
                    waterMl = waterMl,
                    pushupDone = pushupDone,
                    squatDone = squatDone,
                    noSugarDone = noSugarDone,
                    pendingHabitsText = pendingHabitsText,
                    isCompletedAll = isCompletedAll
                )

                val manager = NotificationManagerCompat.from(context)
                manager.notify(NOTIFICATION_ID, notification)
            } catch (_: Exception) {}
        }
    }
}
