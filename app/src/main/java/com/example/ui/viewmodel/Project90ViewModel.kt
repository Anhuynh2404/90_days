package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.Project90Application
import com.example.camera.ClassificationResult
import com.example.camera.WakeUpTarget
import com.example.data.local.entity.DailyJournalEntity
import com.example.data.local.entity.DailyLogEntity
import com.example.data.local.entity.FinanceTransactionEntity
import com.example.data.local.entity.HabitEntity
import com.example.data.local.entity.UserEntity
import com.example.sensor.StepCounterManager
import com.example.service.AlarmSoundManager
import com.example.service.BankNotificationListenerService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

import com.example.service.AlarmToneType
import com.example.service.AlarmVibrationType
import com.example.service.HabitNotificationHelper
import com.example.service.PersistentHabitNotificationService

enum class AppTab(val title: String, val categoryTag: String) {
    DASHBOARD("Tổng Quan", "ALL"),
    MOVEMENT("Vận Động", "MOVEMENT"),
    NUTRITION("Dinh Dưỡng", "NUTRITION"),
    DIGITAL("Detox & Ngủ", "DIGITAL_WELLBEING"),
    ALARM_5AM("Báo Thức 5:00", "WAKE_UP_5AM"),
    FINANCE("Tài Chính", "FINANCE"),
    GROWTH("Phát Triển", "SELF_GROWTH"),
    SETTINGS("Cài Đặt", "SETTINGS")
}

data class DashboardSummary(
    val dayNumber: Int = 1,
    val totalDays: Int = 90,
    val daysRemainingToFeb: Long = 85,
    val progressPercent: Float = 0.1f,
    val completedTodayCount: Int = 0,
    val totalDailyHabitsCount: Int = 13,
    val streakWarning: Boolean = false,
    val currentStreak: Int = 7
)

class Project90ViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as Project90Application).repository
    val stepCounterManager = StepCounterManager(application)
    val alarmSoundManager = AlarmSoundManager(application)

    val todayEpochDay = LocalDate.now().toEpochDay()
    private val _selectedDate = MutableStateFlow(todayEpochDay)
    val selectedDate: StateFlow<Long> = _selectedDate.asStateFlow()

    private val _currentTab = MutableStateFlow(AppTab.DASHBOARD)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    val user: StateFlow<UserEntity?> = repository.userFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val habits: StateFlow<List<HabitEntity>> = repository.allHabitsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayLogs: StateFlow<List<DailyLogEntity>> = repository.getLogsForDate(todayEpochDay)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayJournal: StateFlow<DailyJournalEntity?> = repository.getJournalForDate(todayEpochDay)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val transactions: StateFlow<List<FinanceTransactionEntity>> = repository.allFinanceFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalIncome: StateFlow<Long?> = repository.totalIncomeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val totalExpense: StateFlow<Long?> = repository.totalExpenseFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val totalSavings: StateFlow<Long?> = repository.totalSavingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    // Camera Alarm & Classification State
    private val _wakeUpTarget = MutableStateFlow(WakeUpTarget.WATER_CUP)
    val wakeUpTarget: StateFlow<WakeUpTarget> = _wakeUpTarget.asStateFlow()

    private val _cameraClassification = MutableStateFlow<ClassificationResult?>(null)
    val cameraClassification: StateFlow<ClassificationResult?> = _cameraClassification.asStateFlow()

    private val _isAlarmRinging = MutableStateFlow(false)
    val isAlarmRinging: StateFlow<Boolean> = _isAlarmRinging.asStateFlow()

    private val _alarmDismissSuccess = MutableStateFlow(false)
    val alarmDismissSuccess: StateFlow<Boolean> = _alarmDismissSuccess.asStateFlow()

    // 20-Minute Reading Timer
    private val _readingTimerSeconds = MutableStateFlow(20 * 60) // 1200s
    val readingTimerSeconds: StateFlow<Int> = _readingTimerSeconds.asStateFlow()

    private val _isReadingRunning = MutableStateFlow(false)
    val isReadingRunning: StateFlow<Boolean> = _isReadingRunning.asStateFlow()

    private var timerJob: Job? = null

    // Dashboard summary combining user & logs
    val dashboardSummary: StateFlow<DashboardSummary> = combine(user, habits, todayLogs) { u, hList, logs ->
        val dailyHabits = hList.filter { !it.isWeekly }
        val completedCount = logs.count { it.isCompleted }
        val totalDaily = if (dailyHabits.isNotEmpty()) dailyHabits.size else 13

        val officialStartEpoch = LocalDate.of(2026, 11, 7).toEpochDay()
        val startDay = u?.startDateEpochDay ?: officialStartEpoch
        val targetDay = u?.targetDateEpochDay ?: (startDay + 90)

        val diffFromStart = (todayEpochDay - startDay).toInt()
        val daysIntoJourney = if (diffFromStart < 0) {
            0
        } else {
            (diffFromStart + 1).coerceIn(1, 90)
        }
        val remaining = (targetDay - todayEpochDay).coerceAtLeast(0)
        val progress = if (daysIntoJourney <= 0) 0f else (daysIntoJourney / 90f).coerceIn(0f, 1f)

        val hasStreakRisk = daysIntoJourney > 0 && completedCount < totalDaily

        DashboardSummary(
            dayNumber = daysIntoJourney,
            totalDays = 90,
            daysRemainingToFeb = remaining,
            progressPercent = progress,
            completedTodayCount = completedCount,
            totalDailyHabitsCount = totalDaily,
            streakWarning = hasStreakRisk,
            currentStreak = u?.currentStreak ?: 0
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardSummary())

    init {
        stepCounterManager.startListening()

        // Sync step sensor to Room
        viewModelScope.launch {
            stepCounterManager.stepCount.collect { steps ->
                if (steps > 0) {
                    repository.updateSteps(todayEpochDay, steps)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stepCounterManager.stopListening()
        alarmSoundManager.dismissAlarm()
        timerJob?.cancel()
    }

    fun selectTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun toggleHabit(habitId: String, currentCompleted: Boolean, currentProgress: Int, target: Int) {
        viewModelScope.launch {
            val newCompleted = !currentCompleted
            val newProgress = if (newCompleted) target else 0
            repository.setHabitProgress(todayEpochDay, habitId, newCompleted, newProgress)
            HabitNotificationHelper.postOrUpdateNotification(getApplication())
        }
    }

    fun updateHabitProgress(habitId: String, newProgress: Int, target: Int) {
        viewModelScope.launch {
            val isDone = newProgress >= target
            repository.setHabitProgress(todayEpochDay, habitId, isDone, newProgress)
            HabitNotificationHelper.postOrUpdateNotification(getApplication())
        }
    }

    fun addWater(amountMl: Int) {
        viewModelScope.launch {
            repository.addWater(todayEpochDay, amountMl)
            HabitNotificationHelper.postOrUpdateNotification(getApplication())
        }
    }

    fun addManualSteps(steps: Int) {
        stepCounterManager.addManualSteps(steps)
        val current = stepCounterManager.stepCount.value
        viewModelScope.launch {
            repository.updateSteps(todayEpochDay, current)
            HabitNotificationHelper.postOrUpdateNotification(getApplication())
        }
    }

    fun addTransaction(
        amount: Long,
        type: String,
        category: String,
        bankSource: String,
        description: String,
        isSalary: Boolean
    ) {
        viewModelScope.launch {
            repository.addFinancialTransaction(
                amount = amount,
                type = type,
                category = category,
                bankSource = bankSource,
                description = description,
                isSalary = isSalary
            )
        }
    }

    // Alarm Sound & Vibration Settings
    private val _selectedTone = MutableStateFlow(alarmSoundManager.selectedTone)
    val selectedTone: StateFlow<AlarmToneType> = _selectedTone.asStateFlow()

    private val _selectedVibration = MutableStateFlow(alarmSoundManager.selectedVibration)
    val selectedVibration: StateFlow<AlarmVibrationType> = _selectedVibration.asStateFlow()

    private val _alarmVolume = MutableStateFlow(alarmSoundManager.alarmVolumePercent)
    val alarmVolume: StateFlow<Int> = _alarmVolume.asStateFlow()

    fun setAlarmTone(tone: AlarmToneType) {
        alarmSoundManager.selectedTone = tone
        _selectedTone.value = tone
    }

    fun setAlarmVibration(vib: AlarmVibrationType) {
        alarmSoundManager.selectedVibration = vib
        _selectedVibration.value = vib
    }

    fun setAlarmVolume(volume: Int) {
        alarmSoundManager.alarmVolumePercent = volume
        _alarmVolume.value = volume
    }

    fun playTonePreview(tone: AlarmToneType) {
        _isAlarmRinging.value = true
        alarmSoundManager.triggerHardcoreAlarm(overrideTone = tone)
    }

    fun refreshPersistentNotification() {
        try {
            HabitNotificationHelper.postOrUpdateNotification(getApplication())
            PersistentHabitNotificationService.startOrUpdate(getApplication())
        } catch (_: Exception) {}
    }

    // Alarm controls
    fun triggerTestAlarm() {
        _isAlarmRinging.value = true
        _alarmDismissSuccess.value = false
        alarmSoundManager.triggerHardcoreAlarm()
    }

    fun setWakeUpTarget(target: WakeUpTarget) {
        _wakeUpTarget.value = target
    }

    fun onFrameClassified(result: ClassificationResult) {
        _cameraClassification.value = result
        if (result.isMatch && _isAlarmRinging.value) {
            // Dismiss alarm successfully!
            _alarmDismissSuccess.value = true
            _isAlarmRinging.value = false
            alarmSoundManager.dismissAlarm()

            viewModelScope.launch {
                repository.setHabitProgress(todayEpochDay, "alarm_5am_camera", true, 1)
            }
        }
    }

    fun dismissAlarmManually() {
        _isAlarmRinging.value = false
        alarmSoundManager.dismissAlarm()
    }

    // Reading Timer
    fun toggleReadingTimer() {
        if (_isReadingRunning.value) {
            timerJob?.cancel()
            _isReadingRunning.value = false
        } else {
            _isReadingRunning.value = true
            timerJob = viewModelScope.launch {
                while (_readingTimerSeconds.value > 0 && _isReadingRunning.value) {
                    delay(1000)
                    _readingTimerSeconds.value -= 1
                    val elapsedSeconds = (20 * 60) - _readingTimerSeconds.value
                    repository.saveReadingSeconds(todayEpochDay, elapsedSeconds)
                }
                _isReadingRunning.value = false
            }
        }
    }

    fun resetReadingTimer() {
        timerJob?.cancel()
        _isReadingRunning.value = false
        _readingTimerSeconds.value = 20 * 60
    }

    fun saveJournal(l1: String, l2: String, l3: String) {
        viewModelScope.launch {
            repository.saveJournal(todayEpochDay, l1, l2, l3)
        }
    }
}
