package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.FinanceTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FinanceDao {
    @Query("SELECT * FROM finance_transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<FinanceTransactionEntity>>

    @Query("SELECT * FROM finance_transactions WHERE dateEpochDay = :dateEpochDay ORDER BY timestamp DESC")
    fun getTransactionsForDate(dateEpochDay: Long): Flow<List<FinanceTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: FinanceTransactionEntity): Long

    @Query("DELETE FROM finance_transactions WHERE id = :id")
    suspend fun deleteTransaction(id: Long)

    @Query("SELECT SUM(amount) FROM finance_transactions WHERE type = 'INCOME'")
    fun getTotalIncome(): Flow<Long?>

    @Query("SELECT SUM(amount) FROM finance_transactions WHERE type = 'EXPENSE'")
    fun getTotalExpense(): Flow<Long?>

    @Query("SELECT SUM(savingsAllocated) FROM finance_transactions WHERE type = 'INCOME'")
    fun getTotalSavingsAllocated(): Flow<Long?>
}
