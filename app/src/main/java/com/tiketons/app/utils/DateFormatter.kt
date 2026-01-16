package com.tiketons.app.utils

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

object DateFormatter {
    // Input: "2025-12-30T10:00:00" (Format ISO dari Database)
    // Output: "30 Des 2025"
    fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
            val date = inputFormat.parse(dateString)
            outputFormat.format(date ?: return dateString)
        } catch (e: Exception) {
            dateString // Jika gagal parsing, kembalikan string asli
        }
    }

    // Output: "19:00 WIB"
    fun formatTime(timeString: String): String {
        return try {
            // Asumsi format time dari DB "19:00:00"
            val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val date = inputFormat.parse(timeString)
            "${outputFormat.format(date)} WIB"
        } catch (e: Exception) {
            timeString
        }
    }
}