package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "daily_logs",
    indices = [Index(value = ["dateEpochDay", "habitId"], unique = true)]
)
data class DailyLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val dateEpochDay: Long,
    val habitId: String,
    val isCompleted: Boolean = false,
    val progressValue: Int = 0,
    val note: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)
