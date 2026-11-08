package com.example.core.utils

import java.text.NumberFormat
import java.util.Locale

object FormatUtils {
    private val indonesianLocale = Locale("id", "ID")

    fun formatRupiah(amount: Long): String {
        val formatter = NumberFormat.getNumberInstance(indonesianLocale)
        return "Rp " + formatter.format(amount)
    }

    fun formatDuration(minutes: Int): String {
        if (minutes <= 0) return "0m"
        val hours = minutes / 60
        val remainingMinutes = minutes % 60
        return when {
            hours > 0 && remainingMinutes > 0 -> "${hours}j ${remainingMinutes}m"
            hours > 0 -> "${hours} jam"
            else -> "${remainingMinutes} menit"
        }
    }

    fun formatSecondsToTimer(totalSeconds: Int): String {
        val m = totalSeconds / 60
        val s = totalSeconds % 60
        return String.format(Locale.US, "%02d:%02d", m, s)
    }
}
