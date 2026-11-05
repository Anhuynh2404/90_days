package com.example.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.example.Project90Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.regex.Pattern

data class ParsedBankTransaction(
    val bankName: String,
    val amount: Long,
    val type: String,
    val category: String,
    val description: String,
    val isSalary: Boolean
)

class BankNotificationListenerService : NotificationListenerService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO)

    companion object {
        private val _parsedNotificationEvents = MutableSharedFlow<ParsedBankTransaction>(extraBufferCapacity = 10)
        val parsedNotificationEvents: SharedFlow<ParsedBankTransaction> = _parsedNotificationEvents.asSharedFlow()

        // Bank detection patterns
        val SUPPORTED_BANK_PACKAGES = setOf(
            "com.msb.tnex",        // TNEX
            "com.mbmobile",        // MB Bank
            "com.vcb",             // Vietcombank
            "com.techcombank.bb.app" // Techcombank
        )

        fun parseBankMessage(title: String, body: String, packageName: String = ""): ParsedBankTransaction? {
            val combined = "$title $body".lowercase()

            val isTnex = packageName.contains("tnex", ignoreCase = true) || combined.contains("tnex")
            val isMb = packageName.contains("mb", ignoreCase = true) || combined.contains("mb bank") || combined.contains("mbbank")
            val bankName = when {
                isTnex -> "TNEX"
                isMb -> "MB Bank"
                combined.contains("vietcombank") || combined.contains("vcb") -> "Vietcombank"
                combined.contains("techcombank") -> "Techcombank"
                else -> if (packageName.isNotEmpty()) "Ngân Hàng" else "TNEX / MB Bank"
            }

            // Detect Income (+) vs Expense (-)
            val isIncome = combined.contains("+") || combined.contains("nhan duoc") ||
                    combined.contains("nhận tiền") || combined.contains("tang") || combined.contains("credit")
            val isExpense = combined.contains("-") || combined.contains("thanh toan") ||
                    combined.contains("chi tieu") || combined.contains("chuyen tien") || combined.contains("debit")

            if (!isIncome && !isExpense) {
                return null
            }

            val type = if (isIncome) "INCOME" else "EXPENSE"

            // Regex to extract numbers with dots or commas (e.g., 25.000.000 or 150,000)
            val regex = Pattern.compile("([+-]?\\s*[0-9]{1,3}(?:[.,][0-9]{3})+|[0-9]{4,10})\\s*(?:vnd|đ|d)?", Pattern.CASE_INSENSITIVE)
            val matcher = regex.matcher(body)
            var amount = 0L
            if (matcher.find()) {
                val rawNum = matcher.group(1)?.replace(".", "")?.replace(",", "")?.replace("+", "")?.replace("-", "")?.trim()
                amount = rawNum?.toLongOrNull() ?: 0L
            }

            if (amount <= 0L) {
                // Fallback attempt from title
                val titleMatcher = regex.matcher(title)
                if (titleMatcher.find()) {
                    val rawNum = titleMatcher.group(1)?.replace(".", "")?.replace(",", "")?.replace("+", "")?.replace("-", "")?.trim()
                    amount = rawNum?.toLongOrNull() ?: 0L
                }
            }

            if (amount <= 0L) return null

            val isSalary = combined.contains("luong") || combined.contains("lương") || combined.contains("salary")
            val category = when {
                isSalary -> "Lương"
                combined.contains("an uong") || combined.contains("food") || combined.contains("cafe") -> "Ăn uống"
                combined.contains("gym") || combined.contains("the thao") || combined.contains("bong da") -> "Thể thao & Sức khỏe"
                else -> if (type == "INCOME") "Thu nhập khác" else "Chi tiêu thường ngày"
            }

            return ParsedBankTransaction(
                bankName = bankName,
                amount = amount,
                type = type,
                category = category,
                description = body.take(120),
                isSalary = isSalary
            )
        }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        val extras = sbn?.notification?.extras ?: return
        val title = extras.getString("android.title") ?: ""
        val text = extras.getCharSequence("android.text")?.toString() ?: ""
        val packageName = sbn.packageName ?: ""

        val parsed = parseBankMessage(title, text, packageName)
        if (parsed != null) {
            _parsedNotificationEvents.tryEmit(parsed)

            serviceScope.launch {
                try {
                    val repo = (application as? Project90Application)?.repository
                    repo?.addFinancialTransaction(
                        amount = parsed.amount,
                        type = parsed.type,
                        category = parsed.category,
                        bankSource = parsed.bankName,
                        description = parsed.description,
                        isSalary = parsed.isSalary
                    )
                } catch (_: Exception) {}
            }
        }
    }
}
