package com.example.thuchanhandroid.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateTimeUtils {

    private const val DATE_PATTERN = "yyyy-MM-dd"
    private const val TIME_PATTERN = "HH:mm"

    fun today(): String = SimpleDateFormat(DATE_PATTERN, Locale.getDefault()).format(Date())

    fun nowTime(): String = SimpleDateFormat(TIME_PATTERN, Locale.getDefault()).format(Date())

    fun currentMonth(): Int = Calendar.getInstance().get(Calendar.MONTH) + 1

    fun currentYear(): Int = Calendar.getInstance().get(Calendar.YEAR)

    fun hoursBetween(startTime: String, endTime: String): Double {
        val format = SimpleDateFormat(TIME_PATTERN, Locale.getDefault())
        val start = format.parse(startTime) ?: return 0.0
        val end = format.parse(endTime) ?: return 0.0
        val millis = end.time - start.time
        return if (millis <= 0) 0.0 else millis / 3_600_000.0
    }

    fun monthRange(month: Int, year: Int): Pair<String, String> {
        val from = String.format(Locale.US, "%04d-%02d-01", year, month)
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, 1)
        val lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val to = String.format(Locale.US, "%04d-%02d-%02d", year, month, lastDay)
        return from to to
    }
}
