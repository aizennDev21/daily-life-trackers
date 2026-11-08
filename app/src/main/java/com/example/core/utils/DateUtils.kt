package com.example.core.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
    private val dateIdFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val fullDateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val indonesianDateFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID"))
    private val shortIndonesianDateFormat = SimpleDateFormat("d MMM yyyy", Locale("id", "ID"))
    private val dayMonthFormat = SimpleDateFormat("d MMM", Locale("id", "ID"))

    fun todayId(): String = dateIdFormat.format(Date())

    fun formatDateId(date: Date): String = dateIdFormat.format(date)

    fun formatTime(timestamp: Long): String = timeFormat.format(Date(timestamp))

    fun formatFullDateIndonesian(date: Date = Date()): String = indonesianDateFormat.format(date)

    fun formatShortDate(timestamp: Long): String = shortIndonesianDateFormat.format(Date(timestamp))

    fun formatDayMonth(timestamp: Long): String = dayMonthFormat.format(Date(timestamp))

    fun getGreeting(hour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)): String {
        return when (hour) {
            in 4..10 -> "Selamat Pagi"
            in 11..14 -> "Selamat Siang"
            in 15..18 -> "Selamat Sore"
            else -> "Selamat Malam"
        }
    }

    fun getGreetingEmoji(hour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)): String {
        return when (hour) {
            in 4..10 -> "🌅"
            in 11..14 -> "☀️"
            in 15..18 -> "🌇"
            else -> "🌙"
        }
    }

    fun getPastDays(count: Int): List<String> {
        val list = mutableListOf<String>()
        val cal = Calendar.getInstance()
        for (i in 0 until count) {
            list.add(dateIdFormat.format(cal.time))
            cal.add(Calendar.DAY_OF_YEAR, -1)
        }
        return list
    }

    fun getDayOfWeekShort(dateStr: String): String {
        return try {
            val date = dateIdFormat.parse(dateStr) ?: return ""
            val dayFormat = SimpleDateFormat("EEE", Locale("id", "ID"))
            dayFormat.format(date)
        } catch (e: Exception) {
            ""
        }
    }
}
