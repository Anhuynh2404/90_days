package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "finance_transactions")
data class FinanceTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val dateEpochDay: Long,
    val amount: Long, // in VND
    val type: String, // "INCOME" or "EXPENSE"
    val category: String, // "Lương", "Ăn uống", "Tập gym", "Đầu tư", "Khác"
    val bankSource: String, // "TNEX", "MB Bank", "Thủ công"
    val savingsAllocated: Long = 0L, // 10% auto savings deduction on salary income
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
