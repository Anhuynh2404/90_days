package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "Hardcore Striker",
    val startDateEpochDay: Long = 0L,
    val targetDateEpochDay: Long = 0L, // Countdown target (e.g., February 1st)
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastCompletedDateEpochDay: Long = -1L,
    val totalSavings: Long = 0L,
    val totalIncome: Long = 0L,
    val totalExpense: Long = 0L
)
