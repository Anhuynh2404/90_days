package com.example.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.media.ToneGenerator
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Calendar

enum class AlarmToneType(val id: String, val title: String, val description: String) {
    HARDCORE_BUZZER("buzzer", "Còi Khẩn Cấp (Hardcore Buzzer)", "Âm tần số kép gắt, tỉnh ngủ lập tức"),
    MILITARY_SIREN("siren", "Còi Báo Động Quân Đội (Military Siren)", "Biến thiên tần số cao kích thích nhịp tim"),
    TACTICAL_PULSE("pulse", "Xung Âm Chiến Thuật (Tactical Beep)", "Nhịp ngắt quãng đanh thép"),
    SYSTEM_ALARM("system", "Chuông Mặc Định Hệ Thống", "Sử dụng nhạc chuông báo thức mặc định của máy")
}

enum class AlarmVibrationType(val id: String, val title: String, val pattern: LongArray) {
    HARDCORE_CONTINUOUS("continuous", "Dồn Dập Khắc Nghiệt", longArrayOf(0, 800, 200, 800, 200, 1200)),
    WARRIOR_PULSE("warrior", "Nhịp Đập Chiến Binh", longArrayOf(0, 400, 150, 400, 500)),
    RAPID_STACCATO("staccato", "Báo Nguy SOS Ngắt Quãng", longArrayOf(0, 200, 100, 200, 100, 200, 400))
}

class AlarmSoundManager(private val context: Context) {

    private val managerScope = CoroutineScope(Dispatchers.Default)

    private var ringtone: Ringtone? = null
    private var vibrator: Vibrator? = null
    private var toneGenerator: ToneGenerator? = null
    private var soundLoopJob: Job? = null

    private val _isAlarmRinging = MutableStateFlow(false)
    val isAlarmRinging: StateFlow<Boolean> = _isAlarmRinging.asStateFlow()

    // Configurable Alarm Settings
    private val prefs = context.getSharedPreferences("project90_alarm_prefs", Context.MODE_PRIVATE)

    var selectedTone: AlarmToneType
        get() {
            val key = prefs.getString("selected_tone", AlarmToneType.HARDCORE_BUZZER.name)
            return try { AlarmToneType.valueOf(key ?: AlarmToneType.HARDCORE_BUZZER.name) } catch (_: Exception) { AlarmToneType.HARDCORE_BUZZER }
        }
        set(value) = prefs.edit().putString("selected_tone", value.name).apply()

    var selectedVibration: AlarmVibrationType
        get() {
            val key = prefs.getString("selected_vibration", AlarmVibrationType.HARDCORE_CONTINUOUS.name)
            return try { AlarmVibrationType.valueOf(key ?: AlarmVibrationType.HARDCORE_CONTINUOUS.name) } catch (_: Exception) { AlarmVibrationType.HARDCORE_CONTINUOUS }
        }
        set(value) = prefs.edit().putString("selected_vibration", value.name).apply()

    var alarmVolumePercent: Int
        get() = prefs.getInt("alarm_volume", 100)
        set(value) = prefs.edit().putInt("alarm_volume", value.coerceIn(10, 100)).apply()

    init {
        try {
            val uri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            ringtone = RingtoneManager.getRingtone(context, uri)?.apply {
                audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            }
        } catch (_: Exception) {}

        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }

        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, alarmVolumePercent)
        } catch (_: Exception) {}
    }

    fun triggerHardcoreAlarm(overrideTone: AlarmToneType? = null) {
        if (_isAlarmRinging.value) return
        _isAlarmRinging.value = true

        val tone = overrideTone ?: selectedTone
        val vibType = selectedVibration

        // Trigger vibration
        try {
            val pattern = vibType.pattern
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(pattern, 0)
            }
        } catch (_: Exception) {}

        // Sound generation loop
        soundLoopJob?.cancel()
        soundLoopJob = managerScope.launch {
            when (tone) {
                AlarmToneType.SYSTEM_ALARM -> {
                    try {
                        ringtone?.play()
                    } catch (_: Exception) {}
                }
                AlarmToneType.HARDCORE_BUZZER -> {
                    while (isActive && _isAlarmRinging.value) {
                        try {
                            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_EMERGENCY_RINGBACK, 700)
                        } catch (_: Exception) {}
                        delay(900)
                    }
                }
                AlarmToneType.MILITARY_SIREN -> {
                    while (isActive && _isAlarmRinging.value) {
                        try {
                            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 350)
                            delay(400)
                            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE, 450)
                        } catch (_: Exception) {}
                        delay(600)
                    }
                }
                AlarmToneType.TACTICAL_PULSE -> {
                    while (isActive && _isAlarmRinging.value) {
                        try {
                            toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT, 200)
                            delay(250)
                            toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT, 200)
                        } catch (_: Exception) {}
                        delay(600)
                    }
                }
            }
        }
    }

    fun dismissAlarm() {
        _isAlarmRinging.value = false
        soundLoopJob?.cancel()
        soundLoopJob = null

        try {
            if (ringtone?.isPlaying == true) {
                ringtone?.stop()
            }
        } catch (_: Exception) {}

        try {
            toneGenerator?.stopTone()
        } catch (_: Exception) {}

        try {
            vibrator?.cancel()
        } catch (_: Exception) {}
    }

    companion object {
        fun scheduleCurfewAlarm(
            context: Context,
            hour: Int,
            minute: Int,
            requestCode: Int,
            title: String,
            message: String,
            is5Am: Boolean = false
        ) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

            val intent = Intent(context, AlarmReminderReceiver::class.java).apply {
                putExtra(AlarmReminderReceiver.EXTRA_TITLE, title)
                putExtra(AlarmReminderReceiver.EXTRA_MESSAGE, message)
                putExtra(AlarmReminderReceiver.EXTRA_IS_5AM, is5Am)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                if (before(Calendar.getInstance())) {
                    add(Calendar.DAY_OF_YEAR, 1) // Next day
                }
            }

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                }
            } catch (_: Exception) {}
        }
    }
}
