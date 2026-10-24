package com.example.thuchanhandroid

import com.example.thuchanhandroid.utils.DateTimeUtils
import org.junit.Assert.assertEquals
import org.junit.Test

class DateTimeUtilsTest {

    @Test
    fun hoursBetween_normalShift_returns8Hours() {
        assertEquals(8.0, DateTimeUtils.hoursBetween("08:00", "16:00"), 0.001)
    }

    @Test
    fun hoursBetween_endBeforeStart_returnsZero() {
        assertEquals(0.0, DateTimeUtils.hoursBetween("17:00", "08:00"), 0.001)
    }

    @Test
    fun monthRange_january_has31Days() {
        val (from, to) = DateTimeUtils.monthRange(1, 2026)
        assertEquals("2026-01-01", from)
        assertEquals("2026-01-31", to)
    }

    @Test
    fun monthRange_februaryLeapYear_has29Days() {
        val (from, to) = DateTimeUtils.monthRange(2, 2024)
        assertEquals("2024-02-01", from)
        assertEquals("2024-02-29", to)
    }
}
