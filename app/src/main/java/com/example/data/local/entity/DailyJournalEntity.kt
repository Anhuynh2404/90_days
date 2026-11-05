package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_journals")
data class DailyJournalEntity(
    @PrimaryKey val dateEpochDay: Long,
    val line1GoodThings: String = "",
    val line2LessonLearned: String = "",
    val line3TomorrowGoal: String = "",
    val readingSecondsCompleted: Int = 0,
    val updatedAt: Long = System.currentTimeMillis()
)
