package com.it2161.movieviewer.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private const val API_DATE_FORMAT = "yyyy-MM-dd"
    private const val DISPLAY_DATE_FORMAT = "MMM dd, yyyy"
    
    fun formatDate(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return "N/A"
        
        return try {
            val apiFormat = SimpleDateFormat(API_DATE_FORMAT, Locale.US)
            val displayFormat = SimpleDateFormat(DISPLAY_DATE_FORMAT, Locale.US)
            val date = apiFormat.parse(dateString)
            date?.let { displayFormat.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }
    
    fun formatRuntime(minutes: Int?): String {
        if (minutes == null || minutes <= 0) return "N/A"
        val hours = minutes / 60
        val mins = minutes % 60
        return if (hours > 0) {
            "${hours}h ${mins}m"
        } else {
            "${mins}m"
        }
    }
    
    fun formatRevenue(revenue: Long?): String {
        if (revenue == null || revenue <= 0) return "N/A"
        
        return when {
            revenue >= 1_000_000_000 -> String.format("$%.2fB", revenue / 1_000_000_000.0)
            revenue >= 1_000_000 -> String.format("$%.2fM", revenue / 1_000_000.0)
            revenue >= 1_000 -> String.format("$%.2fK", revenue / 1_000.0)
            else -> "$$revenue"
        }
    }
}
