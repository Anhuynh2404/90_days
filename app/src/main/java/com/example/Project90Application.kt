package com.example

import android.app.Application
import com.example.data.local.database.AppDatabase
import com.example.data.repository.HabitRepository
import com.example.service.AlarmSoundManager
import com.example.service.PersistentHabitNotificationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class Project90Application : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy {
        HabitRepository(
            userDao = database.userDao(),
            habitDao = database.habitDao(),
            dailyLogDao = database.dailyLogDao(),
            financeDao = database.financeDao(),
            journalDao = database.journalDao()
        )
    }

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            repository.initializeDefaultsIfNeeded()
            // Start persistent sticky habit notification service
            try {
                PersistentHabitNotificationService.startOrUpdate(this@Project90Application)
            } catch (_: Exception) {}

            // Setup default exact alarms
            setupDefaultCurfewAlarms()
        }
    }

    private fun setupDefaultCurfewAlarms() {
        // 5:00 AM Hardcore Wake-Up
        AlarmSoundManager.scheduleCurfewAlarm(
            context = this,
            hour = 5,
            minute = 0,
            requestCode = 1001,
            title = "🔥 5:00 AM BÁO THỨC HARDCORE!",
            message = "Dậy ngay! Bật camera và chụp cốc nước hoặc bồn rửa mặt để tắt chuông!",
            is5Am = true
        )

        // 14:00 Caffeine Curfew
        AlarmSoundManager.scheduleCurfewAlarm(
            context = this,
            hour = 14,
            minute = 0,
            requestCode = 1002,
            title = "☕ LỆNH GIỚI NGHIÊM: NGỪNG CAFFEINE!",
            message = "Đã 14:00! Ngừng nạp cà phê & nước tăng lực để bảo vệ thụ thể Adenosine cho giấc ngủ đêm nay."
        )

        // 21:30 Reading 20 min Reminder
        AlarmSoundManager.scheduleCurfewAlarm(
            context = this,
            hour = 21,
            minute = 30,
            requestCode = 1003,
            title = "📖 GIỜ ĐỌC SÁCH 20 PHÚT TẬP TRUNG",
            message = "Khóa điện thoại, mở sách giấy hoặc máy đọc sách. 20 phút nâng cấp tư duy trước khi ngủ."
        )

        // 22:00 Screen Curfew
        AlarmSoundManager.scheduleCurfewAlarm(
            context = this,
            hour = 22,
            minute = 0,
            requestCode = 1004,
            title = "📵 GIỚI HẠN MÀN HÌNH SAU 22:00!",
            message = "Tắt màn hình điện thoại, ngừng lướt mạng xã hội. Cơ thể chuẩn bị tiết Melatonin tự nhiên."
        )

        // 22:30 Bedtime Curfew
        AlarmSoundManager.scheduleCurfewAlarm(
            context = this,
            hour = 22,
            minute = 30,
            requestCode = 1005,
            title = "🛏️ LÊN GIƯỜNG NGỦ TRƯỚC 22:30!",
            message = "Đi ngủ ngay để đảm bảo 6.5 tiếng phục hồi sâu trước khi thức dậy lúc 5:00 AM!"
        )
    }
}
