package com.example.thuchanhandroid.utils

import java.text.NumberFormat
import java.util.Locale

/**
 * Utility thuan Kotlin tu dong tinh toan tien luong hang thang.
 */
object SalaryCalculator {

    const val OVERTIME_MULTIPLIER = 1.5
    const val STANDARD_MONTHLY_HOURS = 176.0

    /**
     * Tinh so gio tang ca tu tong so gio lam trong thang.
     * Neu tong so gio > 176h, phan vuot qua 176h la overtime.
     */
    fun calculateOvertimeHours(totalHours: Double, threshold: Double = STANDARD_MONTHLY_HOURS): Double {
        if (totalHours <= threshold) return 0.0
        return totalHours - threshold
    }

    /**
     * Tinh tong luong hang thang dua tren formula:
     * totalSalary = regularHours * hourlyRate + overtimeHours * hourlyRate * 1.5
     * voi regularHours = totalHours - overtimeHours
     */
    fun calculateTotalSalary(
        totalHours: Double,
        hourlyRate: Double,
        overtimeHours: Double = 0.0
    ): Double {
        val safeTotalHours = maxOf(0.0, totalHours)
        val safeHourlyRate = maxOf(0.0, hourlyRate)
        val safeOvertimeHours = maxOf(0.0, minOf(safeTotalHours, overtimeHours))

        val regularHours = safeTotalHours - safeOvertimeHours
        val regularPay = regularHours * safeHourlyRate
        val overtimePay = safeOvertimeHours * safeHourlyRate * OVERTIME_MULTIPLIER

        return regularPay + overtimePay
    }

    /**
     * Dinh dang so tien thanh chuoi tien te VND (vi du: 15.000.000 ₫)
     */
    fun formatVnd(amount: Double): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale.Builder().setLanguage("vi").setRegion("VN").build())
        return formatter.format(amount)
    }
}
