package com.example.data.repository

import com.example.data.local.dao.DailyLogDao
import com.example.data.local.dao.FinanceDao
import com.example.data.local.dao.HabitDao
import com.example.data.local.dao.JournalDao
import com.example.data.local.dao.UserDao
import com.example.data.local.entity.DailyJournalEntity
import com.example.data.local.entity.DailyLogEntity
import com.example.data.local.entity.FinanceTransactionEntity
import com.example.data.local.entity.HabitCategory
import com.example.data.local.entity.HabitEntity
import com.example.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDate
import java.time.Month
import java.time.temporal.ChronoUnit

class HabitRepository(
    private val userDao: UserDao,
    private val habitDao: HabitDao,
    private val dailyLogDao: DailyLogDao,
    private val financeDao: FinanceDao,
    private val journalDao: JournalDao
) {
    val userFlow: Flow<UserEntity?> = userDao.getUser()
    val allHabitsFlow: Flow<List<HabitEntity>> = habitDao.getAllHabits()
    val allFinanceFlow: Flow<List<FinanceTransactionEntity>> = financeDao.getAllTransactions()
    val totalIncomeFlow: Flow<Long?> = financeDao.getTotalIncome()
    val totalExpenseFlow: Flow<Long?> = financeDao.getTotalExpense()
    val totalSavingsFlow: Flow<Long?> = financeDao.getTotalSavingsAllocated()

    fun getLogsForDate(dateEpochDay: Long): Flow<List<DailyLogEntity>> =
        dailyLogDao.getLogsForDate(dateEpochDay)

    fun getJournalForDate(dateEpochDay: Long): Flow<DailyJournalEntity?> =
        journalDao.getJournalForDate(dateEpochDay)

    suspend fun initializeDefaultsIfNeeded() {
        // Initialize default habits if empty
        if (habitDao.getHabitCount() == 0) {
            val defaultHabits = listOf(
                HabitEntity(
                    id = "pedometer_10k",
                    title = "Đi bộ 10.000 bước mỗi ngày",
                    category = HabitCategory.MOVEMENT.name,
                    targetValue = 10000,
                    unit = "bước",
                    description = "Đốt mỡ thụ động, cải thiện trao đổi chất và tăng sức bền",
                    displayOrder = 1
                ),
                HabitEntity(
                    id = "pushups_50",
                    title = "50 lần Hít đất (Push-ups)",
                    category = HabitCategory.MOVEMENT.name,
                    targetValue = 50,
                    unit = "cái",
                    description = "Xây dựng cơ ngực, vai và bắp tay sau rắn rỏi",
                    displayOrder = 2
                ),
                HabitEntity(
                    id = "squats_100",
                    title = "100 lần Squats",
                    category = HabitCategory.MOVEMENT.name,
                    targetValue = 100,
                    unit = "cái",
                    description = "Gia tăng testosterone tự nhiên, săn chắc thân dưới",
                    displayOrder = 3
                ),
                HabitEntity(
                    id = "cold_shower",
                    title = "Tắm nước lạnh sảng khoái",
                    category = HabitCategory.MOVEMENT.name,
                    targetValue = 1,
                    unit = "lần",
                    description = "Thức tỉnh hệ thần kinh giao cảm, tăng dopamine bền vững",
                    displayOrder = 4
                ),
                HabitEntity(
                    id = "workout_weekly",
                    title = "2 buổi tập Gym / Kháng lực",
                    category = HabitCategory.MOVEMENT.name,
                    isWeekly = true,
                    targetValue = 2,
                    unit = "buổi/tuần",
                    description = "Kích thích phì đại cơ bắp toàn thân với tạ nặng",
                    displayOrder = 5
                ),
                HabitEntity(
                    id = "football_weekly",
                    title = "1 trận đá bóng đối kháng",
                    category = HabitCategory.MOVEMENT.name,
                    isWeekly = true,
                    targetValue = 1,
                    unit = "trận/tuần",
                    description = "Tập luyện tim mạch cường độ cao và phản xạ đồng đội",
                    displayOrder = 6
                ),
                HabitEntity(
                    id = "no_sugar",
                    title = "Kỷ luật Tuyệt Đối: Không Đường (No Sugar)",
                    category = HabitCategory.NUTRITION.name,
                    targetValue = 1,
                    unit = "ngày",
                    description = "Cắt đứt đồ ngọt, trà sữa, nước có gas. Giải phóng gan nhiễm mỡ",
                    displayOrder = 7
                ),
                HabitEntity(
                    id = "water_2l",
                    title = "Uống đủ 2 Lít nước lọc",
                    category = HabitCategory.NUTRITION.name,
                    targetValue = 2000,
                    unit = "ml",
                    description = "Thanh lọc cơ thể, tối ưu hoạt động tế bào não và cơ bắp",
                    displayOrder = 8
                ),
                HabitEntity(
                    id = "caffeine_curfew",
                    title = "Ngừng nạp Caffeine sau 14:00",
                    category = HabitCategory.DIGITAL_WELLBEING.name,
                    targetValue = 1,
                    unit = "ngày",
                    description = "Giữ thụ thể Adenosine tự nhiên giúp đi vào giấc ngủ sâu",
                    displayOrder = 9
                ),
                HabitEntity(
                    id = "screen_limit_22",
                    title = "Khóa màn hình điện thoại sau 22:00",
                    category = HabitCategory.DIGITAL_WELLBEING.name,
                    targetValue = 1,
                    unit = "ngày",
                    description = "Chặn ánh sáng xanh ức chế Melatonin, chuẩn bị phục hồi",
                    displayOrder = 10
                ),
                HabitEntity(
                    id = "bedtime_2230",
                    title = "Lên giường ngủ trước 22:30",
                    category = HabitCategory.DIGITAL_WELLBEING.name,
                    targetValue = 1,
                    unit = "ngày",
                    description = "Tối đa hóa pha ngủ sâu (Deep Sleep) và sóng delta",
                    displayOrder = 11
                ),
                HabitEntity(
                    id = "alarm_5am_camera",
                    title = "Thức dậy 5:00 AM Hardcore (Tắt bằng Camera)",
                    category = HabitCategory.WAKE_UP_5AM.name,
                    targetValue = 1,
                    unit = "ngày",
                    description = "Chuông chỉ tắt khi chụp đúng cốc nước hoặc bồn rửa mặt",
                    displayOrder = 12
                ),
                HabitEntity(
                    id = "savings_10pct",
                    title = "Tự động trích 10% thu nhập vào quỹ tiết kiệm",
                    category = HabitCategory.FINANCE.name,
                    targetValue = 1,
                    unit = "lần",
                    description = "Nguyên tắc 'Pay Yourself First' trước khi chi tiêu bất kỳ khoản nào",
                    displayOrder = 13
                ),
                HabitEntity(
                    id = "reading_20min",
                    title = "Đọc sách 20 phút (Khóa tập trung)",
                    category = HabitCategory.SELF_GROWTH.name,
                    targetValue = 20,
                    unit = "phút",
                    description = "Nâng cấp tư duy, mở rộng vốn từ và khả năng duy trì tập trung",
                    displayOrder = 14
                ),
                HabitEntity(
                    id = "journal_3lines",
                    title = "3 dòng nhật ký tối cô đọng",
                    category = HabitCategory.SELF_GROWTH.name,
                    targetValue = 3,
                    unit = "dòng",
                    description = "1. Việc làm tốt - 2. Bài học rút ra - 3. Mục tiêu ngày mai",
                    displayOrder = 15
                )
            )
            habitDao.insertAll(defaultHabits)
        }

        // Initialize User with 90-day countdown to February 1st
        val existingUser = userDao.getUser().firstOrNull()
        if (existingUser == null) {
            val today = LocalDate.now()
            // Target is February 1st of upcoming year (or next February)
            val currentYear = today.year
            val targetFeb = if (today.month.value >= Month.FEBRUARY.value) {
                LocalDate.of(currentYear + 1, Month.FEBRUARY, 1)
            } else {
                LocalDate.of(currentYear, Month.FEBRUARY, 1)
            }
            // 90 days before February 1st
            val calculatedStart = targetFeb.minusDays(90)

            val initialUser = UserEntity(
                id = 1,
                name = "Kẻ Kỷ Luật Thép",
                startDateEpochDay = calculatedStart.toEpochDay(),
                targetDateEpochDay = targetFeb.toEpochDay(),
                currentStreak = 7,
                longestStreak = 14,
                lastCompletedDateEpochDay = today.minusDays(1).toEpochDay(),
                totalSavings = 2500000L,
                totalIncome = 25000000L,
                totalExpense = 8200000L
            )
            userDao.insertOrUpdate(initialUser)

            // Seed sample initial transactions if empty
            val initialTrans = listOf(
                FinanceTransactionEntity(
                    dateEpochDay = today.minusDays(3).toEpochDay(),
                    amount = 25000000L,
                    type = "INCOME",
                    category = "Lương",
                    bankSource = "MB Bank",
                    savingsAllocated = 2500000L,
                    description = "Lương tháng chuyển khoản - Tự động trích 10% tiết kiệm"
                ),
                FinanceTransactionEntity(
                    dateEpochDay = today.minusDays(2).toEpochDay(),
                    amount = 450000L,
                    type = "EXPENSE",
                    category = "Dinh dưỡng",
                    bankSource = "TNEX",
                    savingsAllocated = 0L,
                    description = "Mua ức gà, trứng và rau xanh tuần"
                ),
                FinanceTransactionEntity(
                    dateEpochDay = today.minusDays(1).toEpochDay(),
                    amount = 250000L,
                    type = "EXPENSE",
                    category = "Tập luyện",
                    bankSource = "TNEX",
                    savingsAllocated = 0L,
                    description = "Tiền sân bóng đá giao hữu"
                )
            )
            initialTrans.forEach { financeDao.insertTransaction(it) }

            // Seed today's sample progress
            seedInitialLogsForDate(today.toEpochDay())
        }
    }

    private suspend fun seedInitialLogsForDate(dateEpochDay: Long) {
        val seed = listOf(
            DailyLogEntity(dateEpochDay = dateEpochDay, habitId = "pedometer_10k", isCompleted = false, progressValue = 6840),
            DailyLogEntity(dateEpochDay = dateEpochDay, habitId = "pushups_50", isCompleted = true, progressValue = 50),
            DailyLogEntity(dateEpochDay = dateEpochDay, habitId = "squats_100", isCompleted = true, progressValue = 100),
            DailyLogEntity(dateEpochDay = dateEpochDay, habitId = "cold_shower", isCompleted = true, progressValue = 1),
            DailyLogEntity(dateEpochDay = dateEpochDay, habitId = "no_sugar", isCompleted = true, progressValue = 1),
            DailyLogEntity(dateEpochDay = dateEpochDay, habitId = "water_2l", isCompleted = false, progressValue = 1500),
            DailyLogEntity(dateEpochDay = dateEpochDay, habitId = "caffeine_curfew", isCompleted = true, progressValue = 1),
            DailyLogEntity(dateEpochDay = dateEpochDay, habitId = "alarm_5am_camera", isCompleted = true, progressValue = 1),
            DailyLogEntity(dateEpochDay = dateEpochDay, habitId = "reading_20min", isCompleted = false, progressValue = 12),
            DailyLogEntity(dateEpochDay = dateEpochDay, habitId = "journal_3lines", isCompleted = false, progressValue = 0)
        )
        seed.forEach { dailyLogDao.upsertLog(it) }
    }

    suspend fun setHabitProgress(dateEpochDay: Long, habitId: String, completed: Boolean, progressVal: Int) {
        dailyLogDao.upsertLog(
            DailyLogEntity(
                dateEpochDay = dateEpochDay,
                habitId = habitId,
                isCompleted = completed,
                progressValue = progressVal,
                updatedAt = System.currentTimeMillis()
            )
        )
        evaluateStreak(dateEpochDay)
    }

    suspend fun addWater(dateEpochDay: Long, amountMl: Int) {
        val currentLog = dailyLogDao.getLogForHabit(dateEpochDay, "water_2l").firstOrNull()
        val currentVal = currentLog?.progressValue ?: 0
        val newVal = (currentVal + amountMl).coerceAtLeast(0)
        val isCompleted = newVal >= 2000

        dailyLogDao.upsertLog(
            DailyLogEntity(
                id = currentLog?.id ?: 0L,
                dateEpochDay = dateEpochDay,
                habitId = "water_2l",
                isCompleted = isCompleted,
                progressValue = newVal,
                updatedAt = System.currentTimeMillis()
            )
        )
        evaluateStreak(dateEpochDay)
    }

    suspend fun updateSteps(dateEpochDay: Long, steps: Int) {
        val currentLog = dailyLogDao.getLogForHabit(dateEpochDay, "pedometer_10k").firstOrNull()
        val isCompleted = steps >= 10000

        dailyLogDao.upsertLog(
            DailyLogEntity(
                id = currentLog?.id ?: 0L,
                dateEpochDay = dateEpochDay,
                habitId = "pedometer_10k",
                isCompleted = isCompleted,
                progressValue = steps,
                updatedAt = System.currentTimeMillis()
            )
        )
        evaluateStreak(dateEpochDay)
    }

    suspend fun addFinancialTransaction(
        amount: Long,
        type: String,
        category: String,
        bankSource: String,
        description: String,
        isSalary: Boolean
    ) {
        val savings = if (type == "INCOME" && (isSalary || category.contains("Lương", ignoreCase = true))) {
            (amount * 10) / 100 // 10% auto allocation
        } else {
            0L
        }

        val tx = FinanceTransactionEntity(
            dateEpochDay = LocalDate.now().toEpochDay(),
            amount = amount,
            type = type,
            category = category,
            bankSource = bankSource,
            savingsAllocated = savings,
            description = description
        )
        financeDao.insertTransaction(tx)

        val incomeDelta = if (type == "INCOME") amount else 0L
        val expenseDelta = if (type == "EXPENSE") amount else 0L
        userDao.updateFinanceTotals(savings = savings, income = incomeDelta, expense = expenseDelta)
    }

    suspend fun saveJournal(dateEpochDay: Long, l1: String, l2: String, l3: String) {
        val current = journalDao.getJournalForDate(dateEpochDay).firstOrNull()
        val completed = l1.isNotBlank() && l2.isNotBlank() && l3.isNotBlank()

        journalDao.upsertJournal(
            DailyJournalEntity(
                dateEpochDay = dateEpochDay,
                line1GoodThings = l1,
                line2LessonLearned = l2,
                line3TomorrowGoal = l3,
                readingSecondsCompleted = current?.readingSecondsCompleted ?: 0,
                updatedAt = System.currentTimeMillis()
            )
        )

        dailyLogDao.upsertLog(
            DailyLogEntity(
                dateEpochDay = dateEpochDay,
                habitId = "journal_3lines",
                isCompleted = completed,
                progressValue = if (completed) 3 else (listOf(l1, l2, l3).count { it.isNotBlank() }),
                updatedAt = System.currentTimeMillis()
            )
        )
        evaluateStreak(dateEpochDay)
    }

    suspend fun saveReadingSeconds(dateEpochDay: Long, seconds: Int) {
        val current = journalDao.getJournalForDate(dateEpochDay).firstOrNull()
        journalDao.upsertJournal(
            DailyJournalEntity(
                dateEpochDay = dateEpochDay,
                line1GoodThings = current?.line1GoodThings ?: "",
                line2LessonLearned = current?.line2LessonLearned ?: "",
                line3TomorrowGoal = current?.line3TomorrowGoal ?: "",
                readingSecondsCompleted = seconds,
                updatedAt = System.currentTimeMillis()
            )
        )

        val minutes = seconds / 60
        val isCompleted = minutes >= 20
        dailyLogDao.upsertLog(
            DailyLogEntity(
                dateEpochDay = dateEpochDay,
                habitId = "reading_20min",
                isCompleted = isCompleted,
                progressValue = minutes,
                updatedAt = System.currentTimeMillis()
            )
        )
        evaluateStreak(dateEpochDay)
    }

    private suspend fun evaluateStreak(dateEpochDay: Long) {
        val user = userDao.getUser().firstOrNull() ?: return
        val dailyHabits = habitDao.getAllHabits().firstOrNull()?.filter { !it.isWeekly } ?: return
        val todayLogs = dailyLogDao.getLogsForDate(dateEpochDay).firstOrNull() ?: return

        val completedCount = todayLogs.count { it.isCompleted }
        val allDone = completedCount >= dailyHabits.size && dailyHabits.isNotEmpty()

        if (allDone && user.lastCompletedDateEpochDay != dateEpochDay) {
            val newStreak = user.currentStreak + 1
            val longest = maxOf(newStreak, user.longestStreak)
            userDao.updateStreak(newStreak, longest, dateEpochDay)
        }
    }
}
