package com.example

import com.example.service.BankNotificationListenerService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testBankNotificationParsing_salaryIncome_allocatesSavings() {
        val title = "MB Bank Biến Động Số Dư"
        val body = "Tài khoản nhận được +25.000.000 VND từ CTY TECH. ND: Chuyen luong thang 11"
        val parsed = BankNotificationListenerService.parseBankMessage(title, body, "com.mbmobile")

        assertNotNull(parsed)
        assertEquals("MB Bank", parsed?.bankName)
        assertEquals(25000000L, parsed?.amount)
        assertEquals("INCOME", parsed?.type)
        assertTrue(parsed?.isSalary == true)
        assertEquals("Lương", parsed?.category)
    }

    @Test
    fun testBankNotificationParsing_expense() {
        val title = "TNEX"
        val body = "Thanh toan thanh cong -150.000 VND tai Highlands Coffee"
        val parsed = BankNotificationListenerService.parseBankMessage(title, body, "com.msb.tnex")

        assertNotNull(parsed)
        assertEquals("TNEX", parsed?.bankName)
        assertEquals(150000L, parsed?.amount)
        assertEquals("EXPENSE", parsed?.type)
    }

    @Test
    fun test90DayProgressCalculation() {
        val currentDay = 45
        val totalDays = 90
        val progress = currentDay / totalDays.toFloat()
        assertEquals(0.5f, progress, 0.001f)
    }
}
