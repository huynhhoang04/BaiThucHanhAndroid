package com.example.thuchanhandroid.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class SalaryCalculatorTest {

    @Test
    fun testZeroHoursReturnsZeroSalary() {
        val total = SalaryCalculator.calculateTotalSalary(
            totalHours = 0.0,
            hourlyRate = 50000.0,
            overtimeHours = 0.0
        )
        assertEquals(0.0, total, 0.01)
    }

    @Test
    fun testRegularHoursWithoutOvertime() {
        // 160h * 50,000 VND = 8,000,000 VND
        val total = SalaryCalculator.calculateTotalSalary(
            totalHours = 160.0,
            hourlyRate = 50000.0,
            overtimeHours = 0.0
        )
        assertEquals(8000000.0, total, 0.01)
    }

    @Test
    fun testOvertimeCalculation() {
        // Total 186h (176h regular, 10h overtime)
        // Regular: 176 * 50,000 = 8,800,000 VND
        // Overtime: 10 * 50,000 * 1.5 = 750,000 VND
        // Total: 9,550,000 VND
        val ot = SalaryCalculator.calculateOvertimeHours(186.0)
        assertEquals(10.0, ot, 0.01)

        val total = SalaryCalculator.calculateTotalSalary(
            totalHours = 186.0,
            hourlyRate = 50000.0,
            overtimeHours = ot
        )
        assertEquals(9550000.0, total, 0.01)
    }

    @Test
    fun testNegativeOrEdgeInputsHandledSafely() {
        val total = SalaryCalculator.calculateTotalSalary(
            totalHours = -10.0,
            hourlyRate = -50000.0,
            overtimeHours = -5.0
        )
        assertEquals(0.0, total, 0.01)
    }
}
