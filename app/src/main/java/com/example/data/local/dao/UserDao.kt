package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = 1 LIMIT 1")
    fun getUser(): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(user: UserEntity)

    @Update
    suspend fun update(user: UserEntity)

    @Query("UPDATE users SET currentStreak = :streak, longestStreak = :longest, lastCompletedDateEpochDay = :lastCompleted WHERE id = 1")
    suspend fun updateStreak(streak: Int, longest: Int, lastCompleted: Long)

    @Query("UPDATE users SET totalSavings = totalSavings + :savings, totalIncome = totalIncome + :income, totalExpense = totalExpense + :expense WHERE id = 1")
    suspend fun updateFinanceTotals(savings: Long, income: Long, expense: Long)
}
