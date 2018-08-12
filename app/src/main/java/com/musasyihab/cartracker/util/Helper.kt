package com.musasyihab.cartracker.util

import java.text.SimpleDateFormat
import java.util.Date

object Helper {
    fun convertDateToString(dateFormat: SimpleDateFormat, date: Date?): String {
        return if (date != null) {
            dateFormat.format(date)
        } else ""
    }

    fun getDateFromString(dateFormat: SimpleDateFormat, date: String): Date? {
        val formatted: String
        val formatDate: Date
        try {
            val dateStr = dateFormat.parse(date)
            formatted = dateFormat.format(dateStr)
            formatDate = dateFormat.parse(formatted)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

        return formatDate
    }
}
